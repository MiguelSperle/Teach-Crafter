package com.miguelsperle.teach_crafter.modules.users.entities.subscriptions;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "subscriptions")
@Entity(name = "subscriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionsEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity usersEntity;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CoursesEntity coursesEntity;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}