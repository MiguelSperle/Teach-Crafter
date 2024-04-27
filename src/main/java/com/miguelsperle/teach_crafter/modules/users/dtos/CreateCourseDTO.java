package com.miguelsperle.teach_crafter.modules.users.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCourseDTO(
        @NotBlank(message = "Name is required to create a course")
        String name,

        @NotBlank(message = "Description is required to create a course")
        String description,

        @NotNull(message = "Maximum attendees is required to create a course")
        Integer maximumAttendees
) {
}
