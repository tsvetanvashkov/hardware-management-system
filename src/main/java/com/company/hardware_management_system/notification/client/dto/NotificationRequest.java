package com.company.hardware_management_system.notification.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NotificationRequest {

    private String contactInfo;

    private String subject;

    private String body;
}
