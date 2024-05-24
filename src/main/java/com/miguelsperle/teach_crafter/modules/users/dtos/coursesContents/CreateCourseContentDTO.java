package com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateCourseContentDTO(
        @NotBlank(message = "description is required to create a content")
        String description,

        @NotNull(message = "A release date is required to create a content")
        LocalDate releaseDate,

        @NotBlank(message = "module is required to create a content")
        String courseModule
){}
