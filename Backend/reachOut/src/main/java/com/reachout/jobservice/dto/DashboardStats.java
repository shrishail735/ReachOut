package com.reachout.jobservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats implements Serializable {
    private long totalApplications;
    private long applied;
    private long phoneScreen;
    private long interview;
    private long offer;
    private long rejected;
    private double offerRate;  // offer / total * 100
}