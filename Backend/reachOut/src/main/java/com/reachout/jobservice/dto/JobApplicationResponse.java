package com.reachout.jobservice.dto;

import com.reachout.jobservice.model.ApplicationStatus;
import com.reachout.jobservice.model.JobApplication;
import lombok.Data;
import java.time.LocalDate;

@Data
public class JobApplicationResponse {

    private Long id;
    private String company;
    private String role;
    private ApplicationStatus status;
    private String notes;
    private String location;
    private String salaryRange;
    private LocalDate appliedDate;

    // static factory method — converts entity to response DTO
    public static JobApplicationResponse from(JobApplication app) {
        JobApplicationResponse res = new JobApplicationResponse();
        res.setId(app.getId());
        res.setCompany(app.getCompany());
        res.setRole(app.getRole());
        res.setStatus(app.getStatus());
        res.setNotes(app.getNotes());
        res.setLocation(app.getLocation());
        res.setSalaryRange(app.getSalaryRange());
        res.setAppliedDate(app.getAppliedDate());
        return res;
    }
}