package edu.yachay.backend.auth;

import edu.yachay.backend.auth.dto.AuthUserResponse;
import edu.yachay.backend.auth.dto.LoginRequest;
import edu.yachay.backend.auth.dto.LoginResponse;
import edu.yachay.backend.identity.domain.models.Profile;
import edu.yachay.backend.identity.domain.models.Role;
import edu.yachay.backend.identity.domain.models.User;
import edu.yachay.backend.identity.domain.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        return new LoginResponse(createToken(), toAuthUser(user));
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.");
    }

    private String createToken() {
        return "yachay-" + UUID.randomUUID();
    }

    private AuthUserResponse toAuthUser(User user) {
        Profile profile = user.getProfile();
        String firstName = profile != null ? profile.getFirstName() : user.getDisplayName();
        String lastName = profile != null ? profile.getLastName() : "";

        return new AuthUserResponse(
                user.getId(),
                firstName != null ? firstName : "",
                lastName != null ? lastName : "",
                user.getEmail(),
                resolveRole(user)
        );
    }

    private String resolveRole(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .filter(name -> name != null && !name.isBlank())
                .map(this::normalizeRole)
                .filter(name -> name.equals("ADMINISTRADOR") || name.equals("DOCENTE") || name.equals("ALUMNO"))
                .findFirst()
                .orElseGet(() -> user.getRoles().stream()
                        .map(Role::getName)
                        .filter(name -> name != null && !name.isBlank())
                        .sorted(Comparator.naturalOrder())
                        .findFirst()
                        .map(this::normalizeRole)
                        .orElse("ALUMNO"));
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
