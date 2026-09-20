package com.reachout.jobservice.service;

import com.reachout.jobservice.config.RabbitMQConfig;
import com.reachout.jobservice.dto.NotificationEvent;
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

//    @CircuitBreaker(name = "notificationService", fallbackMethod = "fallback")
    public void publishStatusChange(NotificationEvent event) {
        log.info("Publishing notification event for {} - {}",
                event.getCompany(), event.getStatus());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }

    // called automatically when circuit is open or call fails
//    public void fallback(NotificationEvent event, Exception ex) {
//        log.warn("Circuit breaker OPEN — notification skipped for {} - {}. Reason: {}",
//                event.getCompany(), event.getStatus(), ex.getMessage());
//        // job application still saved successfully — notification is non-critical
//    }
}