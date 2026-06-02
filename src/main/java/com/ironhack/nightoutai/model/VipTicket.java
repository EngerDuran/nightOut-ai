package com.ironhack.nightoutai.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("VIP") //Le dice a JPA que es un tipo VIP o de tipo General
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VipTicket extends  Ticket {

    private String zone;
    private boolean includeDrinks;
}
