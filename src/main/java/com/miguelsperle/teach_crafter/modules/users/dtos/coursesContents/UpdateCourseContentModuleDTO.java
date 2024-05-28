package com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents;

import jakarta.validation.constraints.NotBlank;

public record UpdateCourseContentModuleDTO(
        @NotBlank(message = "A new course module is required to update your current course content module")
        String newCourseModule
) {
}
