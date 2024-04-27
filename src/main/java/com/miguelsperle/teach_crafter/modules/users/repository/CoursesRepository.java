package com.miguelsperle.teach_crafter.modules.users.repository;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursesRepository extends JpaRepository<CoursesEntity, String> {
}
