package com.miguelsperle.teach_crafter.modules.users.repositories;

import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursesContentsRepository extends JpaRepository<CoursesContentsEntity, String> {
    List<CoursesContentsEntity> findAllByCoursesEntityId(String courseId);
    List<CoursesContentsEntity> findAllByCoursesEntityIdAndStatus(String courseId, String status);
    List<CoursesContentsEntity> findAllByStatus(String status);
}
