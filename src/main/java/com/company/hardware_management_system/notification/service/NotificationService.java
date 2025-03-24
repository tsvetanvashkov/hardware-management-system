package com.company.hardware_management_system.notification.service;

import com.company.hardware_management_system.notification.client.NotificationClient;
import com.company.hardware_management_system.notification.client.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    private final NotificationClient notificationClient;

    @Autowired
    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void sendNotification(String emailContact, String emailSubject, String emailBody) {

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .contactInfo(emailContact)
                .subject(emailSubject)
                .body(emailBody)
                .build();

        ResponseEntity<Void> httpResponse;
        try {
            httpResponse = notificationClient.sendNotification(notificationRequest);
            if (!httpResponse.getStatusCode().is2xxSuccessful()) {
                log.error("[Feign call to notification-service failed] Can't send email to user with email = [%s]".formatted(emailContact));
            }
        } catch (Exception e) {
            log.warn("Can't send email to user with email = [%s] due to 500 Internal Server Error.".formatted(emailContact));
        }
    }
}
