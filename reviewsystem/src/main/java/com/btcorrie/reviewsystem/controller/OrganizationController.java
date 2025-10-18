package com.btcorrie.reviewsystem.controller;

import com.btcorrie.reviewsystem.dto.OrganizationCreateRequest;
import com.btcorrie.reviewsystem.dto.OrganizationResponse;
import com.btcorrie.reviewsystem.dto.OrganizationUpdateRequest;
import com.btcorrie.reviewsystem.service.OrganizationService;
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
@RequestMapping("/api/organizations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    // Create new organization
    @PostMapping
    public ResponseEntity<?> createOrganization(@Valid @RequestBody OrganizationCreateRequest request) {
        try {
            OrganizationResponse response = organizationService.createOrganization(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Get organization by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrganizationById(@PathVariable Long id) {
        try {
            OrganizationResponse response = organizationService.getOrganizationById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    // Get all organizations with pagination
    @GetMapping
    public ResponseEntity<Page<OrganizationResponse>> getAllOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrganizationResponse> organizations = organizationService.getAllOrganizations(pageable);

        return ResponseEntity.ok(organizations);
    }

    // Get active organizations only
    @GetMapping("/active")
    public ResponseEntity<List<OrganizationResponse>> getActiveOrganizations() {
        List<OrganizationResponse> organizations = organizationService.getActiveOrganizations();
        return ResponseEntity.ok(organizations);
    }

    // Update organization
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrganization(@PathVariable Long id,
                                                @Valid @RequestBody OrganizationUpdateRequest request) {
        try {
            OrganizationResponse response = organizationService.updateOrganization(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Deactivate organization (soft delete)
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateOrganization(@PathVariable Long id) {
        try {
            organizationService.deactivateOrganization(id);
            return ResponseEntity.ok().body("Organization deactivated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    // Delete organization (hard delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrganization(@PathVariable Long id) {
        try {
            organizationService.deleteOrganization(id);
            return ResponseEntity.ok().body("Organization deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Search organizations by name
    @GetMapping("/search")
    public ResponseEntity<List<OrganizationResponse>> searchOrganizations(@RequestParam String name) {
        List<OrganizationResponse> organizations = organizationService.searchOrganizationsByName(name);
        return ResponseEntity.ok(organizations);
    }

    // Test endpoint for organizations
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Organization endpoint is working!");
    }
}
