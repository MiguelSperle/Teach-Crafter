package com.miguelsperle.teach_crafter.modules.users.repositories;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursesRepository extends JpaRepository<CoursesEntity, String> {
    List<CoursesEntity> findAllByUsersEntityId(String userId);
    List<CoursesEntity> findByDescriptionContainingIgnoreCase(String filter);
}
