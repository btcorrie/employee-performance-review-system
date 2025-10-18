package com.btcorrie.reviewsystem.service;

import com.btcorrie.reviewsystem.dto.UserCreateRequest;
import com.btcorrie.reviewsystem.dto.UserResponse;
import com.btcorrie.reviewsystem.dto.UserUpdateRequest;
import com.btcorrie.reviewsystem.model.Department;
import com.btcorrie.reviewsystem.model.User;
import com.btcorrie.reviewsystem.repository.DepartmentRepository;
import com.btcorrie.reviewsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create user - Only HR_ADMIN and SYSTEM_ADMIN can create users
    @PreAuthorize("hasRole('HR_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public UserResponse createUser(UserCreateRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());
        user.setActive(true);

        // Set department if provided
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));
            user.setDepartment(department);
        }

        // Set manager if provided
        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + request.getManagerId()));

            if (!manager.isManager()) {
                throw new RuntimeException("Selected user cannot be a manager (insufficient role)");
            }
            user.setManager(manager);
        }

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    // Get all users - Only HR_ADMIN and SYSTEM_ADMIN can see all users
    @PreAuthorize("hasRole('HR_ADMIN') or hasRole('SYSTEM_ADMIN')")
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        List<UserResponse> responses = users.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, users.getTotalElements());
    }

    // Get user by ID - Users can see their own profile, managers can see direct reports, admins can see anyone
    @PreAuthorize("#userId == authentication.principal.id or @userService.isMyDirectReport(#userId) or hasRole('HR_ADMIN') or hasRole('SYSTEM_ADMIN')")
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return convertToDetailedResponse(user);
    }

    // Get current user's profile - Anyone can get their own profile
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        return convertToDetailedResponse(user);
    }

    // Update user - Only HR_ADMIN and SYSTEM_ADMIN can update any user
    @PreAuthorize("hasRole('HR_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return updateUserInternal(user, request);
    }

    // Update own profile - Users can update their own basic info (not role/department)
    @PreAuthorize("#userId == authentication.principal.id")
    public UserResponse updateOwnProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // For self-updates, ignore role and department changes
        UserUpdateRequest limitedRequest = new UserUpdateRequest();
        limitedRequest.setUsername(request.getUsername());
        limitedRequest.setEmail(request.getEmail());
        limitedRequest.setFirstName(request.getFirstName());
        limitedRequest.setLastName(request.getLastName());
        // Don't allow self-update of role, active status, department, or manager

        return updateUserInternal(user, limitedRequest);
    }

    // Get users in my department - Managers can see users in their managed departments
    @PreAuthorize("hasRole('MANAGER') or hasRole('HR_ADMIN') or hasRole('SYSTEM_ADMIN')")
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersInMyDepartments() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // If user is HR_ADMIN or SYSTEM_ADMIN, return all users
        if (currentUser.getRole() == User.Role.HR_ADMIN || currentUser.getRole() == User.Role.SYSTEM_ADMIN) {
            List<User> allUsers = userRepository.findAll();
            return allUsers.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }

        // If user is a manager, return users from departments they manage
        if (currentUser.getManagedDepartments() != null && !currentUser.getManagedDepartments().isEmpty()) {
            List<User> departmentUsers = currentUser.getManagedDepartments().stream()
                    .flatMap(dept -> dept.getUsers().stream())
                    .distinct()
                    .collect(Collectors.toList());

            return departmentUsers.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }

        return List.of(); // No managed departments
    }

    // Get direct reports - Managers can see their direct reports
    @PreAuthorize("hasRole('MANAGER') or hasRole('HR_ADMIN') or hasRole('SYSTEM_ADMIN')")
    @Transactional(readOnly = true)
    public List<UserResponse> getMyDirectReports() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        List<User> directReports = userRepository.findByManagerId(currentUser.getId());

        return directReports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Deactivate user - Only SYSTEM_ADMIN can deactivate users
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setActive(false);
        userRepository.save(user);
    }

    // Delete user - Only SYSTEM_ADMIN can delete users
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if user has direct reports
        if (user.getDirectReports() != null && !user.getDirectReports().isEmpty()) {
            throw new RuntimeException("Cannot delete user who manages other employees. " +
                    "Please reassign direct reports first.");
        }

        userRepository.delete(user);
    }

    // CUSTOM SECURITY METHODS

    // Check if a user is a direct report of the current user
    public boolean isMyDirectReport(Long userId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User targetUser = userRepository.findById(userId)
                .orElse(null);

        if (targetUser == null) {
            return false;
        }

        // Check if current user is the manager of target user
        return targetUser.getManager() != null &&
                targetUser.getManager().getId().equals(currentUser.getId());
    }

    // Check if a user is in a department managed by the current user
    public boolean isUserInMyDepartment(Long userId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User targetUser = userRepository.findById(userId)
                .orElse(null);

        if (targetUser == null || targetUser.getDepartment() == null) {
            return false;
        }

        // Check if current user manages the department of target user
        return currentUser.getManagedDepartments() != null &&
                currentUser.getManagedDepartments().stream()
                        .anyMatch(dept -> dept.getId().equals(targetUser.getDepartment().getId()));
    }

    // PRIVATE HELPER METHODS

    private UserResponse updateUserInternal(User user, UserUpdateRequest request) {
        // Update username if provided and not duplicate
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username is already taken!");
            }
            user.setUsername(request.getUsername());
        }

        // Update email if provided and not duplicate
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email is already in use!");
            }
            user.setEmail(request.getEmail());
        }

        // Update other fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        // Update department if provided
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));
            user.setDepartment(department);
        }

        // Update manager if provided
        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + request.getManagerId()));

            if (!manager.isManager()) {
                throw new RuntimeException("Selected user cannot be a manager (insufficient role)");
            }
            user.setManager(manager);
        }

        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    private UserResponse convertToResponse(User user) {
        int directReportsCount = user.getDirectReports() != null ? user.getDirectReports().size() : 0;

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getRole().name(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                directReportsCount
        );
    }

    private UserResponse convertToDetailedResponse(User user) {
        UserResponse response = convertToResponse(user);

        // Add department info
        if (user.getDepartment() != null) {
            UserResponse.DepartmentSummary deptSummary = new UserResponse.DepartmentSummary(
                    user.getDepartment().getId(),
                    user.getDepartment().getName(),
                    user.getDepartment().getOrganization().getName()
            );
            response.setDepartment(deptSummary);
        }

        // Add manager info
        if (user.getManager() != null) {
            UserResponse.UserSummary managerSummary = new UserResponse.UserSummary(
                    user.getManager().getId(),
                    user.getManager().getUsername(),
                    user.getManager().getFullName(),
                    user.getManager().getRole().name()
            );
            response.setManager(managerSummary);
        }

        return response;
    }
}
