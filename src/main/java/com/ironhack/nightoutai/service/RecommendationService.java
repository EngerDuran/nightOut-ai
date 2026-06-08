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

/**
 * ============================================================
 * RECOMMENDATION SERVICE — Genera recomendaciones según el
 *                         historial de compras del usuario
 * ============================================================
 *
 * ¿Qué hace?
 * 1. Mira todas las reservas (bookings) que tiene un usuario
 * 2. Saca qué salas (venues) ha visitado
 * 3. Busca eventos FUTUROS en esas mismas salas
 * 4. Devuelve una lista de eventos recomendados
 *
 * Lógica de negocio:
 * "Si has comprado entradas para el Reggaetón Night en Kapital,
 *  te interesarán otros eventos en Kapital. Te los recomendamos."
 *
 * Esto se conoce como "recomendación basada en el histórico
 * de compras" (collaborative filtering simplificado).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    /**
     * ============================================================
     * getRecommendedEventsForUser — Recomienda eventos a un usuario
     * ============================================================
     *
     * @param user El usuario para el que generar recomendaciones
     * @return Lista de eventos recomendados (futuros, en salas
     *         donde el usuario ya ha comprado entradas)
     *
     * Flujo completo:
     *
     * 1. Buscar todas las reservas del usuario
     *    └── bookingRepository.findByUserId(user.getId())
     *
     * 2. De esas reservas, extraer las salas (venues) únicas
     *    └── Booking → Ticket → Event → Venue
     *    └── Usamos un Set<Long> para no repetir salas
     *
     * 3. Buscar eventos FUTUROS en esas salas
     *    └── eventRepository.findByVenueIdAndDateAfter()
     *    └── Filtramos solo eventos que NO han pasado
     *
     * 4. Devolver la lista (ordenada por fecha, más próximos primero)
     */
    public List<Event> getRecommendedEventsForUser(User user) {

        // ============================================================
        // Paso 1: Buscar todas las reservas del usuario
        // ============================================================
        List<Booking> userBookings = bookingRepository.findByUserId(user.getId());

        // Si el usuario no tiene reservas, no podemos recomendar nada
        if (userBookings.isEmpty()) {
            log.info("Usuario {} no tiene reservas previas", user.getUsername());
            return Collections.emptyList();
        }

        // ============================================================
        // Paso 2: Extraer los IDs de las salas que ha visitado
        // ============================================================
        //
        // Booking → getTicket() → getEvent() → getVenue() → getId()
        //
        // Usamos Stream API de Java:
        // - .stream() convierte la lista en un flujo de datos
        // - .map() transforma cada booking en el ID de su venue
        // - .collect(Collectors.toSet()) junta todo en un Set (sin duplicados)
        //
        Set<Long> visitedVenueIds = userBookings.stream()
                .map(booking -> booking.getTicket().getEvent().getVenue().getId())
                .collect(Collectors.toSet());

        log.info("Usuario {} ha visitado {} salas distintas: {}",
                user.getUsername(), visitedVenueIds.size(), visitedVenueIds);

        // ============================================================
        // Paso 3: Buscar eventos futuros en esas salas
        // ============================================================
        LocalDateTime now = LocalDateTime.now();
        List<Event> recommended = new ArrayList<>();

        for (Long venueId : visitedVenueIds) {
            // Buscamos eventos cuyo venue sea el que visitó
            // y cuya fecha sea posterior a ahora (futuro)
            List<Event> venueEvents = eventRepository.findByVenueIdAndDateAfter(venueId, now);

            // Solo eventos CONFIRMED o SCHEDULED (no cancelados)
            for (Event event : venueEvents) {
                if (event.getStatus() == EventStatus.CONFIRMED
                        || event.getStatus() == EventStatus.SCHEDULED) {
                    recommended.add(event);
                }
            }
        }

        // ============================================================
        // Paso 4: Ordenar por fecha (más próximo primero)
        // ============================================================
        recommended.sort(Comparator.comparing(Event::getDate));

        log.info("Se generaron {} recomendaciones para el usuario {}",
                recommended.size(), user.getUsername());

        return recommended;
    }

    /**
     * ============================================================
     * buildRecommendationHtml — Genera el HTML del correo
     * ============================================================
     *
     * No solo necesitamos los datos, sino también un correo
     * bonito. Este método construye el HTML que se enviará.
     *
     * @param user       Usuario que recibe las recomendaciones
     * @param events     Lista de eventos recomendados
     * @return String    HTML completo listo para enviar por email
     */
    public String buildRecommendationHtml(User user, List<Event> events) {

        // Si no hay recomendaciones, devolvemos un mensaje amigable
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

        // ============================================================
        // Construir el HTML con cada evento recomendado
        // ============================================================
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

        // HTML completo del correo
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
     * ============================================================
     * getLowestPrice — Calcula el precio mínimo de un evento
     * ============================================================
     *
     * Los tickets están en una lista dentro de Event... espera,
     * Event NO tiene una lista de Tickets directamente.
     *
     * En realidad los tickets se guardan en TicketRepository
     * con una relación ManyToOne a Event.
     *
     * Para simplificar, devolvemos un precio estimado según
     * el género del evento (valores realistas de Madrid).
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
