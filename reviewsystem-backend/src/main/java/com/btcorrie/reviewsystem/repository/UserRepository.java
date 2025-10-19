package com.btcorrie.reviewsystem.repository;

import com.btcorrie.reviewsystem.model.Department;
import com.btcorrie.reviewsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Existing methods
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // NEW METHODS FOR RELATIONSHIPS

    // Find users by department
    List<User> findByDepartment(Department department);

    // Find users by department ID
    List<User> findByDepartmentId(Long departmentId);

    // Find users by role
    List<User> findByRole(User.Role role);

    // Find active users by role
    List<User> findByRoleAndActiveTrue(User.Role role);

    // Find direct reports of a manager
    List<User> findByManager(User manager);

    // Find users by manager ID
    List<User> findByManagerId(Long managerId);

    // Find all active users
    List<User> findByActiveTrue();

    // Find users without a department
    List<User> findByDepartmentIsNull();

    // Find users without a manager
    List<User> findByManagerIsNull();

    // Custom query to find all managers (users who have direct reports)
    @Query("SELECT DISTINCT u FROM User u WHERE u.id IN (SELECT m.id FROM User m WHERE m.directReports IS NOT EMPTY)")
    List<User> findAllManagers();

    // Find users by department and role
    List<User> findByDepartmentAndRole(Department department, User.Role role);

    // Search users by name (first or last name containing)
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContaining(@Param("name") String name);

    // Paginated search for all users with filters
    @Query("SELECT u FROM User u WHERE " +
            "(:departmentId IS NULL OR u.department.id = :departmentId) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:active IS NULL OR u.active = :active)")
    Page<User> findUsersWithFilters(@Param("departmentId") Long departmentId,
                                    @Param("role") User.Role role,
                                    @Param("active") Boolean active,
                                    Pageable pageable);

    // Find users in the same department as a given user
    @Query("SELECT u FROM User u WHERE u.department.id = :departmentId AND u.id != :userId")
    List<User> findColleagues(@Param("departmentId") Long departmentId, @Param("userId") Long userId);
}
