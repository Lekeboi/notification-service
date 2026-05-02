package com.pm.notification_service.dto;

import com.pm.notification_service.model.Notification;
import com.pm.notification_service.model.NotificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationResponse {

    private final UUID id;
    private final String type;
    private final String recipient;
    private final String message;
    private final NotificationStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public NotificationResponse(Notification n) {
        this.id = n.getId();
        this.type = n.getType();
        this.recipient = n.getRecipient();
        this.message = n.getMessage();
        this.status = n.getStatus();
        this.createdAt = n.getCreatedAt();
        this.updatedAt = n.getUpdatedAt();
    }

    public UUID getId() { return id; }
    public String getType() { return type; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public NotificationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
