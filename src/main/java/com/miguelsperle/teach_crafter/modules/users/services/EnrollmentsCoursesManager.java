package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.CourseNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.EnrollmentsEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesRepository;
import com.miguelsperle.teach_crafter.modules.users.repositories.EnrollmentsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentsCoursesManager {
    private final CoursesRepository coursesRepository;
    private final EnrollmentsRepository enrollmentsRepository;

    public EnrollmentsCoursesManager(
            final CoursesRepository coursesRepository,
            final EnrollmentsRepository enrollmentsRepository
    ) {
        this.coursesRepository = coursesRepository;
        this.enrollmentsRepository = enrollmentsRepository;
    }

    public CoursesEntity getCourseById(String courseId) {
        return this.coursesRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course not found"));
    }

    public List<EnrollmentsEntity> getAllEnrollmentsByCourseId(String courseId) {
        return this.enrollmentsRepository.findAllByCoursesEntityId(courseId);
    }

    public List<EnrollmentsEntity> getAllEnrollmentsByUserId(String userId) {
        return this.enrollmentsRepository.findAllByUsersEntityId(userId);
    }
}
