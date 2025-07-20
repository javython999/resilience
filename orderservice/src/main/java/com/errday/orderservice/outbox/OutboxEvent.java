package com.errday.orderservice.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@NoArgsConstructor
public class OutboxEvent {

    public enum EventStatus {
        PENDING,
        PROCESSING,
        PUBLISHED,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private LocalDateTime createAt;

    private LocalDateTime processedAt;

    public OutboxEvent(String eventType, String payload) {
        this.eventType = eventType;
        this.payload = payload;
        this.status = EventStatus.PENDING;
        this.createAt = LocalDateTime.now();
    }

    public void changeStatus(EventStatus status) {
        this.status = status;
        this.processedAt = LocalDateTime.now();
    }
}
