package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.enums.EventStatus;
import com.ironhack.nightoutai.model.*;
import com.ironhack.nightoutai.repository.BookingRepository;
import com.ironhack.nightoutai.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;


    public List<Event> getRecommendedEventsForUser(User user) {


        List<Booking> userBookings = bookingRepository.findByUserId(user.getId());

        if (userBookings.isEmpty()) {
            log.info("Usuario {} no tiene reservas previas", user.getUsername());
            return Collections.emptyList();
        }

        //  Extraer los IDs de las salas que ha visitado

        Set<Long> visitedVenueIds = userBookings.stream()
                .map(booking -> booking.getTicket().getEvent().getVenue().getId())
                .collect(Collectors.toSet());

        log.info("Usuario {} ha visitado {} salas distintas: {}",
                user.getUsername(), visitedVenueIds.size(), visitedVenueIds);


        LocalDateTime now = LocalDateTime.now();
        List<Event> recommended = new ArrayList<>();

        for (Long venueId : visitedVenueIds) {

            List<Event> venueEvents = eventRepository.findByVenueIdAndDateAfter(venueId, now);

            for (Event event : venueEvents) {
                if (event.getStatus() == EventStatus.CONFIRMED
                        || event.getStatus() == EventStatus.SCHEDULED) {
                    recommended.add(event);
                }
            }
        }


        //  Ordenar por fecha (más próximo primero)
        recommended.sort(Comparator.comparing(Event::getDate));

        log.info("Se generaron {} recomendaciones para el usuario {}",
                recommended.size(), user.getUsername());

        return recommended;
    }

    /**
     * buildRecommendationHtml — Genera el HTML del correo
     * @param user       Usuario que recibe las recomendaciones
     * @param events     Lista de eventos recomendados
     * @return String    HTML completo listo para enviar por email
     */
    public String buildRecommendationHtml(User user, List<Event> events) {

        if (events.isEmpty()) {
            return """
                    <html>
                    <body style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2>¡Hola %s!</h2>
                        <p>Esta semana no tenemos eventos recomendados para ti en tus salas habituales.</p>
                        <p>Pero no te preocupes, puedes descubrir nuevos planes en nuestra app.</p>
                        <p style="color: gray;">— El equipo de NightOut AI</p>
                    </body>
                    </html>
                    """.formatted(user.getName());
        }

        StringBuilder eventsHtml = new StringBuilder();

        for (Event event : events) {
            eventsHtml.append("""
                    <div style="border: 1px solid #ddd; border-radius: 10px; padding: 15px; margin: 10px 0;">
                        <h3 style="margin: 0; color: #333;">%s</h3>
                        <p style="margin: 5px 0; color: #666;">
                            📍 %s &nbsp;|&nbsp; 🎵 %s &nbsp;|&nbsp; 👔 %s
                        </p>
                        <p style="margin: 5px 0; color: #666;">
                            📅 %s &nbsp;|&nbsp; 💰 Desde €%.0f
                        </p>
                        <p style="margin: 5px 0;">
                            <strong>Capacidad total:</strong> %d personas
                        </p>
                    </div>
                    """.formatted(
                    event.getName(),
                    event.getVenue().getName(),
                    event.getGenre(),
                    event.getDressCode(),
                    event.getDate().toLocalDate().toString(),
                    getLowestPrice(event),
                    event.getTotalCapacity()
            ));
        }

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                color: white; padding: 30px; border-radius: 10px; text-align: center;">
                        <h1 style="margin: 0;">NightOut AI</h1>
                        <p style="margin: 10px 0 0; opacity: 0.9;">Tus planes para esta semana</p>
                    </div>

                    <h2>¡Hola %s!</h2>
                    <p>Basándonos en tus salas favoritas, tenemos %d eventos que te pueden interesar:</p>

                    %s

                    <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">

                    <p style="color: gray; text-align: center; font-size: 12px;">
                        Recibes este correo porque estás registrado en NightOut AI.
                        <br>No responder a este correo.
                    </p>
                </body>
                </html>
                """.formatted(
                user.getName(),
                events.size(),
                eventsHtml.toString()
        );
    }

    /**
     * getLowestPrice — Calcula el precio mínimo de un evento
     */
    private double getLowestPrice(Event event) {
        // Precios estimados por género musical (los más comunes)
        return switch (event.getGenre().toLowerCase()) {
            case "reggaetón", "reggaeton", "latina" -> 30.0;
            case "rock" -> 35.0;
            case "metal" -> 22.0;
            case "jazz" -> 20.0;
            case "flamenco" -> 28.0;
            case "electrónica", "electronica" -> 25.0;
            case "indie" -> 18.0;
            case "pop" -> 25.0;
            default -> 20.0;
        };
    }
}
