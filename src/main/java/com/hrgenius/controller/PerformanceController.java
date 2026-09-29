package com.hrgenius.controller;

import com.hrgenius.model.PerformanceReview;
import com.hrgenius.repository.PerformanceReviewRepository;
import com.hrgenius.service.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceReviewRepository repo;
    private final CurrentUser currentUser;

    @GetMapping
    public List<PerformanceReview> all() {
        return repo.findAllByOrderByIdDesc();
    }

    @GetMapping("/my")
    public List<PerformanceReview> mine() {
        return repo.findByEmployeeIdOrderByIdDesc(currentUser.employeeId());
    }

    @PostMapping
    public PerformanceReview create(@Valid @RequestBody PerformanceReview r) {
        r.setId(null);
        r.setReviewer(currentUser.get().getUsername());
        return repo.save(r);
    }
}
