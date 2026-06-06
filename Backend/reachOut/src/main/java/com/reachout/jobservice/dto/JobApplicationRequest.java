package com.reachout.jobservice.dto;

import com.reachout.jobservice.model.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class JobApplicationRequest {

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Role is required")
    private String role;

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    private String notes;
    private String location;
    private String salaryRange;

    @NotNull(message = "Applied date is required")
    private LocalDate appliedDate;
}