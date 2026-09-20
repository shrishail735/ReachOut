package com.reachout.emailfetcherservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_emails")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String gmailMessageId;  // Gmail's unique message ID

    @Column(nullable = false)
    private LocalDateTime processedAt;

    private String company;
    private String role;
    private String status;
    private boolean jobApplicationCreated;
}