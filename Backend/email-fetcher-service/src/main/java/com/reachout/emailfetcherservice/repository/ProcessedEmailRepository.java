package com.reachout.emailfetcherservice.repository;

import com.reachout.emailfetcherservice.model.ProcessedEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEmailRepository extends JpaRepository<ProcessedEmail, Long> {
    boolean existsByGmailMessageId(String gmailMessageId);
}