package edu.yachay.backend.notification;

import edu.yachay.backend.notification.dto.*;
import edu.yachay.backend.notification.whatsapp.WhatsappRequest;
import edu.yachay.backend.notification.whatsapp.WhatsappService;
import edu.yachay.backend.identity.domain.models.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/notificaciones")
public class NotificationController {

    private final NotificationService notificationService;
    private final PersistentNotificationService persistentNotificationService;
    private final WhatsappService whatsappService;

    public NotificationController(
            NotificationService notificationService,
            PersistentNotificationService persistentNotificationService,
            WhatsappService whatsappService
    ) {
        this.notificationService = notificationService;
        this.persistentNotificationService = persistentNotificationService;
        this.whatsappService = whatsappService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserNotificationResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(persistentNotificationService.listForUser(currentUser(authentication).getId()));
    }

    @PatchMapping("/{id}/leido")
    public ResponseEntity<UserNotificationResponse> markAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(persistentNotificationService.markAsRead(currentUser(authentication).getId(), id));
    }

    @PatchMapping("/leidas")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        persistentNotificationService.markAllAsRead(currentUser(authentication).getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/test")
    public ResponseEntity<NotificationResponse> sendTestEmail(@Valid @RequestBody EmailRequest request) {
        return ResponseEntity.ok(
                notificationService.sendTestEmail(request.to(), request.subject(), request.message())
        );
    }

    @PostMapping("/whatsapp/test")
    public ResponseEntity<NotificationResponse> sendTestWhatsapp(@Valid @RequestBody WhatsappRequest request) {
        return ResponseEntity.ok(
                whatsappService.sendTestMessage(request.to(), request.message())
        );
    }

    private User currentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
