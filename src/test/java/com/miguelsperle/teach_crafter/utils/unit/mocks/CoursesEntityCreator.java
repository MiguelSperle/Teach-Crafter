package com.miguelsperle.teach_crafter.utils.unit.mocks;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

import java.time.LocalDateTime;

public class CoursesEntityCreator {

    public static CoursesEntity createValidCoursesEntity() {
        return CoursesEntity
                .builder()
                .id("1")
                .name("Teaching Java")
                .description("We are gonna learn Java")
                .maximumAttendees(10)
                .createdAt(LocalDateTime.now().minusHours(2))
                .usersEntity(new UsersEntity())
                .build();
    }

    public static CoursesEntity createCoursesEntityToBeSaved() {
        return CoursesEntity
                .builder()
                .id("1")
                .build();
    }

    public static CoursesEntity createCoursesEntityToUpdateName() {
        return CoursesEntity
                .builder()
                .name("Teaching Python")
                .build();
    }

    public static CoursesEntity createCoursesEntityToUpdateDescription() {
        return CoursesEntity
                .builder()
                .description("We are gonna learn Python")
                .build();
    }
}
