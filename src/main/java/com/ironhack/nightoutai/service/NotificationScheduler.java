package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.User;
import com.ironhack.nightoutai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ============================================================
 * NOTIFICATION SCHEDULER — Envía correos automáticos cada semana
 * ============================================================
 *
 * ¿Qué hace?
 * - Cada lunes a las 10:00 AM, revisa TODOS los usuarios
 * - Para cada usuario, busca eventos recomendados según su
 *   historial de compras
 * - Si hay recomendaciones, envía un correo con los planes
 *
 * ¿Cómo funciona @Scheduled?
 * Es una anotación de Spring que ejecuta un método en un
 * horario específico. La expresión "cron" define cuándo:
 *
 *   ┌───────────── segundo (0-59)
 *   │  ┌───────────── minuto (0-59)
 *   │  │  ┌───────────── hora (0-23)
 *   │  │  │  ┌───────────── día del mes (1-31)
 *   │  │  │  │  ┌───────────── mes (1-12)
 *   │  │  │  │  │  ┌───────────── día de la semana (0-7, 0=domingo)
 *   │  │  │  │  │  │
 *   │  │  │  │  │  │
 *   0  0  10  *  *  1    → "A las 10:00 AM, solo los lunes"
 *
 * ¿Por qué @EnableScheduling en la clase?
 * Spring necesita que le digamos explícitamente
 * "voy a usar @Scheduled". Con esta anotación lo hacemos.
 *
 * ¿Para qué sirve en la demo?
 * Durante la presentación, puedes explicar:
 * "Cada lunes, el sistema analiza el historial de compras
 *  de los usuarios y les envía un correo personalizado con
 *  eventos recomendados en sus salas favoritas."
 *
 * Y para probarlo en vivo, puedes hacer clic en "test manual"
 * en lugar de esperar al lunes.
 */
@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final UserRepository userRepository;
    private final RecommendationService recommendationService;
    private final EmailService emailService;

    /**
     * ============================================================
     * sendWeeklyRecommendations — Tarea automática semanal
     * ============================================================
     *
     * @Scheduled(cron = "0 0 10 * * MON")
     * Se ejecuta: Todos los lunes a las 10:00 AM
     *
     * ¿Qué hace?
     * 1. Obtiene todos los usuarios de la base de datos
     * 2. Para cada usuario, genera recomendaciones
     * 3. Si hay recomendaciones, envía un correo
     *
     * ¿Los lunes a las 10 AM? Sí, porque es cuando la gente
     * planifica su fin de semana. Esto es marketing real.
     */
    @Scheduled(cron = "0 0 10 * * MON")
    public void sendWeeklyRecommendations() {

        log.info("📬 Iniciando envío semanal de recomendaciones...");

        // Obtener todos los usuarios registrados
        List<User> users = userRepository.findAll();

        int sentCount = 0;
        int skippedCount = 0;

        for (User user : users) {

            // Generar recomendaciones para este usuario
            List<Event> recommended = recommendationService.getRecommendedEventsForUser(user);

            // El correo del usuario es su username (en este proyecto
            // el username es el email, ej: "carlos.perez@gmail.com")
            String email = user.getUsername();

            if (!recommended.isEmpty()) {
                // Construir el HTML del correo
                String htmlBody = recommendationService.buildRecommendationHtml(user, recommended);

                // Enviar el correo
                boolean sent = emailService.sendEmail(
                        email,
                        "🎉 NightOut AI — Tus planes para esta semana",
                        htmlBody,
                        user,
                        "RECOMMENDATION"
                );

                if (sent) {
                    sentCount++;
                }

            } else {
                // Si no hay recomendaciones, no enviamos correo
                // (evitamos spam innecesario)
                log.debug("Usuario {} no tiene recomendaciones, se omite", email);
                skippedCount++;
            }
        }

        log.info("📬 Envío semanal completado: {} enviados, {} omitidos",
                sentCount, skippedCount);
    }
}
