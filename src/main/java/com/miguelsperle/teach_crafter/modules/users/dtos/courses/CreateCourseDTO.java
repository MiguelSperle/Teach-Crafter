package com.miguelsperle.teach_crafter.modules.users.dtos.courses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCourseDTO(
        @Schema(example = "Web Programming", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Name is required to create a course")
        String name,

        @Schema(example = "We will learn about HTML, CSS and JavaScript", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Description is required to create a course")
        String description,

        @Schema(example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Maximum attendees is required to create a course")
        Integer maximumAttendees
) {
}
