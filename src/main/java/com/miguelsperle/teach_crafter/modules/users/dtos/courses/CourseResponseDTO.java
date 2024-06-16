package com.miguelsperle.teach_crafter.modules.users.dtos.courses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CourseResponseDTO(
        @Schema(example = "f2a73b1d-8b6c-4a76-9a8e-5cf3a2e89d14")
        String id,
        @Schema(example = "Web Programming")
        String name,
        @Schema(example = "We will learn about HTML, CSS and JavaScript")
        String description,
        @Schema(example = "10")
        int maximumAttendees,
        @Schema(example = "10")
        int numberAvailableSpots,
        @Schema(example = "0")
        int amountEnrollment,
        @Schema(example = "2024-06-12T13:37:32.196Z")
        LocalDateTime createdAt,
        @Schema(example = "Example")
        String createdBy
) {
}
