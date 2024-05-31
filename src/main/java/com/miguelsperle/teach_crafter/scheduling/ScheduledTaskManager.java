package com.miguelsperle.teach_crafter.scheduling;

import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesContentsRepository;
import com.miguelsperle.teach_crafter.modules.users.services.CoursesContentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTaskManager {
    @Autowired
    private CoursesContentsService coursesContentsService;
    @Autowired
    private CoursesContentsRepository coursesContentsRepository;

    private static final String PENDING_STATUS = "PENDING";
    private static final String PUBLISHED_STATUS = "PUBLISHED";

    @Scheduled(cron = "0 0 0 * * *") // EVERY MIDNIGHT
    public void processPendingCoursesForPublished() {
        List<CoursesContentsEntity> pendingCoursesContents = this.coursesContentsRepository.findAllByStatus(PENDING_STATUS);

        List<CoursesContentsEntity> publishedCoursesToSave = new ArrayList<>();

        for (CoursesContentsEntity courseContent : pendingCoursesContents) {
            if (this.isCoursePublishable(courseContent)) {
                courseContent.setStatus(PUBLISHED_STATUS);
                publishedCoursesToSave.add(courseContent);
            }
        }

        this.coursesContentsRepository.saveAll(publishedCoursesToSave);
    }

    private boolean isCoursePublishable(CoursesContentsEntity courseContent) {
        return PUBLISHED_STATUS.equals(this.coursesContentsService.isReleaseDateValid(courseContent.getReleaseDate()));
    }
}
