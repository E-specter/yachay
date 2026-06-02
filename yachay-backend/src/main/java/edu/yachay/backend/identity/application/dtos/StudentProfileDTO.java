package edu.yachay.backend.identity.application.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * DTO para representar un Perfil de Estudiante.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileDTO {
    private Integer id;
    private String studentCode;
    private Short gradeLevel;
    private String section;
    private LocalDate enrollmentDate;
    private Integer schoolId;
    private String schoolName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
