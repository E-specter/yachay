package edu.yachay.backend.identity.application.dtos;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * DTO para solicitudes de creación de nuevos usuarios.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    @NotBlank(message = "Contraseña es requerida")
    @Size(min = 8, message = "Contraseña debe tener mínimo 8 caracteres")
    private String password;

    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Nombre es requerido")
    @Size(min = 2, max = 100)
    private String firstName;

    @NotBlank(message = "Apellido es requerido")
    @Size(min = 2, max = 100)
    private String lastName;

    @Size(max = 500)
    private String avatarUrl;

    private Set<String> roleNames;
}
