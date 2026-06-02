package edu.yachay.backend.identity.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa el perfil específico de un tutor/apoderado.
 * Los tutores son responsables de cero o más estudiantes.
 */
@Entity
@Table(name = "guardian_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardianProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @Column(name = "relationship", length = 50)
    private String relationship;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_guardians",
            joinColumns = @JoinColumn(name = "guardian_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "student_profile_id")
    )
    @Builder.Default
    private Set<StudentProfile> students = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addStudent(StudentProfile student) {
        students.add(student);
    }

    public void removeStudent(StudentProfile student) {
        students.remove(student);
    }
}
