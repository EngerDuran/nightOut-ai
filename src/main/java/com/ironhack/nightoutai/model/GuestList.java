package com.ironhack.nightoutai.model;

import com.ironhack.nightoutai.enums.GuestListStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guestName;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String registeredBy;

    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    private GuestListStatus status;

    private boolean freeEntry;
}
