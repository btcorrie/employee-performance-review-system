package com.btcorrie.reviewsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
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

    // Overall performance rating (1-5 scale, null if never reviewed)
    @Column(name = "current_performance_rating")
    private Integer currentPerformanceRating;

    // Latest review feedback/notes
    @Column(name = "last_review_notes", length = 2000)
    private String lastReviewNotes;

    // Date of most recent performance review
    @Column(name = "last_review_date")
    private LocalDate lastReviewDate;

    // Current goals and objectives (simple text field)
    @Column(name = "current_goals", length = 1000)
    private String currentGoals;

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

    // Helper method to check if user has performance data
    public boolean hasPerformanceData() {
        return currentPerformanceRating != null ||
                lastReviewNotes != null ||
                lastReviewDate != null ||
                currentGoals != null;
    }

    // Helper method to get performance rating as string
    public String getPerformanceRatingText() {
        if (currentPerformanceRating == null) return "Not Rated";
        return switch (currentPerformanceRating) {
            case 1 -> "Needs Improvement";
            case 2 -> "Below Expectations";
            case 3 -> "Meets Expectations";
            case 4 -> "Exceeds Expectations";
            case 5 -> "Outstanding";
            default -> "Invalid Rating";
        };
    }
}
