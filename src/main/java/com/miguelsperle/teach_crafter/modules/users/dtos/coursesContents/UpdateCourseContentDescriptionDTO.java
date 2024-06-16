package com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateCourseContentDescriptionDTO(
        @Schema(example = "In this class, we are going to learn initial fundamentals about JavaScript", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A new content description is required to update the current content description")
        String newContentDescription
) {
}
