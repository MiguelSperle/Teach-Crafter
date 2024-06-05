package com.miguelsperle.teach_crafter.modules.users.entities.coursesContents;


import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "courses_contents")
@Entity(name = "courses_contents")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoursesContentsEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String description;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(nullable = false)
    private String status;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CoursesEntity coursesEntity;

    @Column(name = "course_module", nullable = false)
    private String courseModule;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
