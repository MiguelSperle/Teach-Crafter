package com.miguelsperle.teach_crafter.modules.users.repositories;

import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.EnrollmentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentsRepository extends JpaRepository<EnrollmentsEntity, String> {
    List<EnrollmentsEntity> findAllByCoursesEntityId(String courseId);
    List<EnrollmentsEntity> findAllByUsersEntityId(String userId);
    void deleteByUsersEntityIdAndCoursesEntityId(String userId, String courseId);
    Optional<EnrollmentsEntity> findByUsersEntityIdAndCoursesEntityId(String userId, String courseId);
}