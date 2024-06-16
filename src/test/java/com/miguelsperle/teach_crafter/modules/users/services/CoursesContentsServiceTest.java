package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.*;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions.InvalidReleaseDateException;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.exceptions.EnrollmentNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesContentsRepository;
import com.miguelsperle.teach_crafter.utils.mappers.CoursesContentsMapper;
import com.miguelsperle.teach_crafter.utils.mocks.CoursesContentsEntityCreator;
import com.miguelsperle.teach_crafter.utils.mocks.CoursesEntityCreator;
import com.miguelsperle.teach_crafter.utils.mocks.UsersEntityCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoursesContentsServiceTest {
    @InjectMocks
    private CoursesContentsService coursesContentsService;

    @Mock
    private CoursesContentsRepository coursesContentsRepository;

    @Mock
    private CoursesService coursesService;

    @Mock
    private UsersService usersService;

    @Mock
    private EnrollmentsService enrollmentsService;

    @Mock
    private CloudinaryVideoService cloudinaryVideoService;

    @Test
    @DisplayName("Creator user should be able to create a course content")
    public void creator_user_should_be_able_to_create_a_course_content() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesContentsEntity courseContentToBeSaved = CoursesContentsEntityCreator.createCoursesContentsEntityToBeSaved();
        courseContentToBeSaved.setReleaseDate(courseContentToBeSaved.getReleaseDate().plusDays(2));

        when(this.coursesContentsRepository.save(any(CoursesContentsEntity.class))).thenReturn(courseContentToBeSaved);

        CreateCourseContentDTO convertedToCreateCourseContentDTO = CoursesContentsMapper.toConvertCreateCourseContentDTO(courseContentToBeSaved);

        CoursesContentsEntity newCourseContent = this.coursesContentsService.createCourseContent(course.getId(), convertedToCreateCourseContentDTO);

        assertNotNull(newCourseContent.getId());
        assertThat(newCourseContent).hasFieldOrProperty("id");
    }

    @Test
    @DisplayName("Creator user should not be able to create a course content if the same individual is not the course owner")
    public void creator_user_should_not_be_able_to_create_a_course_content_if_the_same_individual_is_not_the_course_owner() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesContentsEntity courseContentToBeSaved = CoursesContentsEntityCreator.createCoursesContentsEntityToBeSaved();
        courseContentToBeSaved.setReleaseDate(courseContentToBeSaved.getReleaseDate().plusDays(2));

        CreateCourseContentDTO convertedToCreateCourseContentDTO = CoursesContentsMapper.toConvertCreateCourseContentDTO(courseContentToBeSaved);

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.coursesContentsService.createCourseContent(course.getId(), convertedToCreateCourseContentDTO);
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }


    @Test
    @DisplayName("Creator user should not be able to create a course content if the course release date is in the past")
    public void creator_user_should_not_be_able_to_create_a_course_content_if_the_course_release_date_is_in_the_past() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesContentsEntity courseContentToBeSaved = CoursesContentsEntityCreator.createCoursesContentsEntityToBeSaved();
        courseContentToBeSaved.setReleaseDate(courseContentToBeSaved.getReleaseDate().minusDays(2));

        CreateCourseContentDTO convertedToCreateCourseContentDTO = CoursesContentsMapper.toConvertCreateCourseContentDTO(courseContentToBeSaved);

        InvalidReleaseDateException exception = assertThrows(InvalidReleaseDateException.class, () -> {
            this.coursesContentsService.createCourseContent(course.getId(), convertedToCreateCourseContentDTO);
        });

        String expectedErrorMessage = "Release date cannot be in the past";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Creator user should be able to update course content description")
    public void creator_user_should_be_able_to_update_course_content_description() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesContentsRepository.findById(any())).thenReturn(Optional.of(courseContent));

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UpdateCourseContentDescriptionDTO convertedToUpdateCourseContentDescriptionDTO = CoursesContentsMapper.toConvertUpdateCourseContentDescriptionDTO(CoursesContentsEntityCreator.createCoursesContentsEntityToUpdateDescription());

        this.coursesContentsService.updateCourseContentDescription(courseContent.getId(), convertedToUpdateCourseContentDescriptionDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<CoursesContentsEntity> userCaptor = ArgumentCaptor.forClass(CoursesContentsEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.coursesContentsRepository).save(userCaptor.capture());

        assertEquals(convertedToUpdateCourseContentDescriptionDTO.newContentDescription(), userCaptor.getValue().getDescription());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("Creator user should not be able to update course content description if the same individual is not the course owner")
    public void creator_user_should_not_be_able_to_update_course_content_description_if_the_same_individual_is_not_the_course_owner() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesContentsRepository.findById(any())).thenReturn(Optional.of(courseContent));

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UpdateCourseContentDescriptionDTO convertedToUpdateCourseContentDescriptionDTO = CoursesContentsMapper.toConvertUpdateCourseContentDescriptionDTO(CoursesContentsEntityCreator.createCoursesContentsEntityToUpdateDescription());

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.coursesContentsService.updateCourseContentDescription(courseContent.getId(), convertedToUpdateCourseContentDescriptionDTO);
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Creator user should be able to update course content video")
    public void creator_user_should_be_able_to_update_course_content_video() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesContentsRepository.findById(any())).thenReturn(Optional.of(courseContent));

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        MultipartFile mockVideoFile = mock(MultipartFile.class);

        String expectedUrl = "NEW_VIDEO_URL";
        when(this.cloudinaryVideoService.uploadVideoFile(mockVideoFile, "course_videos")).thenReturn(expectedUrl);

        this.coursesContentsService.uploadCourseContentVideo(courseContent.getId(), mockVideoFile);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<CoursesContentsEntity> userCaptor = ArgumentCaptor.forClass(CoursesContentsEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.coursesContentsRepository).save(userCaptor.capture());

        assertEquals(expectedUrl, userCaptor.getValue().getVideoUrl());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("Creator user should not be able to update course content video if the same individual is not the course owner")
    public void creator_user_should_not_be_able_to_update_course_content_video_if_the_same_individual_is_not_the_course_owner() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesContentsRepository.findById(any())).thenReturn(Optional.of(courseContent));

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        MultipartFile mockVideoFile = mock(MultipartFile.class);

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.coursesContentsService.uploadCourseContentVideo(courseContent.getId(), mockVideoFile);
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Creator user should be able to update course content release date")
    public void creator_user_should_be_able_to_update_course_content_release_date() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesContentsRepository.findById(any())).thenReturn(Optional.of(courseContent));

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UpdateCourseContentReleaseDateDTO convertedToUpdateCourseContentReleaseDateDTO = CoursesContentsMapper.toConvertUpdateCourseContentReleaseDateDTO(CoursesContentsEntityCreator.createCoursesContentsEntityToUpdateReleaseDate());

        this.coursesContentsService.updateCourseContentReleaseDate(courseContent.getId(), convertedToUpdateCourseContentReleaseDateDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<CoursesContentsEntity> userCaptor = ArgumentCaptor.forClass(CoursesContentsEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.coursesContentsRepository).save(userCaptor.capture());

        assertEquals(convertedToUpdateCourseContentReleaseDateDTO.newContentReleaseDate(), userCaptor.getValue().getReleaseDate());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("Creator user should not be able to update course content release date if the same individual is not the course owner")
    public void creator_user_should_not_be_able_to_update_course_content_release_date_if_the_same_individual_is_not_the_course_owner() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesContentsRepository.findById(any())).thenReturn(Optional.of(courseContent));

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UpdateCourseContentReleaseDateDTO convertedToUpdateCourseContentReleaseDateDTO = CoursesContentsMapper.toConvertUpdateCourseContentReleaseDateDTO(CoursesContentsEntityCreator.createCoursesContentsEntityToUpdateReleaseDate());

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.coursesContentsService.updateCourseContentReleaseDate(courseContent.getId(), convertedToUpdateCourseContentReleaseDateDTO);
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Creator user should be able to update course content module")
    public void creator_user_should_be_able_to_update_course_content_module() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesContentsRepository.findById(any())).thenReturn(Optional.of(courseContent));

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UpdateCourseContentModuleDTO convertedToUpdateCourseContentModuleDTO = CoursesContentsMapper.toConvertUpdateCourseContentModuleDTO(CoursesContentsEntityCreator.createCoursesContentsEntityToUpdateModule());

        this.coursesContentsService.updateCourseContentModule(courseContent.getId(), convertedToUpdateCourseContentModuleDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<CoursesContentsEntity> userCaptor = ArgumentCaptor.forClass(CoursesContentsEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.coursesContentsRepository).save(userCaptor.capture());

        assertEquals(convertedToUpdateCourseContentModuleDTO.newContentModule(), userCaptor.getValue().getContentModule());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("Creator user should not be able to update course content module if the same individual is not the course owner")
    public void creator_user_should_not_be_able_to_update_course_content_module_if_the_same_individual_is_not_the_course_owner() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesContentsRepository.findById(any())).thenReturn(Optional.of(courseContent));

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UpdateCourseContentModuleDTO convertedToUpdateCourseContentModuleDTO = CoursesContentsMapper.toConvertUpdateCourseContentModuleDTO(CoursesContentsEntityCreator.createCoursesContentsEntityToUpdateModule());

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.coursesContentsService.updateCourseContentModule(courseContent.getId(), convertedToUpdateCourseContentModuleDTO);
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to return all content of a specific course created by creator user")
    public void should_be_able_to_return_all_content_of_a_specific_course_created_by_creator_user() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.coursesContentsRepository.findAllByCoursesEntityId(any())).thenReturn(List.of(courseContent));

        List<CourseContentResponseDTO> result = this.coursesContentsService.getCourseContentsCreatedByCreatorUser(course.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(courseContent.getId(), result.get(0).id());
        assertEquals(courseContent.getDescription(), result.get(0).description());
        assertEquals(courseContent.getVideoUrl(), result.get(0).videoUrl());
        assertEquals(courseContent.getStatus(), result.get(0).status());
        assertEquals(courseContent.getReleaseDate(), result.get(0).releaseDate());
        assertEquals(courseContent.getContentModule(), result.get(0).contentModule());
        assertEquals(courseContent.getCreatedAt(), result.get(0).createdAt());
    }

    @Test
    @DisplayName("Should not be able to return all content of a specific course if the creator user is not the course owner")
    public void should_not_be_able_to_return_all_content_of_a_specific_course_if_the_creator_user_is_not_the_course_owner() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesService.getCourseById(any())).thenReturn(course);

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.coursesContentsService.getCourseContentsCreatedByCreatorUser(course.getId());
        });

        String expectedErrorMessage = "Task not allowed";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to return all published course contents if the user is subscribed in the course")
    public void should_be_able_to_return_all_published_course_contents_if_the_user_is_subscribed_in_the_course() {
        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setStatus("PUBLISHED");
        courseContent.setReleaseDate(LocalDate.now());
        courseContent.setCoursesEntity(course);

        doNothing().when(this.enrollmentsService).ensureUserIsNotSubscribed(any(), any());

        when(this.coursesContentsRepository.findAllByCoursesEntityIdAndStatus(any(), any())).thenReturn(List.of(courseContent));

        List<CourseContentResponseDTO> result = this.coursesContentsService.getPublishedCourseContentsForSubscribedUser(course.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(courseContent.getId(), result.get(0).id());
        assertEquals(courseContent.getDescription(), result.get(0).description());
        assertEquals(courseContent.getVideoUrl(), result.get(0).videoUrl());
        assertEquals(courseContent.getStatus(), result.get(0).status());
        assertEquals(courseContent.getReleaseDate(), result.get(0).releaseDate());
        assertEquals(courseContent.getContentModule(), result.get(0).contentModule());
        assertEquals(courseContent.getCreatedAt(), result.get(0).createdAt());
    }

    @Test
    @DisplayName("Should not be able to return all published course contents if the user is not subscribed in the course")
    public void should_not_be_able_to_return_all_published_course_contents_if_the_user_is_not_subscribed_in_the_course() {
        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        doThrow(new EnrollmentNotFoundException("Enrollment does not exist"))
                .when(this.enrollmentsService).ensureUserIsNotSubscribed(any(), any());

        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        EnrollmentNotFoundException exception = assertThrows(EnrollmentNotFoundException.class, () -> {
            this.coursesContentsService.getPublishedCourseContentsForSubscribedUser(course.getId());
        });

        String expectedErrorMessage = "Enrollment does not exist";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
