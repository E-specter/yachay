package edu.yachay.backend.identity.application.dtos;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.*;

/**
 * DTO para solicitudes de actualización de usuarios.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Email(message = "Email debe ser válido")
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(min = 2, max = 100)
    private String firstName;

    @Size(min = 2, max = 100)
    private String lastName;

    private LocalDate dateOfBirth;

    @Size(max = 500)
    private String avatarUrl;

    private Boolean isActive;
}
