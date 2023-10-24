package com.example.driveronboardingservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "failed_events")
@Data
public class FailedEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "event_type", nullable = false)
    private String eventType;
    @Column(name = "event_payload", nullable = false)
    private String eventPayload;
    @Column(name = "error_message", nullable = false)
    private String errorMessage;
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;
    @Column(name = "next_retry_time", nullable = false)
    private Timestamp nextRetryTime;
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
