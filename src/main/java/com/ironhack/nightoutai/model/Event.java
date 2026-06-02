package com.ironhack.nightoutai.model;

import com.ironhack.nightoutai.enums.EventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalCapacity;
    private int soldTickets;


    private String name;
    private LocalDateTime date;
    private EventStatus status;

    // calcula cupos disponibles
    //Encapsulamiento en el modelo, para dejar el servicio más limpio
    public int getAvailableSeats() {
        return totalCapacity - soldTickets;
    }

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;
}
