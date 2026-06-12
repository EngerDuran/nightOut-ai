package com.ironhack.nightoutai.controller;

import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.Notification;
import com.ironhack.nightoutai.model.User;
import com.ironhack.nightoutai.repository.NotificationRepository;
import com.ironhack.nightoutai.repository.UserRepository;
import com.ironhack.nightoutai.service.EmailService;
import com.ironhack.nightoutai.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;
    private final RecommendationService recommendationService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


    @GetMapping("/test-recommend")
    public ResponseEntity<String> testRecommendation() {

        // Obtener el usuario autenticado desde el token JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        // Generar recomendaciones
        List<Event> recommended = recommendationService.getRecommendedEventsForUser(user);

        // Construir HTML
        String htmlBody = recommendationService.buildRecommendationHtml(user, recommended);

        boolean sent = emailService.sendEmail(
                user.getUsername(),
                "🎉 NightOut AI — Tus recomendaciones personalizadas",
                htmlBody,
                user,
                "RECOMMENDATION"
        );

        if (sent) {
            return ResponseEntity.ok("✅ Correo enviado a " + user.getUsername()
                    + " con " + recommended.size() + " recomendaciones.");
        } else {
            return ResponseEntity.ok("⚠️ El correo NO se pudo enviar (revisá la config SMTP). "
                    + "Pero el intento quedó registrado en la base de datos.");
        }
    }


    @GetMapping("/my-history")
    public ResponseEntity<List<Notification>> getMyHistory() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            // Si el usuario no existe en BD, buscar por email directamente
            List<Notification> byEmail = notificationRepository.findByRecipientEmail(username);
            return ResponseEntity.ok(byEmail);
        }

        List<Notification> byUser = notificationRepository.findByUserId(user.getId());
        List<Notification> byEmail = notificationRepository.findByRecipientEmail(username);

        // Combinar ambas listas sin duplicados
        java.util.Set<Long> seenIds = new java.util.HashSet<>();
        java.util.List<Notification> combined = new java.util.ArrayList<>();

        for (Notification n : byUser) {
            seenIds.add(n.getId());
            combined.add(n);
        }
        for (Notification n : byEmail) {
            if (!seenIds.contains(n.getId())) {
                combined.add(n);
            }
        }

        combined.sort((a, b) -> b.getSentAt().compareTo(a.getSentAt()));

        return ResponseEntity.ok(combined);
    }

    /**
     * ============================================================
     * 3. TODO EL HISTORIAL — Solo administradores
     * ============================================================
     *
     * Muestra todos los correos enviados por el sistema a todos
     * los usuarios. Útil para diagnóstico y la demo general.
     *
     * Cómo probarlo en Postman:
     * GET localhost:8080/api/notifications/all
     * Authorization: Bearer {{token_admin}}
     */
    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> all = notificationRepository.findAllByOrderBySentAtDesc();
        return ResponseEntity.ok(all);
    }

    /**
     * ============================================================
     * 4. DISPARAR ENVÍO SEMANAL — Modo "simular lunes"
     * ============================================================
     *
     * Normalmente el envío semanal ocurre automáticamente los
     * lunes a las 10 AM. Pero si quieres probarlo en medio de
     * la semana, este endpoint lo dispara manualmente.
     *
     * Cómo probarlo en Postman:
     * POST localhost:8080/api/notifications/trigger-weekly
     * Authorization: Bearer {{token_admin}}
     */
    @PostMapping("/trigger-weekly")
    public ResponseEntity<String> triggerWeekly() {

        List<User> users = userRepository.findAll();
        int sentCount = 0;

        for (User user : users) {
            List<Event> recommended = recommendationService.getRecommendedEventsForUser(user);

            if (!recommended.isEmpty()) {
                String htmlBody = recommendationService.buildRecommendationHtml(user, recommended);
                boolean sent = emailService.sendEmail(
                        user.getUsername(),
                        "🎉 NightOut AI — Tus planes para esta semana",
                        htmlBody,
                        user,
                        "RECOMMENDATION"
                );
                if (sent) sentCount++;
            }
        }

        return ResponseEntity.ok(
                "✅ Envío semanal manual completado. "
                        + sentCount + " correos enviados de " + users.size() + " usuarios."
        );
    }
}
