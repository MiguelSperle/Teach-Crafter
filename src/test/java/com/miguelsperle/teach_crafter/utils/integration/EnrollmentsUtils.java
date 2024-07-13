package com.miguelsperle.teach_crafter.utils.integration;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.EnrollmentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

public class EnrollmentsUtils {
    public static EnrollmentsEntity createEnrollment(CoursesEntity course, UsersEntity user) {
        return EnrollmentsEntity.builder().coursesEntity(course).usersEntity(user).build();
    }
}
