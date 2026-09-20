package com.reachout.emailfetcherservice.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {
    private String company;
    private String role;
    private String status;
    private String notes;
    private String location;
    private String salaryRange;
    private LocalDate appliedDate;
}