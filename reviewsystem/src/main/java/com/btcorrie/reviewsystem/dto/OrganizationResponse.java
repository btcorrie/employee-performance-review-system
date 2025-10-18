package com.btcorrie.reviewsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional: Include department count (but not full department details)
    private Integer departmentCount;

    // Optional: Include basic department info for detailed responses
    private List<DepartmentSummary> departments;

    // Nested class for department summary
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentSummary {
        private Long id;
        private String name;
        private Boolean active;
        private Integer userCount;
        private String managerName;
    }

    // Constructor without departments (for list responses)
    public OrganizationResponse(Long id, String name, String description, Boolean active,
                                LocalDateTime createdAt, LocalDateTime updatedAt, Integer departmentCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.departmentCount = departmentCount;
    }
}
