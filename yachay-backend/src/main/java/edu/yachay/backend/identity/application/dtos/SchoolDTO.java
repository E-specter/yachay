package edu.yachay.backend.identity.application.dtos;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para representar una Escuela en las respuestas de la API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolDTO {
    private Integer id;
    private String name;
    private String code;
    private String address;
    private String phone;
    private String logoUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
