package com.btcorrie.reviewsystem.service;

import com.btcorrie.reviewsystem.dto.DepartmentCreateRequest;
import com.btcorrie.reviewsystem.dto.DepartmentResponse;
import com.btcorrie.reviewsystem.dto.DepartmentUpdateRequest;
import com.btcorrie.reviewsystem.model.Department;
import com.btcorrie.reviewsystem.model.Organization;
import com.btcorrie.reviewsystem.model.User;
import com.btcorrie.reviewsystem.repository.DepartmentRepository;
import com.btcorrie.reviewsystem.repository.OrganizationRepository;
import com.btcorrie.reviewsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    // Create new department
    public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
        // Validate organization exists
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + request.getOrganizationId()));

        // Check if department name already exists within the organization
        if (departmentRepository.existsByNameAndOrganization(request.getName(), organization)) {
            throw new RuntimeException("Department with name '" + request.getName() +
                    "' already exists in organization '" + organization.getName() + "'");
        }

        // Validate manager if provided
        User manager = null;
        if (request.getManagerId() != null) {
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + request.getManagerId()));

            // Validate manager is eligible (MANAGER, HR_ADMIN, or SYSTEM_ADMIN role)
            if (!manager.isManager()) {
                throw new RuntimeException("User with role '" + manager.getRole() + "' cannot be assigned as department manager");
            }
        }

        // Create new department
        Department department = new Department();
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setOrganization(organization);
        department.setManager(manager);
        department.setActive(true);

        // Save to database
        Department savedDepartment = departmentRepository.save(department);

        return convertToResponse(savedDepartment);
    }

    // Get department by ID
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        return convertToDetailedResponse(department);
    }

    // Get all departments with pagination
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable) {
        Page<Department> departments = departmentRepository.findAll(pageable);

        List<DepartmentResponse> responses = departments.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, departments.getTotalElements());
    }

    // Get departments by organization
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getDepartmentsByOrganization(Long organizationId) {
        List<Department> departments = departmentRepository.findByOrganizationId(organizationId);

        return departments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get active departments only
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getActiveDepartments() {
        List<Department> departments = departmentRepository.findByActiveTrue();

        return departments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Update department
    public DepartmentResponse updateDepartment(Long id, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Check if new name conflicts within the same organization
        if (request.getName() != null && !request.getName().equals(department.getName())) {
            if (departmentRepository.existsByNameAndOrganization(request.getName(), department.getOrganization())) {
                throw new RuntimeException("Department with name '" + request.getName() +
                        "' already exists in organization '" + department.getOrganization().getName() + "'");
            }
            department.setName(request.getName());
        }

        // Update description if provided
        if (request.getDescription() != null) {
            department.setDescription(request.getDescription());
        }

        // Update active status if provided
        if (request.getActive() != null) {
            department.setActive(request.getActive());
        }

        // Update manager if provided
        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + request.getManagerId()));

            if (!manager.isManager()) {
                throw new RuntimeException("User with role '" + manager.getRole() + "' cannot be assigned as department manager");
            }

            department.setManager(manager);
        }

        // Save updated department
        Department updatedDepartment = departmentRepository.save(department);

        return convertToResponse(updatedDepartment);
    }

    // Remove manager from department
    public DepartmentResponse removeManager(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        department.setManager(null);
        Department updatedDepartment = departmentRepository.save(department);

        return convertToResponse(updatedDepartment);
    }

    // Soft delete department (deactivate)
    public void deactivateDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        department.setActive(false);
        departmentRepository.save(department);
    }

    // Hard delete department (only if no users assigned)
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Check if department has users
        if (department.getUsers() != null && !department.getUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete department with assigned users. " +
                    "Please reassign all users first or use deactivate instead.");
        }

        departmentRepository.delete(department);
    }

    // Search departments by name
    @Transactional(readOnly = true)
    public List<DepartmentResponse> searchDepartmentsByName(String name) {
        List<Department> departments = departmentRepository.findByNameContainingIgnoreCase(name);

        return departments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Convert Department entity to basic response DTO
    private DepartmentResponse convertToResponse(Department department) {
        // Organization summary
        DepartmentResponse.OrganizationSummary orgSummary = new DepartmentResponse.OrganizationSummary(
                department.getOrganization().getId(),
                department.getOrganization().getName()
        );

        // Manager summary
        DepartmentResponse.UserSummary managerSummary = null;
        if (department.getManager() != null) {
            User manager = department.getManager();
            managerSummary = new DepartmentResponse.UserSummary(
                    manager.getId(),
                    manager.getUsername(),
                    manager.getFirstName(),
                    manager.getLastName(),
                    manager.getFullName(),
                    manager.getRole().name(),
                    manager.getActive()
            );
        }

        int userCount = department.getUsers() != null ? department.getUsers().size() : 0;

        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getActive(),
                department.getCreatedAt(),
                department.getUpdatedAt(),
                orgSummary,
                managerSummary,
                userCount
        );
    }

    // Convert Department entity to detailed response DTO (with user list)
    private DepartmentResponse convertToDetailedResponse(Department department) {
        DepartmentResponse response = convertToResponse(department);

        // Add user summaries if users exist
        if (department.getUsers() != null && !department.getUsers().isEmpty()) {
            List<DepartmentResponse.UserSummary> userSummaries = department.getUsers().stream()
                    .map(user -> new DepartmentResponse.UserSummary(
                            user.getId(),
                            user.getUsername(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getFullName(),
                            user.getRole().name(),
                            user.getActive()
                    ))
                    .collect(Collectors.toList());

            response.setUsers(userSummaries);
        }

        return response;
    }
}
