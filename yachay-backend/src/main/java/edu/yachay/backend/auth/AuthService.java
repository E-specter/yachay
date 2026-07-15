package edu.yachay.backend.auth;

import edu.yachay.backend.auth.dto.*;
import edu.yachay.backend.identity.domain.models.*;
import edu.yachay.backend.identity.domain.repositories.UserRepository;
import edu.yachay.backend.notification.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final EmailService emailService;
    private final String frontendUrl;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder,
                       PasswordResetTokenRepository resetTokenRepository, EmailService emailService,
                       @Value("${app.frontend-url:http://localhost:4200}") String frontendUrl) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.resetTokenRepository = resetTokenRepository;
        this.emailService = emailService;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> invalidCredentials());

        if (!passwordEncoder.matches(request.password(), user.getEncryptedPassword())) {
            throw invalidCredentials();
        }

        user.setLastSignInAt(LocalDateTime.now());
        userRepository.save(user);

        List<String> roles = resolveRoles(user);
        return new LoginResponse(jwtService.generateToken(user, roles), toAuthUser(user, roles));
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email().trim().toLowerCase()).ifPresent(user -> {
            resetTokenRepository.deleteByUser_IdAndUsedAtIsNull(user.getId());
            String token = UUID.randomUUID().toString() + UUID.randomUUID();
            resetTokenRepository.save(PasswordResetToken.builder().user(user).tokenHash(hash(token))
                    .expiresAt(LocalDateTime.now().plusMinutes(30)).build());
            emailService.sendEmail(user.getEmail(), "Restablecer contraseña Yachay",
                    "Abre este enlace durante los próximos 30 minutos: " + frontendUrl + "/reset-password?token=" + token);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.password().equals(request.confirmPassword()) || request.password().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe coincidir y tener al menos 8 caracteres.");
        }
        PasswordResetToken token = resetTokenRepository.findByTokenHashAndUsedAtIsNull(hash(request.token()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalido o ya utilizado."));
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token ha expirado.");
        }
        token.getUser().setEncryptedPassword(passwordEncoder.encode(request.password()));
        userRepository.save(token.getUser());
        token.setUsedAt(LocalDateTime.now());
        resetTokenRepository.save(token);
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no disponible.", exception);
        }
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.");
    }

    private AuthUserResponse toAuthUser(User user, List<String> roles) {
        Profile profile = user.getProfile();
        String firstName = profile != null ? profile.getFirstName() : user.getDisplayName();
        String lastName = profile != null ? profile.getLastName() : "";
        String safeFirstName = firstName != null ? firstName : "";
        String safeLastName = lastName != null ? lastName : "";

        return new AuthUserResponse(
                user.getId(),
                safeFirstName,
                safeLastName,
                user.getEmail(),
                resolvePrimaryRole(roles),
                (safeFirstName + " " + safeLastName).trim(),
                roles
        );
    }

    private List<String> resolveRoles(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .filter(name -> name != null && !name.isBlank())
                .map(this::normalizeRole)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();

        return roles.isEmpty() ? List.of("ALUMNO") : roles;
    }

    private String resolvePrimaryRole(List<String> roles) {
        if (roles.contains("ADMINISTRADOR")) return "ADMINISTRADOR";
        if (roles.contains("DOCENTE")) return "DOCENTE";
        if (roles.contains("ALUMNO")) return "ALUMNO";
        return roles.stream().findFirst().orElse("ALUMNO");
    }

    private String normalizeRole(String roleName) {
        String normalized = roleName.trim().toUpperCase();

        if (normalized.equals("ADMIN") || normalized.equals("ADMINISTRADOR")) {
            return "ADMINISTRADOR";
        }

        if (normalized.equals("TEACHER") || normalized.equals("PROFESOR") || normalized.equals("DOCENTE")) {
            return "DOCENTE";
        }

        if (normalized.equals("STUDENT") || normalized.equals("ESTUDIANTE") || normalized.equals("ALUMNO")) {
            return "ALUMNO";
        }

        return normalized;
    }
}
