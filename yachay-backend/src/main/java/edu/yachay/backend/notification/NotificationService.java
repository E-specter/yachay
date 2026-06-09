package edu.yachay.backend.notification;

import edu.yachay.backend.notification.dto.NotificationResponse;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public NotificationResponse sendTestEmail(String to, String subject, String message) {
        return emailService.sendEmail(to, subject, message);
    }

    public NotificationResponse sendAdmissionCreatedEmail(String guardianEmail, String admissionCode) {
        return emailService.sendEmail(
                guardianEmail,
                "Postulación registrada en Yachay",
                "Su solicitud de admisión fue registrada con el código " + admissionCode + "."
        );
    }

    public NotificationResponse sendAdmissionAcceptedEmail(String guardianEmail, String studentEmail) {
        return emailService.sendEmail(
                guardianEmail,
                "Postulación aceptada",
                "La postulación fue aceptada. El correo institucional asignado al estudiante es: " + studentEmail + "."
        );
    }

    public NotificationResponse sendAdmissionRejectedEmail(String guardianEmail, String reason) {
        return emailService.sendEmail(
                guardianEmail,
                "Postulación revisada",
                "La postulación fue revisada y no fue aceptada. Motivo: " + reason + "."
        );
    }
}
