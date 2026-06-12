package edu.yachay.backend.academic.domain.models;

import edu.yachay.backend.identity.domain.models.School;
import edu.yachay.backend.identity.domain.models.TeacherProfile;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(
        name = "yachay_sections",
        uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "academic_year_id", "grade_level", "name"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_teacher_id")
    private TeacherProfile tutorTeacher;

    @Column(name = "level_name", nullable = false, length = 60)
    private String level;

    @Column(name = "grade_level", nullable = false, length = 80)
    private String grade;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "room", length = 80)
    private String room;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "enrolled_count", nullable = false)
    private Integer enrolledCount;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

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
