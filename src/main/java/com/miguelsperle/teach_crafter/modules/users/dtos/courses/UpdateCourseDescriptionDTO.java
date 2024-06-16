package com.miguelsperle.teach_crafter.modules.users.dtos.courses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateCourseDescriptionDTO(
        @Schema(example = "We will explore the fundamentals of HTML, CSS, and JavaScript", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A new course description is required to update the current course description")
        String newCourseDescription
){}
