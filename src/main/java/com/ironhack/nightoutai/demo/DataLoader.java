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
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional // Crea una transaccion de base de datos
    public void run(String... args) {
        if (venueRepository.count() > 0) { //la idempotencia
            log.info("Seed data already exists — skipping");
            return;
        }

        log.info("Seeding Madrid nightlife data...");

        Venue wizink     = venueRepository.save(new Venue(null, "WiZink Center",           "Madrid", 15000, null));
        Venue laRiviera  = venueRepository.save(new Venue(null, "Sala La Riviera",         "Madrid",  2500, null));
        Venue barcelo    = venueRepository.save(new Venue(null, "Teatro Barceló",          "Madrid",  1500, null));
        Venue elSol      = venueRepository.save(new Venue(null, "Sala El Sol",             "Madrid",   350, null));
        Venue salaBut    = venueRepository.save(new Venue(null, "Sala But",                "Madrid",  1000, null));
        Venue kapital    = venueRepository.save(new Venue(null, "Teatro Kapital",          "Madrid",  2000, null));
        Venue lasVentas  = venueRepository.save(new Venue(null, "Las Ventas",              "Madrid", 25000, null));
        Venue circulo    = venueRepository.save(new Venue(null, "Círculo de Bellas Artes", "Madrid",  2000, null));


        //Evitamos fechas de eventos Caducadas
        LocalDateTime now = LocalDateTime.now();

        // Events
        Event rockFest = eventRepository.save(new Event(
                null, wizink.getCapacity(), 0,
                "Rock Festival en el WiZink Center", now.plusDays(14), //Lo usamos para sumar dias, 14 dias desde ahora
                EventStatus.CONFIRMED, "Rock", "Casual", wizink));

        Event electronica = eventRepository.save(new Event(
                null, laRiviera.getCapacity(), 0,
                "Noche de Electrónica en La Riviera", now.plusDays(21),
                EventStatus.CONFIRMED, "Electrónica", "Fiesta", laRiviera));

        Event indieNight = eventRepository.save(new Event(
                null, barcelo.getCapacity(), 0,
                "Fiesta Indie en Teatro Barceló", now.plusDays(30),
                EventStatus.CONFIRMED, "Indie", "Casual", barcelo));

        Event jazzClub = eventRepository.save(new Event(
                null, elSol.getCapacity(), 0,
                "Jazz en Sala El Sol", now.plusDays(45),
                EventStatus.CONFIRMED, "Jazz", "Elegante", elSol));

        Event metalNight = eventRepository.save(new Event(
                null, salaBut.getCapacity(), 0,
                "Noche de Metal en Sala But", now.plusDays(18),
                EventStatus.CONFIRMED, "Metal", "Temático", salaBut));

        Event reggaeton = eventRepository.save(new Event(
                null, kapital.getCapacity(), 0,
                "Reggaetón Night en Teatro Kapital", now.plusDays(25),
                EventStatus.CONFIRMED, "Reggaetón", "Fiesta", kapital));

        Event popConcert = eventRepository.save(new Event(
                null, lasVentas.getCapacity(), 0,
                "Los 40 Principales en Las Ventas", now.plusDays(60),
                EventStatus.SCHEDULED, "Pop", "Casual", lasVentas));

        Event jazzCocktail = eventRepository.save(new Event(
                null, circulo.getCapacity(), 0,
                "Noche de Jazz y Cócteles en Círculo BBAA", now.plusDays(35),
                EventStatus.CONFIRMED, "Jazz", "Formal", circulo));

        Event flamenco = eventRepository.save(new Event(
                null, laRiviera.getCapacity(), 0,
                "Flamenco Fusión Night en La Riviera", now.plusDays(28),
                EventStatus.CONFIRMED, "Flamenco", "Elegante", laRiviera));

        Event retroNight = eventRepository.save(new Event(
                null, barcelo.getCapacity(), 0,
                "Noche de los 80s y 90s en Teatro Barceló", now.plusDays(50),
                EventStatus.CONFIRMED, "Pop", "Temático", barcelo));

        //Tickets
        seedTickets(rockFest,      "A",  true,  45.0, "Pista VIP WiZink",     true,  130.0);
        seedTickets(electronica,   "B",  true,  25.0, "VIP Rivera",           true,   70.0);
        seedTickets(indieNight,    "C",  false, 20.0, "Palco Barceló",        true,   55.0);
        seedTickets(jazzClub,      "D",  false, 15.0, "VIP El Sol",           false,  40.0);
        seedTickets(metalNight,    "E",  true,  22.0, "VIP But",              true,   60.0);
        seedTickets(reggaeton,     "F",  true,  30.0, "VIP Kapital",          true,   80.0);
        seedTickets(popConcert,    "G",  true,  35.0, "Palco Las Ventas",     true,  100.0);
        seedTickets(jazzCocktail,  "H",  false, 28.0, "VIP Círculo",          true,   65.0);
        seedTickets(flamenco,      "I",  false, 30.0, "VIP Flamenco",         true,   75.0);
        seedTickets(retroNight,    "J",  true,  18.0, "VIP Retro",            true,   50.0);

        //Usamos SLF4J y llaves {} para inyectar variables en los logs, es mejor que concatenar con el +
        log.info("DataLoader complete — {} venues, {} events, {} tickets created",
                venueRepository.count(), eventRepository.count(), ticketRepository.count());
    }

    private void seedTickets(Event event, String gate, boolean standing,
                             double generalPrice, String vipZone,
                             boolean includeDrinks, double vipPrice) {

        GeneralTicket general = new GeneralTicket();
        general.setPrice(generalPrice);
        general.setEvent(event);
        general.setStandingArea(standing);
        general.setGateNumber(gate);
        ticketRepository.save(general);

        VipTicket vip = new VipTicket();
        vip.setPrice(vipPrice);
        vip.setEvent(event);
        vip.setZone(vipZone);
        vip.setIncludeDrinks(includeDrinks);
        ticketRepository.save(vip);
    }
}