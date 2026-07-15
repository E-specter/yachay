package edu.yachay.backend.auth;

import edu.yachay.backend.auth.dto.ForgotPasswordRequest;
import edu.yachay.backend.auth.dto.ResetPasswordRequest;
import edu.yachay.backend.identity.domain.repositories.UserRepository;
import edu.yachay.backend.notification.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Test
    void forgotPasswordDoesNotRevealUnknownEmail() {
        UserRepository users = mock(UserRepository.class);
        PasswordResetTokenRepository tokens = mock(PasswordResetTokenRepository.class);
        EmailService email = mock(EmailService.class);
        when(users.findByEmail("unknown@yachay.edu.pe")).thenReturn(Optional.empty());
        AuthService service = new AuthService(users, mock(JwtService.class), mock(PasswordEncoder.class), tokens, email, "http://localhost:4200");

        service.forgotPassword(new ForgotPasswordRequest("unknown@yachay.edu.pe"));

        verify(tokens, never()).save(any());
        verifyNoInteractions(email);
    }

    @Test
    void resetPasswordRejectsMismatchBeforeDatabaseLookup() {
        PasswordResetTokenRepository tokens = mock(PasswordResetTokenRepository.class);
        AuthService service = new AuthService(mock(UserRepository.class), mock(JwtService.class), mock(PasswordEncoder.class), tokens, mock(EmailService.class), "http://localhost:4200");

        assertThatThrownBy(() -> service.resetPassword(new ResetPasswordRequest("token", "Password1", "Password2")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verifyNoInteractions(tokens);
    }
}
