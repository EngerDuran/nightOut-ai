package com.ironhack.nightoutai.repository;

import com.ironhack.nightoutai.enums.GuestListStatus;
import com.ironhack.nightoutai.model.GuestList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestListRepository extends JpaRepository<GuestList, Long> {

    List<GuestList> findByEventId(Long eventId);

    List<GuestList> findByEventName(String eventName);

    List<GuestList> findByGuestNameContainingIgnoreCase(String guestName);

    List<GuestList> findByEventIdAndStatus(Long eventId, GuestListStatus status);

    long countByEventId(Long eventId);
}
