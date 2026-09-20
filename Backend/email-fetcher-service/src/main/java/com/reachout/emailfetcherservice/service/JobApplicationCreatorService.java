package com.reachout.emailfetcherservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.reachout.emailfetcherservice.dto.JobApplicationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
@Slf4j
public class JobApplicationCreatorService {

    @Value("${job-service.url}")
    private String jobServiceUrl;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    // token of the user whose email we're monitoring
    // in production this would come from a user session/DB
    @Value("${job-service.user-token}")
    private String userToken;

    public boolean createJobApplication(String company, String role,
                                        String status, String emailBody) {
        try {
            JobApplicationRequest request = JobApplicationRequest.builder()
                    .company(company)
                    .role(role != null ? role : "Unknown Role")
                    .status(status != null ? status : "APPLIED")
                    .notes("Auto-imported from email")
                    .appliedDate(LocalDate.now())
                    .build();

            String json = objectMapper.writeValueAsString(request);

            Request httpRequest = new Request.Builder()
                    .url(jobServiceUrl + "/api/applications")
                    .addHeader("Authorization", "Bearer " + userToken)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(json,
                            MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (response.isSuccessful()) {
                    log.info("✅ Created job application: {} - {}", company, role);
                    return true;
                } else {
                    log.error("Failed to create application: {}", response.code());
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Error creating job application: {}", e.getMessage());
            return false;
        }
    }
}