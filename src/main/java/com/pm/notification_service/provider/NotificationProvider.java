package com.pm.notification_service.provider;

import com.pm.notification_service.model.Notification;

public interface NotificationProvider {
    void send(Notification notification) throws NotificationDispatchException;
}