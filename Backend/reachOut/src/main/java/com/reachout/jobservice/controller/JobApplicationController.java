package com.reachout.jobservice.controller;

import com.reachout.jobservice.dto.JobApplicationRequest;
import com.reachout.jobservice.dto.JobApplicationResponse;
import com.reachout.jobservice.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173")
public class JobApplicationController {

    private final JobApplicationService jobService;

    @PostMapping
    public ResponseEntity<JobApplicationResponse> create(
            @Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.ok(jobService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAll() {
        return ResponseEntity.ok(jobService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.ok(jobService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobService.delete(id);
        return ResponseEntity.noContent().build();
    }
}