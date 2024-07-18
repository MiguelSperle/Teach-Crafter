package com.miguelsperle.teach_crafter.scheduling;

import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import com.miguelsperle.teach_crafter.modules.users.services.CoursesContentsService;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTaskManager {
    private final CoursesContentsService coursesContentsService;
    private final Logger logger;

    public ScheduledTaskManager(final CoursesContentsService coursesContentsService, final Logger logger) {
        this.coursesContentsService = coursesContentsService;
        this.logger = logger;
    }

    private static final String PENDING_STATUS = "PENDING";
    private static final String PUBLISHED_STATUS = "PUBLISHED";

    @Scheduled(cron = "0 0 0 * * *") // EVERY MIDNIGHT
    public void changePendingContentToPublished() {
        List<CoursesContentsEntity> pendingContents = this.coursesContentsService.getAllCoursesContentsByPendingStatus(PENDING_STATUS);

        if (pendingContents.isEmpty()) {
            logger.info("No pending content to process");
            return;
        }

        List<CoursesContentsEntity> editedContentsToSave = new ArrayList<>();

        for (CoursesContentsEntity pendingContent : pendingContents) {
            if (this.isCourseContentPublishable(pendingContent)) {
                pendingContent.setStatus(PUBLISHED_STATUS);
                editedContentsToSave.add(pendingContent);
            }
        }

        if (editedContentsToSave.isEmpty()) {
            logger.info("No edited content to save");
            return;
        }

        this.coursesContentsService.saveAllCoursesContents(editedContentsToSave);
        logger.info("Saved successfully. Amount: " + editedContentsToSave.size());
    }

    private boolean isCourseContentPublishable(CoursesContentsEntity courseContent) {
        return PUBLISHED_STATUS.equals(this.coursesContentsService.isReleaseDateValid(courseContent.getReleaseDate()));
    }
}
