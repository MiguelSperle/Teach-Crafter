package com.miguelsperle.teach_crafter.scheduling;

import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import com.miguelsperle.teach_crafter.modules.users.services.CoursesContentsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTaskManager {
    private final CoursesContentsService coursesContentsService;

    public ScheduledTaskManager(final CoursesContentsService coursesContentsService) {
        this.coursesContentsService = coursesContentsService;
    }

    private static final String PENDING_STATUS = "PENDING";
    private static final String PUBLISHED_STATUS = "PUBLISHED";

    @Scheduled(cron = "0 0 0 * * *") // EVERY MIDNIGHT
    public void changePendingContentToPublished() {
        List<CoursesContentsEntity> pendingCoursesContents = this.coursesContentsService.getAllCoursesContentsByPendingStatus(PENDING_STATUS);

        List<CoursesContentsEntity> editedContentsToSave = new ArrayList<>();

        for (CoursesContentsEntity courseContent : pendingCoursesContents) {
            if (this.isCourseContentPublishable(courseContent)) {
                courseContent.setStatus(PUBLISHED_STATUS);
                editedContentsToSave.add(courseContent);
            }
        }

        if (!editedContentsToSave.isEmpty()) {
            this.coursesContentsService.saveAllCoursesContents(editedContentsToSave);
        }
    }

    private boolean isCourseContentPublishable(CoursesContentsEntity courseContent) {
        return PUBLISHED_STATUS.equals(this.coursesContentsService.isReleaseDateValid(courseContent.getReleaseDate()));
    }
}
