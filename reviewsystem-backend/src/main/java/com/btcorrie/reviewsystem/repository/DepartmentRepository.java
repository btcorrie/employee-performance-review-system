package com.btcorrie.reviewsystem.repository;

import com.btcorrie.reviewsystem.model.Department;
import com.btcorrie.reviewsystem.model.Organization;
import com.btcorrie.reviewsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // Find department by name within an organization
    Optional<Department> findByNameAndOrganization(String name, Organization organization);

    // Check if department name exists within an organization
    Boolean existsByNameAndOrganization(String name, Organization organization);

    // Find all departments by organization
    List<Department> findByOrganization(Organization organization);

    // Find all active departments by organization
    List<Department> findByOrganizationAndActiveTrue(Organization organization);

    // Find all departments by organization ID
    List<Department> findByOrganizationId(Long organizationId);

    // Find departments managed by a specific user
    List<Department> findByManager(User manager);

    // Find all active departments
    List<Department> findByActiveTrue();

    // Custom query to find departments with user count
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.users WHERE d.organization.id = :organizationId AND d.active = true")
    List<Department> findActiveDepartmentsByOrganizationWithUsers(@Param("organizationId") Long organizationId);

    // Find departments by name containing (case-insensitive search)
    List<Department> findByNameContainingIgnoreCase(String name);

    // Find departments without a manager
    List<Department> findByManagerIsNull();
}
