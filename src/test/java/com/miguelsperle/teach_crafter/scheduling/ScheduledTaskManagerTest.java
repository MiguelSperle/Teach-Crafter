package com.miguelsperle.teach_crafter.scheduling;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import com.miguelsperle.teach_crafter.modules.users.services.CoursesContentsService;
import com.miguelsperle.teach_crafter.utils.unit.mocks.CoursesContentsEntityCreator;
import com.miguelsperle.teach_crafter.utils.unit.mocks.CoursesEntityCreator;
import com.miguelsperle.teach_crafter.utils.unit.mocks.UsersEntityCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledTaskManagerTest {
    @InjectMocks
    private ScheduledTaskManager scheduledTaskManager;

    @Mock
    private CoursesContentsService coursesContentsService;

    @Mock
    private Logger logger;

    @Captor
    private ArgumentCaptor<List<CoursesContentsEntity>> listCaptor;

    @Test
    @DisplayName("Should be able to change pending content to published")
    public void should_be_able_to_change_pending_content_to_published() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesContentsService.getAllCoursesContentsByPendingStatus(any())).thenReturn(List.of(courseContent));

        when(this.coursesContentsService.isReleaseDateValid(any())).thenReturn("PUBLISHED");

        this.scheduledTaskManager.changePendingContentToPublished();

        // Verify if the method save was called with a specific argument
        verify(this.coursesContentsService, atLeastOnce()).saveAllCoursesContents(listCaptor.capture());

        String expectedMessage = "Saved successfully. Amount: " + listCaptor.getValue().size();

        verify(logger).info(expectedMessage);

        assertEquals("PUBLISHED", listCaptor.getValue().get(0).getStatus());
    }

    @Test
    @DisplayName("Should be able to log a message if no pending content found")
    public void should_be_able_to_log_a_message_if_no_pending_content_found() {
        when(this.coursesContentsService.getAllCoursesContentsByPendingStatus(any())).thenReturn(Collections.emptyList());

        this.scheduledTaskManager.changePendingContentToPublished();

        String expectedMessage = "No pending content to process";

        verify(logger).info(expectedMessage);
    }

    @Test
    @DisplayName("Should be able to log a message if no edited content to save")
    public void should_be_able_to_log_a_message_if_no_edited_content_to_save() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        CoursesContentsEntity courseContent = CoursesContentsEntityCreator.createValidCoursesContentsEntity();
        courseContent.setCoursesEntity(course);

        when(this.coursesContentsService.getAllCoursesContentsByPendingStatus(any())).thenReturn(List.of(courseContent));

        when(this.coursesContentsService.isReleaseDateValid(any())).thenReturn("PENDING");

        this.scheduledTaskManager.changePendingContentToPublished();

        String expectedMessage = "No edited content to save";

        verify(logger).info(expectedMessage);
    }

}
