package com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents;

import jakarta.validation.constraints.NotBlank;

public record UpdateCourseContentDescriptionDTO(
        @NotBlank(message = "A new course content description is required to update your current course content description")
        String newDescription
) {
}
