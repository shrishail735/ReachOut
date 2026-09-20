package com.reachout.emailfetcherservice.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GmailService {

    private final Gmail gmail;

    @Value("${email.max-emails-per-poll}")
    private int maxEmailsPerPoll;

    private static final String USER = "me";

    // fetch recent unread emails
    public List<Message> fetchRecentEmails(long afterTimestamp) throws IOException {
        String query = "is:unread after:" + (afterTimestamp / 1000);

        List<Message> messages = gmail.users().messages()
                .list(USER)
                .setQ(query)
                .setMaxResults((long) maxEmailsPerPoll)
                .execute()
                .getMessages();

        if (messages == null) return new ArrayList<>();

        List<Message> fullMessages = new ArrayList<>();
        for (Message message : messages) {
            Message fullMessage = gmail.users().messages()
                    .get(USER, message.getId())
                    .setFormat("full")
                    .execute();
            fullMessages.add(fullMessage);
        }

        return fullMessages;
    }

    // extract subject from email headers
    public String getSubject(Message message) {
        return getHeader(message, "Subject");
    }

    // extract sender from email headers
    public String getSender(Message message) {
        return getHeader(message, "From");
    }

    // extract plain text body from email
    public String getBody(Message message) {
        return extractBody(message.getPayload());
    }

    private String extractBody(MessagePart part) {
        if (part == null) return "";

        // plain text part
        if ("text/plain".equals(part.getMimeType()) && part.getBody() != null) {
            byte[] bodyBytes = Base64.getUrlDecoder()
                    .decode(part.getBody().getData());
            String text = new String(bodyBytes);
            // limit to 2000 chars — enough for Gemini, saves tokens
            return text.length() > 2000 ? text.substring(0, 2000) : text;
        }

        // multipart — recursively find text/plain
        if (part.getParts() != null) {
            for (MessagePart subPart : part.getParts()) {
                String body = extractBody(subPart);
                if (!body.isEmpty()) return body;
            }
        }

        return "";
    }

    private String getHeader(Message message, String headerName) {
        if (message.getPayload() == null) return "";
        List<MessagePartHeader> headers = message.getPayload().getHeaders();
        if (headers == null) return "";

        return headers.stream()
                .filter(h -> headerName.equalsIgnoreCase(h.getName()))
                .map(MessagePartHeader::getValue)
                .findFirst()
                .orElse("");
    }
}