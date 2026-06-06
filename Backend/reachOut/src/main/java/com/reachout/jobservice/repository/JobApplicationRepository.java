package com.reachout.jobservice.repository;

import com.reachout.jobservice.model.ApplicationStatus;
import com.reachout.jobservice.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // get all applications for a specific user
    List<JobApplication> findByUserId(Long userId);

    // get applications by status for a specific user
    List<JobApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status);

    // find specific application belonging to a user (security check)
    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
}