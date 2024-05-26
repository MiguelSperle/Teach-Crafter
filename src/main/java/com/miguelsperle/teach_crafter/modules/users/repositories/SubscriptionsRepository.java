package com.miguelsperle.teach_crafter.modules.users.repositories;

import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.SubscriptionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionsRepository extends JpaRepository<SubscriptionsEntity, String> {
    List<SubscriptionsEntity> findAllByCoursesEntityId(String courseId);
    List<SubscriptionsEntity> findAllByUsersEntityId(String userId);
    void deleteByUsersEntityIdAndCoursesEntityId(String userId, String courseId);
    Optional<SubscriptionsEntity> findByUsersEntityIdAndCoursesEntityId(String userId, String courseId);
}