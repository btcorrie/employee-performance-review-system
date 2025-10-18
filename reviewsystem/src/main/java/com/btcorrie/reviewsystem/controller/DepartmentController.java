package com.btcorrie.reviewsystem.controller;

import com.btcorrie.reviewsystem.dto.DepartmentCreateRequest;
import com.btcorrie.reviewsystem.dto.DepartmentResponse;
import com.btcorrie.reviewsystem.dto.DepartmentUpdateRequest;
import com.btcorrie.reviewsystem.service.DepartmentService;
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

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // Create new department
    @PostMapping
    public ResponseEntity<?> createDepartment(@Valid @RequestBody DepartmentCreateRequest request) {
        try {
            DepartmentResponse response = departmentService.createDepartment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Get department by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        try {
            DepartmentResponse response = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    // Get all departments with pagination
    @GetMapping
    public ResponseEntity<Page<DepartmentResponse>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DepartmentResponse> departments = departmentService.getAllDepartments(pageable);

        return ResponseEntity.ok(departments);
    }

    // Get departments by organization
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<DepartmentResponse>> getDepartmentsByOrganization(@PathVariable Long organizationId) {
        List<DepartmentResponse> departments = departmentService.getDepartmentsByOrganization(organizationId);
        return ResponseEntity.ok(departments);
    }

    // Get active departments only
    @GetMapping("/active")
    public ResponseEntity<List<DepartmentResponse>> getActiveDepartments() {
        List<DepartmentResponse> departments = departmentService.getActiveDepartments();
        return ResponseEntity.ok(departments);
    }

    // Update department
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id,
                                              @Valid @RequestBody DepartmentUpdateRequest request) {
        try {
            DepartmentResponse response = departmentService.updateDepartment(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Remove manager from department
    @PatchMapping("/{id}/remove-manager")
    public ResponseEntity<?> removeManager(@PathVariable Long id) {
        try {
            DepartmentResponse response = departmentService.removeManager(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    // Deactivate department (soft delete)
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateDepartment(@PathVariable Long id) {
        try {
            departmentService.deactivateDepartment(id);
            return ResponseEntity.ok().body("Department deactivated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    // Delete department (hard delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok().body("Department deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Search departments by name
    @GetMapping("/search")
    public ResponseEntity<List<DepartmentResponse>> searchDepartments(@RequestParam String name) {
        List<DepartmentResponse> departments = departmentService.searchDepartmentsByName(name);
        return ResponseEntity.ok(departments);
    }

    // Test endpoint for departments
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Department endpoint is working!");
    }
}
