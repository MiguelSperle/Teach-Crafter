package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.*;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.SubscriptionsEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesRepository;
import com.miguelsperle.teach_crafter.utils.mappers.CoursesMapper;
import com.miguelsperle.teach_crafter.utils.mocks.CoursesEntityCreator;
import com.miguelsperle.teach_crafter.utils.mocks.SubscriptionsEntityCreator;
import com.miguelsperle.teach_crafter.utils.mocks.UsersEntityCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoursesServiceTest {
    @InjectMocks
    private CoursesService coursesService;

    @Mock
    private CoursesRepository coursesRepository;

    @Mock
    private UsersService usersService;

    @Mock
    private SubscriptionsService subscriptionsService;

    private CoursesEntity course;

    @BeforeEach
    public void setUp() {
        this.course = CoursesEntityCreator.createValidCoursesEntity();
        this.course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
    }

    @Test
    @DisplayName("Should be able to create a new course")
    public void should_be_able_to_create_a_new_course() {
        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.coursesRepository.findAllByUsersEntityId(any())).thenReturn(Collections.emptyList());

        when(this.coursesRepository.save(any(CoursesEntity.class))).thenReturn(CoursesEntityCreator.createCoursesEntityToBeSaved());

        CreateCourseDTO convertedToCreateCourseDTO = CoursesMapper.toConvertCreateCourseDTO(CoursesEntityCreator.createCoursesEntityToBeSaved());

        CoursesEntity newCourse = this.coursesService.createCourse(convertedToCreateCourseDTO);

        assertNotNull(newCourse.getId());
        assertThat(newCourse).hasFieldOrProperty("id");
    }

    @Test
    @DisplayName("Should not be able to create a new course if you had reached creation limit")
    public void should_not_be_able_to_create_a_new_course_if_you_had_reached_creation_limit() {
        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        List<CoursesEntity> existingCourses = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            existingCourses.add(this.course);
        }

        when(this.coursesRepository.findAllByUsersEntityId(any())).thenReturn(existingCourses);

        CreateCourseDTO convertedToCreateCourseDTO = CoursesMapper.toConvertCreateCourseDTO(CoursesEntityCreator.createCoursesEntityToBeSaved());

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.coursesService.createCourse(convertedToCreateCourseDTO);
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to update the course name")
    public void should_be_able_to_update_the_course_name() {
        when(this.coursesRepository.findById(any())).thenReturn(Optional.of(this.course));

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UpdateCourseNameDTO convertedToUpdateCourseNameDTO = CoursesMapper.toConvertUpdateCourseNameDTO(CoursesEntityCreator.createCoursesEntityToUpdateName());

        this.coursesService.updateCourseName(convertedToUpdateCourseNameDTO, any());

        // capture the value after of the method called ( save() )
        ArgumentCaptor<CoursesEntity> userCaptor = ArgumentCaptor.forClass(CoursesEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.coursesRepository).save(userCaptor.capture());

        assertEquals(convertedToUpdateCourseNameDTO.newName(), userCaptor.getValue().getName());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("Should not be able to update course name if user is not the owner")
    public void should_not_be_able_to_update_course_name_if_user_is_not_the_owner() {
        when(this.coursesRepository.findById(any())).thenReturn(Optional.of(this.course));

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createSecondValidUsersEntity());

        UpdateCourseNameDTO convertedToUpdateCourseNameDTO = CoursesMapper.toConvertUpdateCourseNameDTO(CoursesEntityCreator.createCoursesEntityToUpdateName());

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.coursesService.updateCourseName(convertedToUpdateCourseNameDTO, any());
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to update the course description")
    public void should_be_able_to_update_the_course_description() {
        when(this.coursesRepository.findById(any())).thenReturn(Optional.of(this.course));

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UpdateCourseDescriptionDTO convertedToUpdateCourseDescriptionDTO = CoursesMapper.toConvertUpdateCourseDescriptionDTO(CoursesEntityCreator.createCoursesEntityToUpdateDescription());

        this.coursesService.updateCourseDescription(convertedToUpdateCourseDescriptionDTO, any());

        // capture the value after of the method called ( save() )
        ArgumentCaptor<CoursesEntity> userCaptor = ArgumentCaptor.forClass(CoursesEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.coursesRepository).save(userCaptor.capture());

        assertEquals(convertedToUpdateCourseDescriptionDTO.newDescription(), userCaptor.getValue().getDescription());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("Should not be able to update course description if user is not the owner")
    public void should_not_be_able_to_update_course_description_if_user_is_not_the_owner() {
        when(this.coursesRepository.findById(any())).thenReturn(Optional.of(this.course));

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createSecondValidUsersEntity());

        UpdateCourseDescriptionDTO convertedToUpdateCourseDescriptionDTO = CoursesMapper.toConvertUpdateCourseDescriptionDTO(CoursesEntityCreator.createCoursesEntityToUpdateDescription());

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.coursesService.updateCourseDescription(convertedToUpdateCourseDescriptionDTO, any());
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to deactivate course")
    public void should_be_able_to_deactivate_course() {
        when(this.coursesRepository.findById(any())).thenReturn(Optional.of(this.course));

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        this.coursesService.deactivateCourse(this.course.getId());

        // Verify if the method save was called with a specific argument
        verify(this.coursesRepository).deleteById(this.course.getId());
    }

    @Test
    @DisplayName("Should not be able to deactivate course if user is not the owner")
    public void should_not_be_able_to_deactivate_course_if_user_is_not_the_owner() {
        when(this.coursesRepository.findById(any())).thenReturn(Optional.of(this.course));

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createSecondValidUsersEntity());

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.coursesService.deactivateCourse(this.course.getId());
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }


    @Test
    @DisplayName("Should be able to return all courses created by authenticated user")
    public void should_be_able_to_return_all_courses_created_by_authenticated_user() {
        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.coursesRepository.findAllByUsersEntityId(any())).thenReturn(List.of(this.course));

        SubscriptionsEntity subscription = SubscriptionsEntityCreator.createValidSubscriptionsEntity();
        subscription.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        subscription.setCoursesEntity(this.course);

        when(this.subscriptionsService.getAllSubscriptionsByCourseId(any())).thenReturn(List.of(subscription));

        List<CourseResponseDTO> result = this.coursesService.getAllCoursesCreatedByCreatorUser();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(this.course.getId(), result.get(0).id());
        assertEquals(this.course.getName(), result.get(0).name());
        assertEquals(this.course.getDescription(), result.get(0).description());
        assertEquals(this.course.getMaximumAttendees(), result.get(0).maximumAttendees());
        assertEquals(this.course.getMaximumAttendees() - List.of(subscription).size(), result.get(0).numberAvailableSpots());
        assertEquals(List.of(subscription).size(), result.get(0).amountSubscription());
        assertEquals(this.course.getCreatedAt(), result.get(0).createdAt());
        assertEquals(this.course.getUsersEntity().getName(), result.get(0).createdBy());
    }

    @Test
    @DisplayName("Should be able to return all courses by user subscriptions")
    public void should_be_able_to_return_all_courses_by_user_subscriptions() {
        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        SubscriptionsEntity subscription = SubscriptionsEntityCreator.createValidSubscriptionsEntity();
        subscription.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        subscription.setCoursesEntity(this.course);

        when(this.subscriptionsService.getAllSubscriptionsByUserId(any())).thenReturn(List.of(subscription));

        List<CoursesSubscribedResponseDTO> result = this.coursesService.getCoursesByUserSubscriptions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(subscription.getCoursesEntity().getId(), result.get(0).id());
        assertEquals(subscription.getCoursesEntity().getName(), result.get(0).name());
        assertEquals(subscription.getCoursesEntity().getDescription(), result.get(0).description());
        assertEquals(subscription.getCoursesEntity().getCreatedAt(), result.get(0).createdAt());
        assertEquals(subscription.getCreatedAt(), result.get(0).subscriptionCreatedAt());
        assertEquals(subscription.getCoursesEntity().getUsersEntity().getName(), result.get(0).createdBy());
    }

    @Test
    @DisplayName("Should be able to return all courses by description keyword")
    public void should_be_able_to_return_all_courses_by_description_keyword() {
        when(this.coursesRepository.findByDescriptionContainingIgnoreCase(any())).thenReturn(List.of(this.course));

        SubscriptionsEntity subscription = SubscriptionsEntityCreator.createValidSubscriptionsEntity();
        subscription.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        subscription.setCoursesEntity(this.course);

        when(this.subscriptionsService.getAllSubscriptionsByCourseId(any())).thenReturn(List.of(subscription));

        List<CourseResponseDTO> result = this.coursesService.getCourses(any());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(this.course.getId(), result.get(0).id());
        assertEquals(this.course.getName(), result.get(0).name());
        assertEquals(this.course.getDescription(), result.get(0).description());
        assertEquals(this.course.getMaximumAttendees() - List.of(subscription).size(), result.get(0).numberAvailableSpots());
        assertEquals(List.of(subscription).size(), result.get(0).amountSubscription());
        assertEquals(this.course.getCreatedAt(), result.get(0).createdAt());
        assertEquals(this.course.getUsersEntity().getName(), result.get(0).createdBy());
    }
}
