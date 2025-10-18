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
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentCreateRequest request) {
        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        DepartmentResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<DepartmentResponse>> getDepartmentsByOrganization(@PathVariable Long organizationId) {
        List<DepartmentResponse> departments = departmentService.getDepartmentsByOrganization(organizationId);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DepartmentResponse>> getActiveDepartments() {
        List<DepartmentResponse> departments = departmentService.getActiveDepartments();
        return ResponseEntity.ok(departments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id,
                                                               @Valid @RequestBody DepartmentUpdateRequest request) {
        DepartmentResponse response = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/remove-manager")
    public ResponseEntity<DepartmentResponse> removeManager(@PathVariable Long id) {
        DepartmentResponse response = departmentService.removeManager(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateDepartment(@PathVariable Long id) {
        departmentService.deactivateDepartment(id);
        return ResponseEntity.ok(Map.of("message", "Department deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(Map.of("message", "Department deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DepartmentResponse>> searchDepartments(@RequestParam String name) {
        List<DepartmentResponse> departments = departmentService.searchDepartmentsByName(name);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Department endpoint is working!");
    }
}
