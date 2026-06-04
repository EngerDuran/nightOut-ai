package com.ironhack.nightoutai.demo;

import com.ironhack.nightoutai.enums.EventStatus;
import com.ironhack.nightoutai.model.*;
import com.ironhack.nightoutai.repository.EventRepository;
import com.ironhack.nightoutai.repository.TicketRepository;
import com.ironhack.nightoutai.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Order(2) // Se ejecuta después de UserDataLoader
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (venueRepository.count() > 0) {
            log.info("Los datos ya existen, se omite DataLoader");
            return;
        }

        log.info("Inicializando la base de datos con eventos de prueba...");

        // Venues en Madrid
        Venue wizink = venueRepository.save(new Venue(null, "WiZink Center", "Madrid", 15000, null));
        Venue laRiviera = venueRepository.save(new Venue(null, "Sala La Riviera", "Madrid", 2500, null));
        Venue barcelo = venueRepository.save(new Venue(null, "Teatro Barceló", "Madrid", 1500, null));
        Venue elSol = venueRepository.save(new Venue(null, "Sala El Sol", "Madrid", 350, null));

        // Events
        LocalDateTime now = LocalDateTime.now();

        Event rock = eventRepository.save(new Event(null, wizink.getCapacity(), 0, "Rock festival en el WiZink Center", now.plusDays(14), EventStatus.CONFIRMED, wizink));
        Event electronica = eventRepository.save(new Event(null, laRiviera.getCapacity(), 0, "Noche de Electrónica en La Riviera", now.plusDays(21), EventStatus.CONFIRMED, laRiviera));
        Event indie = eventRepository.save(new Event(null, barcelo.getCapacity(), 0, "Fiesta Pop en Teatro Barceló", now.plusDays(30), EventStatus.SCHEDULED, barcelo));
        Event jazz = eventRepository.save(new Event(null, elSol.getCapacity(), 0, "Jazz en Sala El Sol", now.plusDays(45), EventStatus.CONFIRMED, elSol));

        // Tickets
        seedTickets(rock, "A", true, 35.0, "Pista VIP", true, 120.0);
        seedTickets(electronica, "B", true, 25.0, "VIP Rivera", true, 70.0);
        seedTickets(indie, "C", false, 20.0, "Palco Barceló", true, 55.0);
        seedTickets(jazz, "D", false, 15.0, "VIP El Sol", false, 40.0);

        log.info("DataLoader complete — {} venues, {} events, {} tickets created",
                venueRepository.count(), eventRepository.count(), ticketRepository.count());
    }

    private void seedTickets(Event event, String gate, boolean standing,
                             double generalPrice, String vipZone,
                             boolean includeDrinks, double vipPrice) {

        // Create General ticket
        GeneralTicket general = new GeneralTicket();
        general.setPrice(generalPrice);
        general.setEvent(event);
        general.setStandingArea(standing);
        general.setGateNumber(gate);
        ticketRepository.save(general);

        // Create VIP ticket
        VipTicket vip = new VipTicket();
        vip.setPrice(vipPrice);
        vip.setEvent(event);
        vip.setZone(vipZone);
        vip.setIncludeDrinks(includeDrinks);
        ticketRepository.save(vip);
    }
}