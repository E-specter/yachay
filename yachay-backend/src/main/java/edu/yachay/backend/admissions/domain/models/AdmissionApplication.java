package edu.yachay.backend.admissions.domain.models;

import edu.yachay.backend.identity.domain.models.StudentProfile;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "yachay_admission_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_first_name", nullable = false, length = 120)
    private String studentFirstName;

    @Column(name = "student_last_name", nullable = false, length = 160)
    private String studentLastName;

    @Column(name = "guardian_first_name", nullable = false, length = 120)
    private String guardianFirstName;

    @Column(name = "guardian_last_name", nullable = false, length = 160)
    private String guardianLastName;

    @Column(name = "guardian_phone", length = 30)
    private String guardianPhone;

    @Column(name = "guardian_email", nullable = false, length = 180)
    private String guardianEmail;

    @Column(name = "level_name", nullable = false, length = 60)
    private String level;

    @Column(name = "grade_name", nullable = false, length = 80)
    private String grade;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "observations", length = 1000)
    private String observations;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", unique = true)
    private StudentProfile studentProfile;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

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

    public String studentFullName() {
        return studentFirstName + " " + studentLastName;
    }

    public String guardianFullName() {
        return guardianFirstName + " " + guardianLastName;
    }
}
