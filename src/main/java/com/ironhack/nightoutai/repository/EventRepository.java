package com.ironhack.nightoutai.repository;

import com.ironhack.nightoutai.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Venue, Long> {

}
