package com.ironhack.nightoutai.repository;

import com.ironhack.nightoutai.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * NOTIFICATION REPOSITORY — Acceso a base de datos de correos
 * ============================================================
 *
 * Spring Data JPA genera automáticamente las consultas SQL
 * a partir del nombre de los métodos. No necesitas escribir
 * ni una línea de SQL.
 *
 * findByUserId(Long userId)
 *   → Genera: SELECT * FROM notification WHERE user_id = ?
 *   → Sirve para: Mostrar el historial de correos de un usuario
 *
 * findAllByOrderBySentAtDesc()
 *   → Genera: SELECT * FROM notification ORDER BY sent_at DESC
 *   → Sirve para: Ver todos los correos enviados, los más recientes primero
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Busca todas las notificaciones de un usuario específico.
     * Útil para que el usuario vea su historial de correos.
     */
    List<Notification> findByUserId(Long userId);

    /**
     * Busca todas las notificaciones ordenadas por fecha descendente.
     * Útil para el panel de administración.
     */
    List<Notification> findAllByOrderBySentAtDesc();

    /**
     * Cuenta cuántas notificaciones se han enviado a un usuario.
     */
    long countByUserId(Long userId);
}
