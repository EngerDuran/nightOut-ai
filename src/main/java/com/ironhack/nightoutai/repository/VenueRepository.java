package com.ironhack.nightoutai.repository;

import com.ironhack.nightoutai.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByName(String name);
    Optional<Venue> findById(Long id);
    List<Venue> findAll();
    List<Venue> findByLocation(String location);
}
