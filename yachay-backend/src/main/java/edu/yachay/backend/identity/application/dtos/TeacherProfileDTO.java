package edu.yachay.backend.identity.application.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * DTO para representar un Perfil de Docente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfileDTO {
    private Integer id;
    private String employeeId;
    private String specialization;
    private LocalDate hireDate;
    private Integer schoolId;
    private String schoolName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
