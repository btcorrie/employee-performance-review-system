package com.btcorrie.reviewsystem.repository;

import com.btcorrie.reviewsystem.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    // Find organization by name
    Optional<Organization> findByName(String name);

    // Check if organization name exists
    Boolean existsByName(String name);

    // Find all active organizations
    List<Organization> findByActiveTrue();

    // Find all inactive organizations
    List<Organization> findByActiveFalse();

    // Custom query to find organizations with department count
    @Query("SELECT o FROM Organization o LEFT JOIN FETCH o.departments WHERE o.active = true")
    List<Organization> findActiveOrganizationsWithDepartments();

    // Find organizations by name containing (case-insensitive search)
    List<Organization> findByNameContainingIgnoreCase(String name);
}
