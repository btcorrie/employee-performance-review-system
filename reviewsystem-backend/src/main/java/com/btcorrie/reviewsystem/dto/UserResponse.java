package com.btcorrie.reviewsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // PERFORMANCE FIELDS
    private Integer currentPerformanceRating;
    private String currentPerformanceRatingText; // "Exceeds Expectations", etc.
    private String lastReviewNotes;
    private LocalDate lastReviewDate;
    private String currentGoals;
    private Boolean hasPerformanceData;

    // Department info
    private DepartmentSummary department;

    // Manager info
    private UserSummary manager;

    // Direct reports count
    private Integer directReportsCount;

    // Nested classes for related entity summaries
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentSummary {
        private Long id;
        private String name;
        private String organizationName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String username;
        private String fullName;
        private String role;
    }

    // Constructor without nested objects (for list responses)
    public UserResponse(Long id, String username, String email, String firstName,
                        String lastName, String fullName, String role, Boolean active,
                        LocalDateTime createdAt, LocalDateTime updatedAt,
                        Integer currentPerformanceRating, String currentPerformanceRatingText,
                        String lastReviewNotes, LocalDate lastReviewDate, String currentGoals,
                        Boolean hasPerformanceData, Integer directReportsCount) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.currentPerformanceRating = currentPerformanceRating;
        this.currentPerformanceRatingText = currentPerformanceRatingText;
        this.lastReviewNotes = lastReviewNotes;
        this.lastReviewDate = lastReviewDate;
        this.currentGoals = currentGoals;
        this.hasPerformanceData = hasPerformanceData;
        this.directReportsCount = directReportsCount;
    }
}
