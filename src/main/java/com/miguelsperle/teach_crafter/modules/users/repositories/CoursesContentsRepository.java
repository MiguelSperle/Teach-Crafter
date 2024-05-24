package com.miguelsperle.teach_crafter.modules.users.repositories;

import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursesContentsRepository extends JpaRepository<CoursesContentsEntity, String> {
}
