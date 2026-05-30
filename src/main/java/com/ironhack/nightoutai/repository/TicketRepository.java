package com.ironhack.nightoutai.repository;

import com.ironhack.nightoutai.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
