package com.ironhack.nightoutai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String recipientEmail;

    @Column(length = 500)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    private LocalDateTime sentAt;

    private String type; // RECOMMENDATION, PROMOTION, REMINDER

    private String status; // SENT, FAILED
}
