package com.reachout.emailfetcherservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reachout.emailfetcherservice.dto.ParsedJobEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.api-url}")
    private String apiUrl;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ParsedJobEmail parseJobEmail(String emailSubject, String emailBody) {
        try {
            String prompt = buildPrompt(emailSubject, emailBody);
            String response = callGemini(prompt);
            ParsedJobEmail geminiResponse =  parseGeminiResponse(response);
            if (geminiResponse.isJobEmail()) {
                String infoResponse = callGemini(infoPrompt(geminiResponse));
                log.info("company related info fetched by gemini " + infoResponse);
            }
            return geminiResponse;
        } catch (Exception e) {
            log.error("Failed to parse email with Gemini: {}", e.getMessage());
            return new ParsedJobEmail(null, null, null, false);
        }
    }
    private String infoPrompt(ParsedJobEmail response) {
        return """
            Analyze the below job application data with company name and role.
            
            Job application data: %s
            
            Please search information related to company for this specific role including
            - Any interview experience links for same role at same company
            - Preparation guide as per company interview process
            - Required concepts as per role
            
            Please find any additional information you get after researching about company and role.
            """.formatted(response);
    }
    private String buildPrompt(String subject, String body) {
        return """
            Analyze this email and determine if it's a job application related email.
            If it is, extract the following information.
            
            Email Subject: %s
            Email Body: %s
            
            Respond ONLY with a JSON object in this exact format, no other text:
            {
              "isJobEmail": true or false,
              "company": "company name or null",
              "role": "job title or null",
              "status": "one of: APPLIED, PHONE_SCREEN, INTERVIEW, OFFER, REJECTED or null",
            }
            
            Rules:
            - isJobEmail = true only if this is clearly about a job application
            - status = APPLIED if they confirmed receiving your application
            - status = INTERVIEW if they're scheduling or inviting you for an interview
            - status = OFFER if they're extending a job offer
            - status = REJECTED if they're rejecting your application
            - status = PHONE_SCREEN if they want a quick call before interview
            - If you can't determine something with confidence, use null
            """.formatted(subject, body);
    }

    private String callGemini(String prompt) throws IOException {
        String requestBody = """
            {
              "contents": [{
                "parts": [{
                  "text": "%s"
                }]
              }]
            }
            """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n"));

        Request request = new Request.Builder()
                .url(apiUrl + "?key=" + apiKey)
                .post(RequestBody.create(requestBody,
                        MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Gemini API error: " + response.code());
            }
            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
        }
    }

    private ParsedJobEmail parseGeminiResponse(String response) {
        try {
            log.info("Raw Gemini response: [{}]", response);

            // remove all variations of markdown code blocks
            String cleaned = response
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            log.info("Cleaned response: [{}]", cleaned);

            return objectMapper.readValue(cleaned, ParsedJobEmail.class);
        } catch (Exception e) {
            log.error("Parse error: {} | Response was: [{}]", e.getMessage(), response);
            return new ParsedJobEmail(null, null, null, false);
        }
    }
}