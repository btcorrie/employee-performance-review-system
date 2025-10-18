package com.btcorrie.reviewsystem.controller;

import com.btcorrie.reviewsystem.dto.UserCreateRequest;
import com.btcorrie.reviewsystem.dto.UserResponse;
import com.btcorrie.reviewsystem.dto.UserUpdateRequest;
import com.btcorrie.reviewsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponse> users = userService.getAllUsers(pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        UserResponse response = userService.getCurrentUserProfile();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateOwnProfile(@Valid @RequestBody UserUpdateRequest request) {
        UserResponse currentUser = userService.getCurrentUserProfile();
        UserResponse response = userService.updateOwnProfile(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-department")
    public ResponseEntity<List<UserResponse>> getUsersInMyDepartments() {
        List<UserResponse> users = userService.getUsersInMyDepartments();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/my-reports")
    public ResponseEntity<List<UserResponse>> getMyDirectReports() {
        List<UserResponse> users = userService.getMyDirectReports();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("User endpoint is working!");
    }
}
