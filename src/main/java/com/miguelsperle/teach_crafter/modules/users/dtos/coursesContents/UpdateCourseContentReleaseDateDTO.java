package com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateCourseContentReleaseDateDTO(
        @NotNull(message = "A new release date is required to update your current course content release date")
        LocalDate newReleaseDate
) {
}
