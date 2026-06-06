package com.reachout.jobservice.service;

import com.reachout.jobservice.dto.JobApplicationRequest;
import com.reachout.jobservice.dto.JobApplicationResponse;
import com.reachout.jobservice.dto.NotificationEvent;
import com.reachout.jobservice.model.JobApplication;
import com.reachout.jobservice.model.User;
import com.reachout.jobservice.repository.JobApplicationRepository;
import com.reachout.jobservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobRepo;
    private final UserRepository userRepository;
    private final NotificationPublisher notificationPublisher;

    // helper — gets currently logged in user from JWT
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // CREATE
    public JobApplicationResponse create(JobApplicationRequest request) {
        User user = getCurrentUser();

        JobApplication app = JobApplication.builder()
                .company(request.getCompany())
                .role(request.getRole())
                .status(request.getStatus())
                .notes(request.getNotes())
                .location(request.getLocation())
                .salaryRange(request.getSalaryRange())
                .appliedDate(request.getAppliedDate())
                .user(user)
                .build();

        return JobApplicationResponse.from(jobRepo.save(app));
    }

    // READ ALL — only current user's applications
    public List<JobApplicationResponse> getAll() {
        User user = getCurrentUser();
        return jobRepo.findByUserId(user.getId())
                .stream()
                .map(JobApplicationResponse::from)
                .toList();
    }

    // READ ONE
    public JobApplicationResponse getById(Long id) {
        User user = getCurrentUser();
        JobApplication app = jobRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Application not found"));
        return JobApplicationResponse.from(app);
    }

    // UPDATE
    public JobApplicationResponse update(Long id, JobApplicationRequest request) {
        User user = getCurrentUser();
        JobApplication app = jobRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        boolean statusChanged = !app.getStatus().equals(request.getStatus());
        String oldStatus = app.getStatus().name();

        app.setCompany(request.getCompany());
        app.setRole(request.getRole());
        app.setStatus(request.getStatus());
        app.setNotes(request.getNotes());
        app.setLocation(request.getLocation());
        app.setSalaryRange(request.getSalaryRange());
        app.setAppliedDate(request.getAppliedDate());

        JobApplication saved = jobRepo.save(app);

        // publish event only when status changes to INTERVIEW or OFFER
        if (statusChanged &&
                (request.getStatus().name().equals("INTERVIEW") ||
                        request.getStatus().name().equals("OFFER"))) {

            notificationPublisher.publishStatusChange(new NotificationEvent(
                    user.getEmail(),
                    user.getName(),
                    app.getCompany(),
                    app.getRole(),
                    request.getStatus().name()
            ));
        }

        return JobApplicationResponse.from(saved);
    }

    // DELETE
    public void delete(Long id) {
        User user = getCurrentUser();
        JobApplication app = jobRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Application not found"));
        jobRepo.delete(app);
    }
}