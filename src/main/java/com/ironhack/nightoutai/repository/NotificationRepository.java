package com.ironhack.nightoutai.repository;

import com.ironhack.nightoutai.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


    List<Notification> findByUserId(Long userId);


    List<Notification> findAllByOrderBySentAtDesc();

    //Contador de noticificaciones
    long countByUserId(Long userId);


    List<Notification> findByRecipientEmail(String recipientEmail);
}
