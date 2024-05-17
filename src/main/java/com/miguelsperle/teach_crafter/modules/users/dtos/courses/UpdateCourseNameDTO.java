package com.miguelsperle.teach_crafter.modules.users.dtos.courses;

import jakarta.validation.constraints.NotBlank;

public record UpdateCourseNameDTO(
        @NotBlank(message = "A new course name is required to update your current course name")
        String newName
){}
