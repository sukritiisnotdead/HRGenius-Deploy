package com.hrgenius.repository;

import com.hrgenius.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeIdOrderByStartDateDesc(Long employeeId);
    List<LeaveRequest> findAllByOrderByStartDateDesc();
    long countByStatus(String status);
}
