package edu.yachay.backend.auth;

import edu.yachay.backend.identity.domain.models.Role;
import edu.yachay.backend.identity.domain.models.User;
import edu.yachay.backend.identity.domain.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        return path.equals("/auth/login")
                || path.equals("/auth/forgot-password")
                || path.equals("/auth/reset-password")
                || path.equals("/admisiones");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            String email = jwtService.extractSubject(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new JwtService.InvalidJwtException("Usuario del token no existe.", null));

                if (jwtService.isValidForUser(token, user)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities(user)
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);
        } catch (JwtService.InvalidJwtException ex) {
            SecurityContextHolder.clearContext();
            unauthorized(response);
        }
    }

    private List<SimpleGrantedAuthority> authorities(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .filter(name -> name != null && !name.isBlank())
                .map(this::normalizeRole)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private String normalizeRole(String roleName) {
        String normalized = roleName.trim().toUpperCase();
        if (normalized.equals("ADMIN") || normalized.equals("ADMINISTRADOR")) return "ADMINISTRADOR";
        if (normalized.equals("TEACHER") || normalized.equals("PROFESOR") || normalized.equals("DOCENTE")) return "DOCENTE";
        if (normalized.equals("STUDENT") || normalized.equals("ESTUDIANTE") || normalized.equals("ALUMNO")) return "ALUMNO";
        return normalized;
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token JWT invalido o expirado.\"}");
    }
}
