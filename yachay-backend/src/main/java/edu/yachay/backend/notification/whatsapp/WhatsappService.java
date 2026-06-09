package edu.yachay.backend.notification.whatsapp;

import edu.yachay.backend.notification.dto.NotificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WhatsappService {

    private final boolean enabled;
    private final String token;
    private final String phoneNumberId;

    public WhatsappService(
            @Value("${whatsapp.enabled:false}") boolean enabled,
            @Value("${whatsapp.token:}") String token,
            @Value("${whatsapp.phone-number-id:}") String phoneNumberId
    ) {
        this.enabled = enabled;
        this.token = token;
        this.phoneNumberId = phoneNumberId;
    }

    public NotificationResponse sendTestMessage(String to, String message) {
        if (!enabled) {
            return new NotificationResponse(true, "WhatsApp desactivado por configuracion.");
        }

        if (!StringUtils.hasText(token) || !StringUtils.hasText(phoneNumberId)) {
            return new NotificationResponse(false, "WhatsApp activo, pero faltan WHATSAPP_TOKEN o WHATSAPP_PHONE_NUMBER_ID.");
        }

        return new NotificationResponse(true, "WhatsApp Cloud API preparada para enviar mensaje a " + to + ".");
    }
}
