package com.ironhack.nightoutai.dto;

import com.ironhack.nightoutai.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestDTO {

    private String name;
    private LocalDateTime date;
    private EventStatus status;
    private Long venueId;
}
