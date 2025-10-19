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
import java.util.Map;

@RestController
@RequestMapping("/api/organizations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(@Valid @RequestBody OrganizationCreateRequest request) {
        OrganizationResponse response = organizationService.createOrganization(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getOrganizationById(@PathVariable Long id) {
        OrganizationResponse response = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/active")
    public ResponseEntity<List<OrganizationResponse>> getActiveOrganizations() {
        List<OrganizationResponse> organizations = organizationService.getActiveOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> updateOrganization(@PathVariable Long id,
                                                                   @Valid @RequestBody OrganizationUpdateRequest request) {
        OrganizationResponse response = organizationService.updateOrganization(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateOrganization(@PathVariable Long id) {
        organizationService.deactivateOrganization(id);
        return ResponseEntity.ok(Map.of("message", "Organization deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.ok(Map.of("message", "Organization deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrganizationResponse>> searchOrganizations(@RequestParam String name) {
        List<OrganizationResponse> organizations = organizationService.searchOrganizationsByName(name);
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Organization endpoint is working!");
    }
}
