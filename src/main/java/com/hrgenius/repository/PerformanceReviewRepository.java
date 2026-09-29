package com.hrgenius.repository;

import com.hrgenius.model.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployeeIdOrderByIdDesc(Long employeeId);
    List<PerformanceReview> findAllByOrderByIdDesc();
}
