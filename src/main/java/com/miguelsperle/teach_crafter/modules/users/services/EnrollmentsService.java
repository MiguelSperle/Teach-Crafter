package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.EnrollmentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.exceptions.EnrollmentAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.NoAvailableSpotsException;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.exceptions.EnrollmentNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.EnrollmentsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EnrollmentsService {
    private final EnrollmentsRepository enrollmentsRepository;
    private final UsersService usersService;
    private final EnrollmentsCoursesManager enrollmentsCoursesManager;

    public EnrollmentsService(
            final EnrollmentsRepository enrollmentsRepository,
            final UsersService usersService,
            final EnrollmentsCoursesManager enrollmentsCoursesManager
    ) {
        this.enrollmentsRepository = enrollmentsRepository;
        this.usersService = usersService;
        this.enrollmentsCoursesManager = enrollmentsCoursesManager;
    }

    private List<EnrollmentsEntity> getAllEnrollmentsByCourseId(String courseId) {
        return this.enrollmentsRepository.findAllByCoursesEntityId(courseId);
    }

    public EnrollmentsEntity createCourseEnrollment(String courseId) {
        EnrollmentsEntity newEnrollment = new EnrollmentsEntity();

        this.ensureUserIsNotCourseOwner(courseId);

        this.verifyAvailableSpots(courseId);

        this.verifyEnrollmentExistsForTheCourse(courseId);

        newEnrollment.setUsersEntity(this.usersService.getAuthenticatedUser());
        newEnrollment.setCoursesEntity(this.enrollmentsCoursesManager.getCourseById(courseId));

        return this.enrollmentsRepository.save(newEnrollment);
    }

    private void ensureUserIsNotCourseOwner(String courseId) {
        UsersEntity user = this.usersService.getAuthenticatedUser();

        CoursesEntity course = this.enrollmentsCoursesManager.getCourseById(courseId);

        if (Objects.equals(user.getId(), course.getUsersEntity().getId())) {
            throw new TaskDeniedException("Task not allowed");
        }
    }

    private void verifyAvailableSpots(String courseId) {
        List<EnrollmentsEntity> enrollments = this.getAllEnrollmentsByCourseId(courseId);

        CoursesEntity course = this.enrollmentsCoursesManager.getCourseById(courseId);

        if (Objects.equals(course.getMaximumAttendees() - enrollments.size(), 0)) {
            throw new NoAvailableSpotsException("No available spots");
        }
    }

    private void verifyEnrollmentExistsForTheCourse(String courseId) {
        UsersEntity user = this.usersService.getAuthenticatedUser();

        Optional<EnrollmentsEntity> enrollment = this.getEnrollmentByUserIdAndCourseId(user.getId(), courseId);

        if (enrollment.isPresent())
            throw new EnrollmentAlreadyExistsException("You have already subscribed in this course");
    }

    public Optional<EnrollmentsEntity> getEnrollmentByUserIdAndCourseId(String userId, String courseId) {
        return this.enrollmentsRepository.findByUsersEntityIdAndCoursesEntityId(userId, courseId);
    }

    @Transactional
    public void deleteCourseEnrollment(String courseId) {
        UsersEntity user = this.usersService.getAuthenticatedUser();

        this.ensureUserIsSubscribed(user.getId(), courseId);

        this.enrollmentsRepository.deleteByUsersEntityIdAndCoursesEntityId(user.getId(), courseId);
    }

    public void ensureUserIsSubscribed(String userId, String courseId) {
        this.getEnrollmentByUserIdAndCourseId(userId, courseId).orElseThrow(() -> new EnrollmentNotFoundException("Enrollment does not exist"));
    }
}
