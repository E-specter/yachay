package edu.yachay.backend.notification.domain.models;

import edu.yachay.backend.identity.domain.models.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "yachay_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id", nullable = false)
    private User recipient;

    @Column(name = "title", nullable = false, length = 180)
    private String title;

    @Column(name = "body", nullable = false, length = 1000)
    private String body;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean read = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (read == null) {
            read = false;
        }
    }

    public void markAsRead() {
        read = true;
        readAt = LocalDateTime.now();
    }
}
