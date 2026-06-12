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
 * NOTIFICATION SCHEDULER — Envía correos automáticos cada semana
 */
@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final UserRepository userRepository;
    private final RecommendationService recommendationService;
    private final EmailService emailService;


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

            String email = user.getUsername();

            if (!recommended.isEmpty()) {
                // Construimos el HTML del correo
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
