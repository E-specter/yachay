package edu.yachay.backend.admissions.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
