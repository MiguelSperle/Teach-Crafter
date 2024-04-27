package com.miguelsperle.teach_crafter.modules.users.entities.courses;

import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "courses")
@Entity(name = "courses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoursesEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "maximum_attendees", nullable = false)
    private Integer maximumAttendees;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private UsersEntity usersEntity;
}
