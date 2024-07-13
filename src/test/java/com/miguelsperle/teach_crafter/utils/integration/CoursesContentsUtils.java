package com.miguelsperle.teach_crafter.utils.integration;

import com.github.javafaker.Faker;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;

import java.time.LocalDate;

public class CoursesContentsUtils {
    public static CoursesContentsEntity createCourseContent(CoursesEntity course, Faker faker) {
        return CoursesContentsEntity.builder().coursesEntity(course).contentModule("Introduction to " + faker.lorem().word())
                .releaseDate(LocalDate.now()).description(faker.lorem().paragraph()).status("PUBLISHED").build();
    }
}
