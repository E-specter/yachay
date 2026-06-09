package edu.yachay.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "El token es obligatorio.")
        String token,

        @NotBlank(message = "La contrasena es obligatoria.")
        String password,

        @NotBlank(message = "La confirmacion es obligatoria.")
        String confirmPassword
) {
}
