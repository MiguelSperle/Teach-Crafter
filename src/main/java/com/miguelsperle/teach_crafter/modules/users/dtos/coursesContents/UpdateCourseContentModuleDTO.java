package com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateCourseContentModuleDTO(
        @Schema(example = "Introduction to JavaScript", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A new content module is required to update the current content module")
        String newContentModule
) {
}
