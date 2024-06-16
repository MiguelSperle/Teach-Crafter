package com.miguelsperle.teach_crafter.modules.users.dtos.courses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateCourseNameDTO(
        @Schema(example = "Web Development", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A new course name is required to update the current course name")
        String newCourseName
) {
}
