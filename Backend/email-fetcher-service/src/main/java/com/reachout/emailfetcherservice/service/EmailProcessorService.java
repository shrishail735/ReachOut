package com.reachout.emailfetcherservice.service;

import com.google.api.services.gmail.model.Message;
import com.reachout.emailfetcherservice.dto.ParsedJobEmail;
import com.reachout.emailfetcherservice.model.ProcessedEmail;
import com.reachout.emailfetcherservice.repository.ProcessedEmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProcessorService {

    private final GmailService gmailService;
    private final GeminiService geminiService;
    private final JobApplicationCreatorService jobCreatorService;
    private final ProcessedEmailRepository processedEmailRepo;

    private long lastPollTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // last 24 hours on first run

    public void processNewEmails() {
        log.info("🔍 Polling Gmail for new emails since {}",
                new java.util.Date(lastPollTimestamp));

        try {
            List<Message> emails = gmailService.fetchRecentEmails(lastPollTimestamp);
            log.info("Found {} new emails", emails.size());

            for (Message email : emails) {
                processEmail(email);
            }

            lastPollTimestamp = System.currentTimeMillis();

        } catch (Exception e) {
            log.error("Failed to fetch emails: {}", e.getMessage());
        }
    }

    private void processEmail(Message email) {
        String messageId = email.getId();

        // skip already processed emails
        if (processedEmailRepo.existsByGmailMessageId(messageId)) {
            log.debug("Skipping already processed email: {}", messageId);
            return;
        }

        String subject = gmailService.getSubject(email);
        String body = gmailService.getBody(email);
        String sender = gmailService.getSender(email);

        log.info("📧 Processing email: {} from {}", subject, sender);

        // ask Gemini to parse it
        ParsedJobEmail parsed = geminiService.parseJobEmail(subject, body);

        if (!parsed.isJobEmail()) {
            log.info("⏭️ Not a job email — skipping: {}", subject);
            saveProcessedEmail(messageId, null, null, null, false);
            return;
        }

//        if ("low".equals(parsed.getConfidence())) {
//            log.info("⚠️ Low confidence parse — skipping: {}", subject);
//            saveProcessedEmail(messageId, parsed.getCompany(),
//                    parsed.getRole(), parsed.getStatus(), false);
//            return;
//        }

        log.info("✅ Job email detected: {} - {} - {}",
                parsed.getCompany(), parsed.getRole(), parsed.getStatus());

        // create job application in DB via Job Service
        boolean created = jobCreatorService.createJobApplication(
                parsed.getCompany(),
                parsed.getRole(),
                parsed.getStatus(),
                body
        );

        saveProcessedEmail(messageId, parsed.getCompany(),
                parsed.getRole(), parsed.getStatus(), created);
    }

    private void saveProcessedEmail(String messageId, String company,
                                    String role, String status, boolean created) {
        ProcessedEmail processed = ProcessedEmail.builder()
                .gmailMessageId(messageId)
                .processedAt(LocalDateTime.now())
                .company(company)
                .role(role)
                .status(status)
                .jobApplicationCreated(created)
                .build();
        processedEmailRepo.save(processed);
    }
}