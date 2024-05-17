package com.miguelsperle.teach_crafter.modules.users.dtos.courses;

import jakarta.validation.constraints.NotBlank;

public record UpdateCourseDescriptionDTO(
        @NotBlank(message = "A new course description is required to update your current course description")
        String newDescription
){}
