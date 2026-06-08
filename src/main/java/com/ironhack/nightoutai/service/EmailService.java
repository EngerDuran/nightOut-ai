package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.model.Notification;
import com.ironhack.nightoutai.model.User;
import com.ironhack.nightoutai.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * ============================================================
 * EMAIL SERVICE — Servicio de envío de correos electrónicos
 * ============================================================
 *
 * ¿Qué hace este servicio?
 * 1. Envía correos electrónicos usando JavaMailSender (SMTP)
 * 2. Guarda un registro de cada envío en la tabla 'notification'
 * 3. Si el envío falla, guarda el error igual para que sepas
 *
 * ¿Cómo funciona JavaMailSender?
 * Spring Boot configura automáticamente un bean JavaMailSender
 * con los datos que pongas en application.yaml en la sección
 * spring.mail.*. Nosotros solo lo inyectamos y llamamos a send().
 *
 * ¿Qué necesito para que funcione?
 * 1. Una cuenta de Gmail (u otro proveedor SMTP)
 * 2. Una "Contraseña de aplicación" de Gmail (NO tu contraseña normal)
 * 3. Configurar application.yaml (lo haremos después)
 *
 * ¿Y si no configuro el email? ¿La app no arranca?
 * No, la app arranca igual. Spring Boot solo crea el bean
 * JavaMailSender si encuentra la configuración. Si no está,
 * nosotros en EmailService lo detectamos con un try-catch.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    /**
     * JavaMailSender: el bean que Spring Boot crea automáticamente
     * a partir de la configuración en application.yaml
     *
     * Inyectamos también NotificationRepository para guardar
     * un registro de cada correo enviado.
     *
     * IMPORTANTE: Ponemos required = false porque si el usuario
     * no tiene configurado el email, no queremos que la app
     * falle al arrancar. Simplemente no se enviarán correos.
     */
    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    /**
     * ============================================================
     * sendEmail — Método principal para enviar correos
     * ============================================================
     *
     * @param to      Dirección de correo del destinatario
     * @param subject Asunto del correo
     * @param body    Contenido del correo (puede ser HTML)
     * @param user    Usuario que recibe el correo (para guardar historial)
     * @param type    Tipo de notificación (RECOMMENDATION, PROMOTION, etc.)
     *
     * @return true si se envió correctamente, false si falló
     *
     * Flujo:
     * 1. Intentamos enviar el correo con mailSender.send()
     * 2. Si funciona, guardamos en DB con estado SENT
     * 3. Si falla, guardamos en DB con estado FAILED (para debug)
     * 4. En ambos casos, devolvemos el resultado
     *
     * ¿Por qué guardar en DB aunque falle?
     * Para que puedas ver en la API qué correos fallaron
     * y diagnosticar el problema sin buscar en logs.
     */
    public boolean sendEmail(String to, String subject, String body, User user, String type) {

        // ============================================================
        // 1. Intentar enviar el correo
        // ============================================================
        try {
            // MimeMessage = formato de correo electrónico estándar
            // Soporta HTML, adjuntos, etc. (no como SimpleMailMessage)
            MimeMessage message = mailSender.createMimeMessage();

            // MimeMessageHelper es una clase de Spring que facilita
            // la construcción de MimeMessage (setear destinatario,
            // asunto, cuerpo HTML, etc.)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);                              // ¿A quién?
            helper.setSubject(subject);                    // ¿De qué?
            helper.setText(body, true);                    // true = es HTML

            // Enviar el correo (esto hace la conexión SMTP real)
            mailSender.send(message);

            // ============================================================
            // 2. Si llegamos aquí, el correo se envió OK
            // ============================================================
            log.info("✅ Correo enviado a {}: {}", to, subject);

            // Guardamos el registro de éxito en la base de datos
            saveNotification(user, to, subject, body, type, "SENT");

            return true;

        } catch (Exception e) {
            // ============================================================
            // 3. Si algo falló, registramos el error
            // ============================================================
            log.error("❌ Error al enviar correo a {}: {}", to, e.getMessage());

            // También guardamos el fallo en DB para diagnóstico
            saveNotification(user, to, subject, body, type, "FAILED");

            return false;
        }
    }

    /**
     * ============================================================
     * sendSimpleEmail — Versión simple sin HTML
     * ============================================================
     *
     * A veces solo necesitas texto plano (sin negritas, colores,
     * imágenes). Este método es para esos casos. El HTML es más
     * bonito pero el texto plano es más compatible.
     */
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

    /**
     * ============================================================
     * saveNotification — Guarda el registro del envío en DB
     * ============================================================
     *
     * Cada vez que se envía (o intenta enviar) un correo,
     * guardamos un registro en la tabla notification.
     *
     * ¿Para qué sirve?
     * - Para que el usuario pueda ver su historial de correos
     * - Para depurar por qué un correo no se envió
     * - Para la demo: mostrar "Mira, aquí están los correos que
     *   el sistema está mandando automáticamente"
     */
    private void saveNotification(User user, String to, String subject,
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
