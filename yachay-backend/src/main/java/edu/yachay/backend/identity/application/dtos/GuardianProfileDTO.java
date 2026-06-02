package edu.yachay.backend.identity.application.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para representar un Perfil de Apoderado/Tutor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardianProfileDTO {
    private Integer id;
    private String relationship;
    private String occupation;
    private Set<Integer> studentIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
