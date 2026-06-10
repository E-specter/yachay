package edu.yachay.backend.auth;

import edu.yachay.backend.auth.dto.*;
import edu.yachay.backend.identity.domain.models.*;
import edu.yachay.backend.identity.domain.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.Comparator;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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
