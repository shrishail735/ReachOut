package com.reachout.jobservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)  // stores "APPLIED" not 0,1,2 in DB
    @Column(nullable = false)
    private ApplicationStatus status;

    private String notes;
    private String location;
    private String salaryRange;

    @Column(nullable = false)
    private LocalDate appliedDate;

    // many applications belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}