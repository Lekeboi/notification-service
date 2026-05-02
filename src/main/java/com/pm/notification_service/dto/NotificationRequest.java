package com.pm.notification_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class NotificationRequest {

    @NotBlank(message = "type is required")
    @Pattern(regexp = "EMAIL|SMS", message = "type must be EMAIL or SMS")
    private String type;

    @NotBlank(message = "recipient is required")
    private String recipient;

    @NotBlank(message = "message is required")
    private String message;

    public String getType() { return type; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }

    public void setType(String type) { this.type = type; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public void setMessage(String message) { this.message = message; }
}
