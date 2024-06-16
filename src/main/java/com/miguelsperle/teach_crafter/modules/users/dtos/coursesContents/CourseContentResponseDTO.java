package com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CourseContentResponseDTO(
        @Schema(example = "5e9d6f8a-2c1b-47e3-b41f-10a8c7d39b72")
        String id,
        @Schema(example = "In this class, we are going to learn initial fundamentals about Html")
        String description,
        @Schema(example = "VIDEO_URL")
        String videoUrl,
        @Schema(example = "PUBLISHED")
        String status,
        LocalDate releaseDate,
        @Schema(example = "Introduction to Html")
        String contentModule,
        @Schema(example = "2024-06-12T13:37:32.196Z")
        LocalDateTime createdAt
) {
}
