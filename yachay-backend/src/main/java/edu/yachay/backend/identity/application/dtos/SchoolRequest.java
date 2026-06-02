package edu.yachay.backend.identity.application.dtos;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear o actualizar escuelas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolRequest {

    @NotBlank(message = "Nombre de escuela es requerido")
    @Size(min = 2, max = 255)
    private String name;

    @Size(max = 50)
    private String code;

    @Size(max = 255)
    private String address;

    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String logoUrl;

    private Boolean isActive;
}
