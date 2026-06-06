package com.reachout.notificationservice.service;

import com.reachout.notificationservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendStatusChangeEmail(NotificationEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getUserEmail());
            message.setSubject(buildSubject(event));
            message.setText(buildBody(event));

            mailSender.send(message);
            log.info("Email sent to {} for {} - {}",
                    event.getUserEmail(), event.getCompany(), event.getStatus());

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}",
                    event.getUserEmail(), e.getMessage());
        }
    }

    private String buildSubject(NotificationEvent event) {
        if (event.getStatus().equals("INTERVIEW")) {
            return "🎯 Interview scheduled at " + event.getCompany() + "!";
        }
        return "🎉 Offer received from " + event.getCompany() + "!";
    }

    private String buildBody(NotificationEvent event) {
        if (event.getStatus().equals("INTERVIEW")) {
            return String.format("""
                Hi %s,
                
                Great news! Your application for %s at %s has moved to the INTERVIEW stage.
                
                Make sure to:
                ✅ Research the company thoroughly
                ✅ Practice common interview questions
                ✅ Prepare questions to ask the interviewer
                ✅ Review your projects and experience
                
                You've got this! 💪
                
                Best of luck,
                ReachOut Team 🚀
                """,
                    event.getUserName(),
                    event.getRole(),
                    event.getCompany()
            );
        }

        return String.format("""
                Hi %s,
                
                Congratulations! 🎉 You have received an OFFER from %s for the %s position!
                
                Next steps:
                ✅ Review the offer details carefully
                ✅ Negotiate if needed — you deserve it!
                ✅ Respond within the given timeframe
                
                Amazing work! You earned it! 🏆
                
                Best of luck,
                ReachOut Team 🚀
                """,
                event.getUserName(),
                event.getRole(),
                event.getCompany()
        );
    }
}