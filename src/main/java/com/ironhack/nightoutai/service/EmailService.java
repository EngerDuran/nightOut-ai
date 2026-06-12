package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.model.Notification;
import com.ironhack.nightoutai.model.User;
import com.ironhack.nightoutai.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j
public class EmailService {


    @Autowired(required = false)
    private JavaMailSender mailSender;

    private final NotificationRepository notificationRepository;

    public EmailService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     *
     * sendEmail, metodo principal para enviar correos
     *
     * @param to      Dirección de correo del destinatario
     * @param subject Asunto del correo
     * @param body    Contenido del correo (puede ser HTML)
     * @param user    Usuario que recibe el correo (para guardar historial)
     * @param type    Tipo de notificación (RECOMMENDATION, PROMOTION, etc.)
     *
     * @return true si se envió correctamente, false si falló
     */
    public boolean sendEmail(String to, String subject, String body, User user, String type) {

        if (mailSender == null) {
            log.warn("⚠️ JavaMailSender no está configurado. " +
                    "Setéa MAIL_USERNAME y MAIL_PASSWORD para enviar correos reales.");
            saveNotification(user, to, subject, body, type, "FAILED");
            return false;
        }

        try {
            // MimeMessage = formato de correo electrónico estándar
            // Soporta HTML, adjuntos, etc. (no como SimpleMailMessage)
            MimeMessage message = mailSender.createMimeMessage();

            // MimeMessageHelper facilitala construcción de MimeMessage
            // (setear destinatario, asunto, cuerpo HTML, etc.)
            // la construcción de MimeMessage
            // (setea destinatario,asunto, cuerpo HTML, etc.)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            // (conexión SMTP real)
            mailSender.send(message);

            log.info("✅ Correo enviado a {}: {}", to, subject);

            saveNotification(user, to, subject, body, type, "SENT");

            return true;

        } catch (Exception e) {

            log.error("❌ Error al enviar correo a {}: {}", to, e.getMessage());

            saveNotification(user, to, subject, body, type, "FAILED");

            return false;
        }
    }


    public boolean sendEmail(String to, String subject, String body, String type) {
        if (mailSender == null) {
            log.warn("⚠️ JavaMailSender no está configurado.");
            saveNotification(null, to, subject, body, type, "FAILED");
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);

            log.info("✅ Correo enviado a {}: {}", to, subject);
            saveNotification(null, to, subject, body, type, "SENT");
            return true;

        } catch (Exception e) {
            log.error("❌ Error al enviar correo a {}: {}", to, e.getMessage());
            saveNotification(null, to, subject, body, type, "FAILED");
            return false;
        }
    }


    public boolean sendSimpleEmail(String to, String subject, String text, User user, String type) {
        try {
            // SimpleMailMessage = formato de texto plano
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            log.info("✅ Correo simple enviado a {}: {}", to, subject);
            saveNotification(user, to, subject, text, type, "SENT");
            return true;

        } catch (Exception e) {
            log.error("❌ Error al enviar correo simple a {}: {}", to, e.getMessage());
            saveNotification(user, to, subject, text, type, "FAILED");
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNotification(User user, String to, String subject,
                                   String body, String type, String status) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setRecipientEmail(to);
        notification.setSubject(subject);
        notification.setBody(body);
        notification.setSentAt(LocalDateTime.now());
        notification.setType(type);
        notification.setStatus(status);

        notificationRepository.save(notification);
    }
}
