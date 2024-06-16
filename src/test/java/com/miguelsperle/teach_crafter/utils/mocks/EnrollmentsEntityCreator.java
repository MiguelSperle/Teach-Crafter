package com.miguelsperle.teach_crafter.utils.mocks;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.EnrollmentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

import java.time.LocalDateTime;

public class EnrollmentsEntityCreator {
    public static EnrollmentsEntity createValidEnrollmentsEntity() {
        return EnrollmentsEntity
                .builder()
                .id("1")
                .usersEntity(new UsersEntity())
                .coursesEntity(new CoursesEntity())
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

    }

    public static EnrollmentsEntity createEnrollmentsEntityToBeSaved() {
        return EnrollmentsEntity
                .builder()
                .id("1")
                .build();
    }
}
