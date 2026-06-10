package edu.yachay.backend.academic.domain.models;

import edu.yachay.backend.identity.domain.models.TeacherProfile;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "yachay_academic_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_profile_id", nullable = false)
    private TeacherProfile teacher;

    @Column(name = "title", nullable = false, length = 180)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    @Column(name = "due_at", nullable = false)
    private LocalDateTime dueAt;

    @Column(name = "max_score", precision = 5, scale = 2)
    private java.math.BigDecimal maxScore;

    @Column(name = "task_type", length = 40)
    private String taskType;

    @Column(name = "allow_late_submission")
    private Boolean allowLateSubmission;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
