package edu.yachay.backend.academic.domain.models;

import edu.yachay.backend.identity.domain.models.StudentProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "yachay_announcement_reads", uniqueConstraints = @UniqueConstraint(columnNames = {"announcement_id", "student_profile_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile student;

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    @PrePersist
    void onCreate() {
        readAt = readAt == null ? LocalDateTime.now() : readAt;
    }
}
