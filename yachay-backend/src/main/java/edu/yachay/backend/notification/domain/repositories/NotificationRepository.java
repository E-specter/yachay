package edu.yachay.backend.notification.domain.repositories;

import edu.yachay.backend.notification.domain.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient_IdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipient_IdAndReadFalse(Long recipientId);

    boolean existsByRecipient_IdAndTitleAndBody(Long recipientId, String title, String body);
}
