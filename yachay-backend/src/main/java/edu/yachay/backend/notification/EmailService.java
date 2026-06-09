package edu.yachay.backend.notification;

import edu.yachay.backend.notification.dto.NotificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String mailUsername;
    private final String mailPassword;
    private final String mailFrom;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword,
            @Value("${mail.from:notificaciones@yachay.edu.pe}") String mailFrom
    ) {
        this.mailSender = mailSender;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
        this.mailFrom = mailFrom;
    }

    public NotificationResponse sendEmail(String to, String subject, String message) {
        if (!isMailConfigured()) {
            return new NotificationResponse(
                    false,
                    "Correo no configurado. Defina MAIL_USERNAME y MAIL_PASSWORD en el entorno."
            );
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(mailFrom);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailSender.send(mailMessage);

            return new NotificationResponse(true, "Correo enviado correctamente.");
        } catch (MailException exception) {
            return new NotificationResponse(
                    false,
                    "No se pudo enviar el correo: " + exception.getMessage()
            );
        }
    }

    private boolean isMailConfigured() {
        return StringUtils.hasText(mailUsername) && StringUtils.hasText(mailPassword);
    }
}
