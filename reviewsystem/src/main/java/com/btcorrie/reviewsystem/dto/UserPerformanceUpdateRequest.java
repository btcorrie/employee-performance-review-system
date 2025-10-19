package com.btcorrie.reviewsystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPerformanceUpdateRequest {

    @Min(value = 1, message = "Performance rating must be between 1 and 5")
    @Max(value = 5, message = "Performance rating must be between 1 and 5")
    private Integer currentPerformanceRating;

    @Size(max = 2000, message = "Review notes cannot exceed 2000 characters")
    private String lastReviewNotes;

    private LocalDate lastReviewDate;

    @Size(max = 1000, message = "Current goals cannot exceed 1000 characters")
    private String currentGoals;
}
