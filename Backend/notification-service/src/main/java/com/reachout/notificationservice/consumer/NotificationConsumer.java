package com.reachout.notificationservice.consumer;

import com.reachout.notificationservice.dto.NotificationEvent;
import com.reachout.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "notification.queue")
    public void handleNotification(NotificationEvent event) {
        log.info("Received notification event: {} applied to {} - status: {}",
                event.getUserName(), event.getCompany(), event.getStatus());

        emailService.sendStatusChangeEmail(event);
    }
}