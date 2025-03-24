package com.company.hardware_management_system.notification.client.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {

    private String contactInfo;

    private String subject;

    private LocalDateTime createdOn;

    private String status;
}
