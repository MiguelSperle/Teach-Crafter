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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledTaskManagerTest {
    @InjectMocks
    private ScheduledTaskManager scheduledTaskManager;

    @Mock
    private CoursesContentsService coursesContentsService;

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
        verify(this.coursesContentsService, times(1)).saveAllCoursesContents(listCaptor.capture());

        assertEquals("PUBLISHED", listCaptor.getValue().get(0).getStatus());
    }
}
