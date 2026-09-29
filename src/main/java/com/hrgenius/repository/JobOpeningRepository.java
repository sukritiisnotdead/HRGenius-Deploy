package com.hrgenius.repository;

import com.hrgenius.model.JobOpening;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOpeningRepository extends JpaRepository<JobOpening, Long> {
    long countByStatus(String status);
}
