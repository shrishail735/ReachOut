package com.reachout.jobservice.service;

import com.reachout.jobservice.config.RabbitMQConfig;
import com.reachout.jobservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j  // gives us log.info(), log.error() etc
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishStatusChange(NotificationEvent event) {
        log.info("Publishing notification event for {} - {}",
                event.getCompany(), event.getStatus());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }
}