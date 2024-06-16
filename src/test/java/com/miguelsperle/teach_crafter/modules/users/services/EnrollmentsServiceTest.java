package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.NoAvailableSpotsException;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.EnrollmentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.exceptions.EnrollmentAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.exceptions.EnrollmentNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.repositories.EnrollmentsRepository;
import com.miguelsperle.teach_crafter.utils.mocks.CoursesEntityCreator;
import com.miguelsperle.teach_crafter.utils.mocks.EnrollmentsEntityCreator;
import com.miguelsperle.teach_crafter.utils.mocks.UsersEntityCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnrollmentsServiceTest {
    @InjectMocks
    private EnrollmentsService enrollmentsService;

    @Mock
    private EnrollmentsRepository enrollmentsRepository;

    @Mock
    private UsersService usersService;

    @Mock
    private EnrollmentsCoursesManager enrollmentsCoursesManager;

    @BeforeEach
    public void setUp() {
        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());
    }

    @Test
    @DisplayName("User should be able to create a enrollment in some course")
    public void user_should_be_able_to_create_a_enrollment() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        when(this.enrollmentsCoursesManager.getCourseById(any())).thenReturn(course);

        when(this.enrollmentsRepository.findAllByCoursesEntityId(any())).thenReturn(Collections.emptyList());

        when(this.enrollmentsRepository.findByUsersEntityIdAndCoursesEntityId(any(), any())).thenReturn(Optional.empty());

        when(this.enrollmentsRepository.save(any(EnrollmentsEntity.class))).thenReturn(EnrollmentsEntityCreator.createEnrollmentsEntityToBeSaved());

        EnrollmentsEntity newEnrollment = this.enrollmentsService.createCourseEnrollment(course.getId());

        assertNotNull(newEnrollment.getId());
        assertThat(newEnrollment).hasFieldOrProperty("id");
    }

    @Test
    @DisplayName("User should not be able to create a enrollment if the same individual is the course owner")
    public void user_should_not_be_able_to_create_a_enrollment_if_the_same_individual_is_the_course_owner() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.enrollmentsCoursesManager.getCourseById(any())).thenReturn(course);

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.enrollmentsService.createCourseEnrollment(course.getId());
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("User should not be able to create a enrollment if the course does not have available spots")
    public void user_should_not_be_able_to_create_a_enrollment_if_the_course_does_not_have_available_spots() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        EnrollmentsEntity enrollment = EnrollmentsEntityCreator.createValidEnrollmentsEntity();
        enrollment.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        enrollment.setCoursesEntity(course);

        List<EnrollmentsEntity> existingEnrollment = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            existingEnrollment.add(enrollment);
        }

        when(this.enrollmentsRepository.findAllByCoursesEntityId(any())).thenReturn(existingEnrollment);

        when(this.enrollmentsCoursesManager.getCourseById(any())).thenReturn(course);

        NoAvailableSpotsException exception = assertThrows(NoAvailableSpotsException.class, () -> {
            this.enrollmentsService.createCourseEnrollment(course.getId());
        });

        String expectedErrorMessage = "No available spots";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("User should not be able to create a enrollment if the same individual is already subscribed in the course")
    public void user_should_not_be_able_to_create_a_enrollment_if_the_same_individual_is_already_subscribed_in_the_course() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        when(this.enrollmentsCoursesManager.getCourseById(any())).thenReturn(course);

        EnrollmentsEntity enrollment = EnrollmentsEntityCreator.createValidEnrollmentsEntity();
        enrollment.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        enrollment.setCoursesEntity(course);

        when(this.enrollmentsRepository.findByUsersEntityIdAndCoursesEntityId(any(), any())).thenReturn(Optional.of(enrollment));

        EnrollmentAlreadyExistsException exception = assertThrows(EnrollmentAlreadyExistsException.class, () -> {
            this.enrollmentsService.createCourseEnrollment(course.getId());
        });

        String expectedErrorMessage = "You have already subscribed in this course";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("User should be able to delete a enrollment of a course")
    public void user_should_be_able_to_delete_a_enrollment_of_a_course() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        EnrollmentsEntity enrollment = EnrollmentsEntityCreator.createValidEnrollmentsEntity();
        enrollment.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        enrollment.setCoursesEntity(course);

        when(this.enrollmentsRepository.findByUsersEntityIdAndCoursesEntityId(any(), any())).thenReturn(Optional.of(enrollment));

        this.enrollmentsService.deleteCourseEnrollment(course.getId());

        // Verify if the method save was called with a specific argument
        verify(this.enrollmentsRepository).deleteByUsersEntityIdAndCoursesEntityId(UsersEntityCreator.createValidAuthenticatedUsersEntity().getId(), course.getId());
    }

    @Test
    @DisplayName("User should not be able to delete a enrollment of a course if the same individual is not subscribed in the course")
    public void user_should_not_be_able_to_delete_a_enrollment_of_a_course_if_the_same_individual_is_not_subscribed_in_the_course() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        when(this.enrollmentsRepository.findByUsersEntityIdAndCoursesEntityId(any(), any())).thenReturn(Optional.empty());

        EnrollmentNotFoundException exception = assertThrows(EnrollmentNotFoundException.class, () -> {
            this.enrollmentsService.deleteCourseEnrollment(course.getId());
        });

        String expectedErrorMessage = "Enrollment does not exist";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
