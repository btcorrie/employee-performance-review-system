package com.btcorrie.reviewsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // Will be BCrypt hashed

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // NEW RELATIONSHIPS

    // Many users belong to one department
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // Self-referential relationship: Many users can have one manager
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    // One user can manage many users (reverse of manager relationship)
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> directReports;

    // One user can manage many departments
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Department> managedDepartments;

    // Enum for user roles
    public enum Role {
        EMPLOYEE,      // Can view own profile and reviews
        MANAGER,       // Can manage direct reports and create reviews
        HR_ADMIN,      // Can view all users and manage departments
        SYSTEM_ADMIN   // Full system access
    }

    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Helper method to check if user is a manager
    public boolean isManager() {
        return role == Role.MANAGER || role == Role.HR_ADMIN || role == Role.SYSTEM_ADMIN;
    }

    // Helper method to check if user has admin privileges
    public boolean isAdmin() {
        return role == Role.HR_ADMIN || role == Role.SYSTEM_ADMIN;
    }
}
