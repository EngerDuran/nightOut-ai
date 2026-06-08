package com.ironhack.nightoutai.tools;

import com.ironhack.nightoutai.enums.GuestListStatus;
import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.GuestList;
import com.ironhack.nightoutai.repository.EventRepository;
import com.ironhack.nightoutai.repository.GuestListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GuestListTools {

    private final GuestListRepository guestListRepository;
    private final EventRepository eventRepository;

    @Tool(description = """
            Register one or more guests on the VIP or free-entry guest list for a specific event.
            Use this when the user asks to be added to a guest list, bring friends, get free entry,
            or get a discount at the door. Returns a summary of who was registered.
            """)
    public String addToGuestList(
            @ToolParam(description = """
                    List of guest names to register. Extract ALL names from the user's message.
                    The user may include themselves. Example: ['Carlos Pérez', 'Lucía Ortiz']
                    """)
            List<String> guestNames,

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
                    // Si no encuentra exacto, buscar por coincidencia parcial
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

        // Registrar cada invitado
        int count = 0;
        for (String name : guestNames) {
            String trimmed = name.trim();
            if (trimmed.isEmpty()) continue;

            GuestList entry = new GuestList();
            entry.setGuestName(trimmed);
            entry.setEvent(event);
            entry.setRegisteredBy("RRPP Virtual"); // Viene del sistema de IA
            entry.setRegistrationDate(LocalDateTime.now());
            entry.setStatus(GuestListStatus.CONFIRMED);
            entry.setFreeEntry(freeEntry);

            guestListRepository.save(entry);
            count++;
        }

        String entryType = freeEntry ? "🎟️ ENTRADA GRATIS" : "💵 DESCUENTO EN PUERTA";
        String namesList = guestNames.stream()
                .map(String::trim)
                .filter(n -> !n.isEmpty())
                .collect(Collectors.joining(", "));

        return """
                ✅ ¡Apuntados correctamente!
                
                📍 Evento: %s
                🆓 Tipo: %s
                👥 Invitados (%d): %s
                
                Preséntense en la puerta con su DNI. ¡Que disfruten la fiesta! 🎉
                """.formatted(event.getName(), entryType, count, namesList);
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
