package com.reachout.emailfetcherservice.scheduler;


import com.reachout.emailfetcherservice.service.EmailProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class EmailPollingScheduler {

    private final EmailProcessorService emailProcessorService;

    @Scheduled(fixedRateString = "${email.poll-interval-ms}")
    public void pollEmails() {
        log.info("⏰ Scheduled email poll triggered");
        emailProcessorService.processNewEmails();
    }
}