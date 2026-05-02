package com.pm.notification_service.service;

import com.pm.notification_service.dto.NotificationRequest;
import com.pm.notification_service.dto.NotificationResponse;
import com.pm.notification_service.model.Notification;
import com.pm.notification_service.model.NotificationStatus;
import com.pm.notification_service.provider.NotificationProvider;
import com.pm.notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationProvider provider;

    public NotificationService(NotificationRepository repository, NotificationProvider provider) {
        this.repository = repository;
        this.provider = provider;
    }

    public NotificationResponse send(NotificationRequest request) {
        validateRecipient(request.getType(), request.getRecipient());

        Notification notification = new Notification();
        notification.setType(request.getType());
        notification.setRecipient(request.getRecipient());
        notification.setMessage(request.getMessage());
        notification = repository.save(notification);

        try {
            provider.send(notification);
            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
        }

        return new NotificationResponse(repository.save(notification));
    }

    public NotificationResponse getById(UUID id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notification not found: " + id));
        return new NotificationResponse(notification);
    }

    private void validateRecipient(String type, String recipient) {
        if ("EMAIL".equals(type)) {
            if (!recipient.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new IllegalArgumentException("Invalid email address: " + recipient);
            }
        } else if ("SMS".equals(type)) {
            if (!recipient.matches("^\\+?[1-9]\\d{7,14}$")) {
                throw new IllegalArgumentException("Invalid phone number: " + recipient);
            }
        }
    }
}
