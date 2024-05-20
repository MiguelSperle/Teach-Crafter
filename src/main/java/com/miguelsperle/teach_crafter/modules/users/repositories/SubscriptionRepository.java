package com.miguelsperle.teach_crafter.modules.users.repositories;

import com.miguelsperle.teach_crafter.modules.users.entities.subscription.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, String> {
    List<SubscriptionEntity> findAllByCoursesEntityId(String courseId);
    List<SubscriptionEntity> findAllByUsersEntityId(String userId);
    void deleteAllByCoursesEntityId(String courseId);
    void deleteByUsersEntityIdAndCoursesEntityId(String userId, String courseId);
    Optional<SubscriptionEntity> findByUsersEntityIdAndCoursesEntityId(String userId, String courseId);
}