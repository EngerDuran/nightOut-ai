package com.ironhack.nightoutai.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("GENERAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralTicket extends Ticket {

    private boolean isStandingArea;
    private String gateNumber;


}
