package com.ironhack.nightoutai.dto;

import com.ironhack.nightoutai.enums.EventStatus;
import com.ironhack.nightoutai.model.Venue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestDto {

    private String name;
    private LocalDateTime date;
    private EventStatus status;
    private Long venueId;
}
