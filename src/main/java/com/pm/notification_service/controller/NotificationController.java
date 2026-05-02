package com.pm.notification_service.controller;

import com.pm.notification_service.dto.NotificationRequest;
import com.pm.notification_service.dto.NotificationResponse;
import com.pm.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse send(@RequestBody @Valid NotificationRequest request) {
        return service.send(request);
    }

    @GetMapping("/{id}")
    public NotificationResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }
}
