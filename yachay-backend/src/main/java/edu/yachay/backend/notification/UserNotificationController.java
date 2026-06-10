package edu.yachay.backend.notification;

import edu.yachay.backend.identity.domain.models.User;
import edu.yachay.backend.notification.dto.UserNotificationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserNotificationController {

    private final PersistentNotificationService persistentNotificationService;

    public UserNotificationController(PersistentNotificationService persistentNotificationService) {
        this.persistentNotificationService = persistentNotificationService;
    }

    @GetMapping({"/docente/notificaciones", "/alumno/notificaciones"})
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserNotificationResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(persistentNotificationService.listForUser(currentUser(authentication).getId()));
    }

    @PatchMapping({"/docente/notificaciones/{id}/leido", "/alumno/notificaciones/{id}/leido"})
    public ResponseEntity<UserNotificationResponse> markAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(persistentNotificationService.markAsRead(currentUser(authentication).getId(), id));
    }

    @PatchMapping({"/docente/notificaciones/leidas", "/alumno/notificaciones/leidas"})
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        persistentNotificationService.markAllAsRead(currentUser(authentication).getId());
        return ResponseEntity.noContent().build();
    }

    private User currentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
