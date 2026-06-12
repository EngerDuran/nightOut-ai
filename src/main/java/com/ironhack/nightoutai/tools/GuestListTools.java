package com.ironhack.nightoutai.tools;

import com.ironhack.nightoutai.enums.GuestListStatus;
import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.GuestList;
import com.ironhack.nightoutai.model.User;
import com.ironhack.nightoutai.repository.EventRepository;
import com.ironhack.nightoutai.repository.GuestListRepository;
import com.ironhack.nightoutai.repository.UserRepository;
import com.ironhack.nightoutai.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GuestListTools {

    private final GuestListRepository guestListRepository;
    private final EventRepository eventRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Tool(description = """
            Register one or more guests on the VIP or free-entry guest list for a specific event.
            ALWAYS ask for their email addresses first so you can send each person a confirmation email.
            If they don't provide emails, you can still register them without confirmation.
            Returns a summary of who was registered and who received email confirmation.
            """)
    public String addToGuestList(
            @ToolParam(description = """
                    List of guest names to register. Extract ALL names from the user's message.
                    The user may include themselves. Example: ['Carlos Pérez', 'Lucía Ortiz']
                    """)
            List<String> guestNames,

            @ToolParam(description = """
                    Optional: email addresses for each guest, in the SAME ORDER as guestNames.
                    If the user provides emails, extract them here.
                    Pass an empty list or null if no emails were given.
                    Example: ['carlos.perez@gmail.com', 'lucia.ortiz@gmail.com']
                    """)
            List<String> guestEmails,

            @ToolParam(description = """
                    The name of the event they want to attend.
                    Match it to an existing event name in the database.
                    Example: 'Reggaetón Night en Teatro Kapital'
                    """)
            String eventName,

            @ToolParam(description = """
                    true = free entry (lista gratis), false = discount only (descuento en puerta).
                    """)
            boolean freeEntry) {

        // Buscar el evento por nombre (case-insensitive)
        Event event = eventRepository.findByName(eventName)
                .orElseGet(() -> {
                    List<Event> allEvents = eventRepository.findAll();
                    return allEvents.stream()
                            .filter(e -> e.getName().toLowerCase().contains(eventName.toLowerCase())
                                    || eventName.toLowerCase().contains(e.getName().toLowerCase()))
                            .findFirst()
                            .orElse(null);
                });

        if (event == null) {
            return "❌ Lo siento, no encontré ningún evento llamado \"" + eventName + "\". "
                    + "¿Podrías decirme el nombre exacto del evento al que quieres ir?";
        }

        if (guestNames == null || guestNames.isEmpty()) {
            return "❌ No me diste nombres para apuntar. ¿Quiénes van a la lista?";
        }

        boolean hasEmails = guestEmails != null && guestEmails.stream().anyMatch(e -> e != null && !e.trim().isEmpty());
        List<String> emailResults = new ArrayList<>();

        // Registrar cada invitado y enviar emails individuales
        int count = 0;
        for (int i = 0; i < guestNames.size(); i++) {
            String trimmed = guestNames.get(i).trim();
            if (trimmed.isEmpty()) continue;

            GuestList entry = new GuestList();
            entry.setGuestName(trimmed);
            entry.setEvent(event);
            entry.setRegisteredBy("RRPP Virtual");
            entry.setRegistrationDate(LocalDateTime.now());
            entry.setStatus(GuestListStatus.CONFIRMED);
            entry.setFreeEntry(freeEntry);

            // Guardar email si se proporcionó para este invitado
            String email = (guestEmails != null && i < guestEmails.size())
                    ? (guestEmails.get(i) != null ? guestEmails.get(i).trim() : null)
                    : null;
            if (email != null && !email.isEmpty()) {
                entry.setGuestEmail(email);
            }

            guestListRepository.save(entry);
            count++;

            // Enviar email individual a este invitado si tenemos su email
            if (email != null && !email.isEmpty()) {
                try {
                    String guestSubject = "🎉 Confirmación — " + event.getName();
                    String venueName = event.getVenue() != null ? event.getVenue().getName() : "Por confirmar";
                    String venueLocation = event.getVenue() != null ? event.getVenue().getLocation() : "";
                    String formattedDate = event.getDate() != null
                            ? event.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
                            : "Por confirmar";
                    String body = """
                            <!DOCTYPE html>
                            <html>
                            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                                <div style="background: linear-gradient(135deg, #6B3FA0, #2D1B69); color: white; padding: 30px; border-radius: 10px 10px 0 0; text-align: center;">
                                    <h1 style="margin: 0;">🎉 ¡Enhorabuena! ¡Estás invitado!</h1>
                                </div>
                                <div style="background: #f8f8f8; padding: 30px; border-radius: 0 0 10px 10px; border: 1px solid #ddd;">
                                    <p style="font-size: 18px;">Hola <strong>%s</strong>,</p>
                                    <p>Has sido añadido a la lista de invitados para:</p>
                                    <div style="background: white; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #6B3FA0;">
                                        <h2 style="color: #6B3FA0; margin: 0 0 15px 0;">%s</h2>
                                        <p>📅 <strong>Fecha:</strong> %s</p>
                                        <p>📍 <strong>Lugar:</strong> %s — %s</p>
                                        <p>🎵 <strong>Género:</strong> %s</p>
                                        <p>👔 <strong>Dress Code:</strong> %s</p>
                                        <p>🎟️ <strong>Tipo:</strong> %s</p>
                                    </div>
                                    <hr style="border: none; border-top: 1px solid #ddd; margin: 20px 0;">
                                    <p style="color: #666;">📍 Preséntate en la puerta con tu DNI.</p>
                                    <p style="color: #666; font-size: 14px; text-align: center; margin-top: 30px;">
                                        <strong>NightOut AI</strong> — Tu plan para esta noche 🎶
                                    </p>
                                </div>
                            </body>
                            </html>
                            """.formatted(
                            trimmed,
                            event.getName(),
                            formattedDate,
                            venueName, venueLocation,
                            event.getGenre() != null ? event.getGenre() : "—",
                            event.getDressCode() != null ? event.getDressCode() : "—",
                            freeEntry ? "Entrada gratis 🎟️" : "Descuento en puerta 💵"
                    );

                    // Buscar si este email pertenece a un usuario registrado
                    String cleanEmail = email.trim();
                    User registeredUser = userRepository.findByUsername(cleanEmail);
                    if (registeredUser != null) {
                        emailService.sendEmail(email, guestSubject, body, registeredUser, "GUEST_LIST_CONFIRMATION");
                    } else {
                        emailService.sendEmail(email, guestSubject, body, "GUEST_LIST_CONFIRMATION");
                    }
                    emailResults.add("✅ " + trimmed + " (email enviado a " + email + ")");
                } catch (Exception e) {
                    log.error("Error al enviar email a {}: {}", email, e.getMessage());
                    emailResults.add("⚠️ " + trimmed + " (error al enviar email a " + email + ")");
                }
            }
        }

        if (!hasEmails) {
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                    String username = auth.getName();
                    User user = userRepository.findByUsername(username);
                    if (user != null) {
                        String namesList = guestNames.stream()
                                .map(String::trim)
                                .filter(n -> !n.isEmpty())
                                .collect(Collectors.joining(", "));
                        String venueName = event.getVenue() != null ? event.getVenue().getName() : "Por confirmar";
                        String venueLocation = event.getVenue() != null ? event.getVenue().getLocation() : "";
                        String formattedDate = event.getDate() != null
                                ? event.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
                                : "Por confirmar";
                        String subject = "🎉 Confirmación — Lista de Invitados para " + event.getName();
                        String body = """
                                <!DOCTYPE html>
                                <html>
                                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                                    <div style="background: linear-gradient(135deg, #6B3FA0, #2D1B69); color: white; padding: 30px; border-radius: 10px 10px 0 0; text-align: center;">
                                        <h1 style="margin: 0;">🎉 ¡Estás en la lista!</h1>
                                    </div>
                                    <div style="background: #f8f8f8; padding: 30px; border-radius: 0 0 10px 10px; border: 1px solid #ddd;">
                                        <p style="font-size: 18px;">Has sido añadido a la lista de invitados para:</p>
                                        <div style="background: white; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #6B3FA0;">
                                            <h2 style="color: #6B3FA0; margin: 0 0 15px 0;">%s</h2>
                                            <p>📅 <strong>Fecha:</strong> %s</p>
                                            <p>📍 <strong>Lugar:</strong> %s — %s</p>
                                            <p>🎵 <strong>Género:</strong> %s</p>
                                            <p>👔 <strong>Dress Code:</strong> %s</p>
                                            <p>🎟️ <strong>Tipo:</strong> %s</p>
                                            <hr style="border: none; border-top: 1px solid #ddd; margin: 15px 0;">
                                            <p><strong>👥 Invitados registrados:</strong> %s</p>
                                        </div>
                                        <p>📍 Preséntate en la puerta con tu DNI.</p>
                                        <p style="color: #666; font-size: 14px; text-align: center; margin-top: 30px;">
                                            <strong>NightOut AI</strong> — Tu plan para esta noche 🎶
                                        </p>
                                    </div>
                                </body>
                                </html>
                                """.formatted(
                                event.getName(),
                                formattedDate,
                                venueName, venueLocation,
                                event.getGenre() != null ? event.getGenre() : "—",
                                event.getDressCode() != null ? event.getDressCode() : "—",
                                freeEntry ? "Entrada gratis 🎟️" : "Descuento en puerta 💵",
                                namesList
                        );
                        emailService.sendEmail(user.getUsername(), subject, body, user, "GUEST_LIST_CONFIRMATION");
                    }
                }
            } catch (Exception e) {
                log.error("Error al enviar email de resumen: {}", e.getMessage());
            }
        }

        String entryType = freeEntry ? "🎟️ ENTRADA GRATIS" : "💵 DESCUENTO EN PUERTA";
        String namesList = guestNames.stream()
                .map(String::trim)
                .filter(n -> !n.isEmpty())
                .collect(Collectors.joining(", "));

        StringBuilder response = new StringBuilder();
        response.append("✅ ¡Apuntados correctamente!\n\n");
        response.append("📍 Evento: ").append(event.getName()).append("\n");
        response.append("🆓 Tipo: ").append(entryType).append("\n");
        response.append("👥 Invitados (").append(count).append("): ").append(namesList).append("\n\n");

        if (hasEmails && !emailResults.isEmpty()) {
            response.append("📧 Confirmaciones enviadas:\n");
            for (String result : emailResults) {
                response.append(result).append("\n");
            }
        } else if (hasEmails) {
            response.append("⚠️ No se pudieron enviar los emails de confirmación.\n");
        }

        response.append("\n¡Que disfruten la fiesta! 🎉");
        return response.toString();
    }

    @Tool(description = """
            Check how many people are on the guest list for a specific event.
            Useful when the user asks "who's on the list" or "how many are going".
            """)
    public String getGuestListSummary(
            @ToolParam(description = "The name of the event to check. Example: 'Reggaetón Night en Teatro Kapital'")
            String eventName) {

        Event event = eventRepository.findByName(eventName)
                .orElse(null);

        if (event == null) {
            return "No encontré un evento con ese nombre.";
        }

        long total = guestListRepository.countByEventId(event.getId());
        List<GuestList> confirmed = guestListRepository.findByEventIdAndStatus(event.getId(), GuestListStatus.CONFIRMED);
        List<GuestList> checkedIn = guestListRepository.findByEventIdAndStatus(event.getId(), GuestListStatus.CHECKED_IN);

        if (total == 0) {
            return "📋 Todavía no hay nadie apuntado en la lista de invitados para \"" + event.getName() + "\". "
                    + "¡Sé el primero en apuntarte!";
        }

        String names = confirmed.stream()
                .map(GuestList::getGuestName)
                .collect(Collectors.joining(", "));

        return """
                📋 LISTA DE INVITADOS — %s
                
                👥 Total apuntados: %d
                ✅ Confirmados (%d): %s
                🚪 Ya han entrado: %d
                
                ¡La fiesta te espera! 🎉
                """.formatted(event.getName(), total, confirmed.size(), names, checkedIn.size());
    }
}
