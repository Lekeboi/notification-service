package com.pm.notification_service.provider;

import com.pm.notification_service.model.Notification;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MockNotificationProvider implements NotificationProvider {

    private final Random random = new Random();

    @Override
    public void send(Notification notification) throws NotificationDispatchException {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (random.nextDouble() < 0.20) {
            throw new NotificationDispatchException(
                    "Failed to send notification to: " + notification.getRecipient()
            );
        }
    }
}
