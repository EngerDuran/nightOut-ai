package com.ironhack.nightoutai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ============================================================
 * NOTIFICATION — Registro de correos electrónicos enviados
 * ============================================================
 *
 * ¿Qué es esto?
 * Cada vez que el sistema envíe un correo (automático o manual),
 * guardamos un registro aquí. Así podemos:
 *
 * 1. Ver el historial de qué correos se han enviado a quién
 * 2. Evitar enviar correos duplicados
 * 3. Saber si un correo se envió correctamente o falló
 * 4. Mostrar en la demo que "el sistema está mandando correos"
 *
 * Relaciones:
 * - user (ManyToOne): El usuario que recibe el correo
 *   Un usuario puede recibir muchos correos, pero cada correo
 *   pertenece a un solo usuario.
 *
 * Campos:
 * - recipientEmail: A qué dirección se envió
 * - subject: Asunto del correo
 * - body: Contenido (puede ser HTML)
 * - sentAt: Cuándo se envió
 * - type: De qué tipo es (RECOMMENDATION, PROMOTION, REMINDER)
 * - status: Si se envió correctamente o falló
 */
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
