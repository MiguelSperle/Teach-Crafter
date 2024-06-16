package com.miguelsperle.teach_crafter.utils.mocks;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CoursesContentsEntityCreator {
    public static CoursesContentsEntity createValidCoursesContentsEntity() {
        return CoursesContentsEntity
                .builder()
                .id("1")
                .description("Learn the fundamentals of Java")
                .videoUrl("VIDEO_URL")
                .status("PENDING")
                .releaseDate(LocalDate.now().plusDays(2))
                .coursesEntity(new CoursesEntity())
                .contentModule("Introduction to Java")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();
    }

    public static CoursesContentsEntity createCoursesContentsEntityToBeSaved() {
        return CoursesContentsEntity
                .builder()
                .id("1")
                .releaseDate(LocalDate.now())
                .build();
    }

    public static CoursesContentsEntity createCoursesContentsEntityToUpdateDescription() {
        return CoursesContentsEntity
                .builder()
                .description("Learn the fundamentals of Python")
                .build();
    }

    public static CoursesContentsEntity createCoursesContentsEntityToUpdateReleaseDate() {
        return CoursesContentsEntity
                .builder()
                .releaseDate(LocalDate.now())
                .build();
    }

    public static CoursesContentsEntity createCoursesContentsEntityToUpdateModule() {
        return CoursesContentsEntity
                .builder()
                .contentModule("Introduction to Python")
                .build();
    }
}
