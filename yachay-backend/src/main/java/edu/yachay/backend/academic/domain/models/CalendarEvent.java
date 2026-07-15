package edu.yachay.backend.academic.domain.models;

import edu.yachay.backend.identity.domain.models.StudentProfile;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "yachay_calendar_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id")
    private StudentProfile student;

    @Column(name = "title", nullable = false, length = 180)
    private String title;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "audience", nullable = false, length = 50)
    @Builder.Default
    private String audience = "TODOS";

    @Column(name = "day_of_week", length = 20)
    private String dayOfWeek;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "description", length = 1000)
    private String description;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 20, columnDefinition = "varchar(20) default 'ACTIVO'")
    private String status = "ACTIVO";

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
