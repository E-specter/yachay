package edu.yachay.backend.identity.application.dtos;

import lombok.*;
import java.time.*;

/**
 * DTO para representar un Perfil en las respuestas de la API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private StudentProfileDTO studentProfile;
    private TeacherProfileDTO teacherProfile;
    private GuardianProfileDTO guardianProfile;
}
