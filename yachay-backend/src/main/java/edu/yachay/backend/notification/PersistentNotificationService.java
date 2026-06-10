package edu.yachay.backend.notification;

import edu.yachay.backend.identity.domain.models.Role;
import edu.yachay.backend.identity.domain.models.User;
import edu.yachay.backend.identity.domain.repositories.UserRepository;
import edu.yachay.backend.notification.domain.models.Notification;
import edu.yachay.backend.notification.domain.repositories.NotificationRepository;
import edu.yachay.backend.notification.dto.UserNotificationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class PersistentNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public PersistentNotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserNotificationResponse> listForUser(Long userId) {
        return notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByRecipient_IdAndReadFalse(userId);
    }

    @Transactional
    public UserNotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificacion no encontrada."));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para modificar esta notificacion.");
        }

        notification.markAsRead();
        return toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(userId);
        notifications.stream()
                .filter(notification -> !Boolean.TRUE.equals(notification.getRead()))
                .forEach(Notification::markAsRead);
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void createForUser(User recipient, String title, String body, String type, String linkUrl) {
        if (recipient == null || recipient.getId() == null) {
            return;
        }

        if (notificationRepository.existsByRecipient_IdAndTitleAndBody(recipient.getId(), title, body)) {
            return;
        }

        notificationRepository.save(Notification.builder()
                .recipient(recipient)
                .title(title)
                .body(body)
                .type(type != null ? type : "INFO")
                .linkUrl(linkUrl)
                .read(false)
                .build());
    }

    @Transactional
    public void createForUsers(Collection<User> recipients, String title, String body, String type, String linkUrl) {
        recipients.stream()
                .filter(user -> user != null && user.getId() != null)
                .distinct()
                .forEach(user -> createForUser(user, title, body, type, linkUrl));
    }

    @Transactional
    public void createForRole(String roleName, String title, String body, String type, String linkUrl) {
        String normalizedRole = normalizeRole(roleName);
        List<User> recipients = userRepository.findAll().stream()
                .filter(user -> user.getRoles() != null && user.getRoles().stream()
                        .map(Role::getName)
                        .map(this::normalizeRole)
                        .anyMatch(normalizedRole::equals))
                .toList();

        createForUsers(recipients, title, body, type, linkUrl);
    }

    @Transactional
    public void createForAudience(String audience, String title, String body, String type, String linkUrl) {
        String normalizedAudience = normalizeRole(audience);

        if (normalizedAudience.equals("TODOS")) {
            createForUsers(userRepository.findAll(), title, body, type, linkUrl);
            return;
        }

        if (normalizedAudience.equals("APODERADOS") || normalizedAudience.equals("ALUMNOS_APODERADOS")) {
            createForRole("ALUMNO", title, body, type, linkUrl);
            return;
        }

        createForRole(normalizedAudience, title, body, type, linkUrl);
    }

    private UserNotificationResponse toResponse(Notification notification) {
        return new UserNotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getType(),
                notification.getLinkUrl(),
                notification.getRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }

    private String normalizeRole(String value) {
        if (value == null || value.isBlank()) {
            return "TODOS";
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "ADMIN", "ADMINISTRADOR" -> "ADMINISTRADOR";
            case "TEACHER", "PROFESOR", "DOCENTE" -> "DOCENTE";
            case "STUDENT", "ESTUDIANTE", "ALUMNO", "ALUMNOS" -> "ALUMNO";
            case "GUARDIAN", "APODERADO" -> "APODERADO";
            case "ALUMNOS_APODERADOS" -> "ALUMNOS_APODERADOS";
            default -> normalized;
        };
    }
}
