package com.btcorrie.reviewsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Organization info
    private OrganizationSummary organization;

    // Manager info
    private UserSummary manager;

    // User count
    private Integer userCount;

    // Optional: List of users for detailed responses
    private List<UserSummary> users;

    // Nested classes for related entity summaries
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizationSummary {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
        private String fullName;
        private String role;
        private Boolean active;
    }

    // Constructor without users list (for list responses)
    public DepartmentResponse(Long id, String name, String description, Boolean active,
                              LocalDateTime createdAt, LocalDateTime updatedAt,
                              OrganizationSummary organization, UserSummary manager, Integer userCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.organization = organization;
        this.manager = manager;
        this.userCount = userCount;
    }
}
