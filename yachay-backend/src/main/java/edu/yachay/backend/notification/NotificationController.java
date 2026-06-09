package edu.yachay.backend.notification;

import edu.yachay.backend.notification.dto.EmailRequest;
import edu.yachay.backend.notification.dto.NotificationResponse;
import edu.yachay.backend.notification.whatsapp.WhatsappRequest;
import edu.yachay.backend.notification.whatsapp.WhatsappService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/notificaciones")
public class NotificationController {

    private final NotificationService notificationService;
    private final WhatsappService whatsappService;

    public NotificationController(NotificationService notificationService, WhatsappService whatsappService) {
        this.notificationService = notificationService;
        this.whatsappService = whatsappService;
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
}
