package com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateCourseContentDTO(
        @Schema(example = "In this class, we are going to learn initial fundamentals about Html", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Description is required to create a content")
        String description,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "A release date is required to create a content")
        LocalDate releaseDate,

        @Schema(example = "Introduction to Html", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Course module is required to create a content")
        String courseModule
) {
}
