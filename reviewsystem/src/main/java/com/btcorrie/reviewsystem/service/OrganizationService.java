package com.btcorrie.reviewsystem.service;

import com.btcorrie.reviewsystem.dto.OrganizationCreateRequest;
import com.btcorrie.reviewsystem.dto.OrganizationResponse;
import com.btcorrie.reviewsystem.dto.OrganizationUpdateRequest;
import com.btcorrie.reviewsystem.model.Organization;
import com.btcorrie.reviewsystem.repository.OrganizationRepository;
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
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    // Create new organization
    public OrganizationResponse createOrganization(OrganizationCreateRequest request) {
        // Check if organization name already exists
        if (organizationRepository.existsByName(request.getName())) {
            throw new RuntimeException("Organization with name '" + request.getName() + "' already exists");
        }

        // Create new organization
        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setDescription(request.getDescription());
        organization.setActive(true);

        // Save to database
        Organization savedOrganization = organizationRepository.save(organization);

        // Convert to response DTO
        return convertToResponse(savedOrganization);
    }

    // Get organization by ID
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + id));

        return convertToDetailedResponse(organization);
    }

    // Get all organizations with pagination
    @Transactional(readOnly = true)
    public Page<OrganizationResponse> getAllOrganizations(Pageable pageable) {
        Page<Organization> organizations = organizationRepository.findAll(pageable);

        List<OrganizationResponse> responses = organizations.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, organizations.getTotalElements());
    }

    // Get active organizations only
    @Transactional(readOnly = true)
    public List<OrganizationResponse> getActiveOrganizations() {
        List<Organization> organizations = organizationRepository.findByActiveTrue();

        return organizations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Update organization
    public OrganizationResponse updateOrganization(Long id, OrganizationUpdateRequest request) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + id));

        // Check if new name conflicts with existing organization
        if (request.getName() != null && !request.getName().equals(organization.getName())) {
            if (organizationRepository.existsByName(request.getName())) {
                throw new RuntimeException("Organization with name '" + request.getName() + "' already exists");
            }
            organization.setName(request.getName());
        }

        // Update fields if provided
        if (request.getDescription() != null) {
            organization.setDescription(request.getDescription());
        }

        if (request.getActive() != null) {
            organization.setActive(request.getActive());
        }

        // Save updated organization
        Organization updatedOrganization = organizationRepository.save(organization);

        return convertToResponse(updatedOrganization);
    }

    // Soft delete organization (deactivate)
    public void deactivateOrganization(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + id));

        organization.setActive(false);
        organizationRepository.save(organization);
    }

    // Hard delete organization (only if no departments exist)
    public void deleteOrganization(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + id));

        // Check if organization has departments
        if (organization.getDepartments() != null && !organization.getDepartments().isEmpty()) {
            throw new RuntimeException("Cannot delete organization with existing departments. " +
                    "Please remove all departments first or use deactivate instead.");
        }

        organizationRepository.delete(organization);
    }

    // Search organizations by name
    @Transactional(readOnly = true)
    public List<OrganizationResponse> searchOrganizationsByName(String name) {
        List<Organization> organizations = organizationRepository.findByNameContainingIgnoreCase(name);

        return organizations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Convert Organization entity to basic response DTO
    private OrganizationResponse convertToResponse(Organization organization) {
        int departmentCount = organization.getDepartments() != null ? organization.getDepartments().size() : 0;

        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getDescription(),
                organization.getActive(),
                organization.getCreatedAt(),
                organization.getUpdatedAt(),
                departmentCount
        );
    }

    // Convert Organization entity to detailed response DTO (with department summaries)
    private OrganizationResponse convertToDetailedResponse(Organization organization) {
        OrganizationResponse response = convertToResponse(organization);

        // Add department summaries if departments exist
        if (organization.getDepartments() != null && !organization.getDepartments().isEmpty()) {
            List<OrganizationResponse.DepartmentSummary> departmentSummaries = organization.getDepartments().stream()
                    .map(dept -> new OrganizationResponse.DepartmentSummary(
                            dept.getId(),
                            dept.getName(),
                            dept.getActive(),
                            dept.getUsers() != null ? dept.getUsers().size() : 0,
                            dept.getManager() != null ? dept.getManager().getFullName() : null
                    ))
                    .collect(Collectors.toList());

            response.setDepartments(departmentSummaries);
        }

        return response;
    }
}
