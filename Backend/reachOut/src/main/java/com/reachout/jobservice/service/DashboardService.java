package com.reachout.jobservice.service;

import com.reachout.jobservice.dto.DashboardStats;
import com.reachout.jobservice.model.ApplicationStatus;
import com.reachout.jobservice.model.User;
import com.reachout.jobservice.repository.JobApplicationRepository;
import com.reachout.jobservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final JobApplicationRepository jobRepo;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // cache result — key is unique per user
    @Cacheable(value = "dashboard-stats", key = "#userEmail")
    public DashboardStats getStats(String userEmail) {
        User user = getCurrentUser();
        log.info("Cache MISS — fetching stats from DB for {}", user.getEmail());

        long total = jobRepo.findByUserId(user.getId()).size();
        long applied = jobRepo.findByUserIdAndStatus(user.getId(), ApplicationStatus.APPLIED).size();
        long phoneScreen = jobRepo.findByUserIdAndStatus(user.getId(), ApplicationStatus.PHONE_SCREEN).size();
        long interview = jobRepo.findByUserIdAndStatus(user.getId(), ApplicationStatus.INTERVIEW).size();
        long offer = jobRepo.findByUserIdAndStatus(user.getId(), ApplicationStatus.OFFER).size();
        long rejected = jobRepo.findByUserIdAndStatus(user.getId(), ApplicationStatus.REJECTED).size();
        double offerRate = total > 0 ? (double) offer / total * 100 : 0;

        return new DashboardStats(total, applied, phoneScreen, interview, offer, rejected, offerRate);
    }

    // evict cache when applications change
    @CacheEvict(value = "dashboard-stats", key = "#userEmail")
    public void evictStatsCache(String userEmail) {
        log.info("Cache EVICTED for user");
    }
}