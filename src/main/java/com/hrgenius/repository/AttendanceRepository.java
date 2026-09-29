package com.hrgenius.repository;

import com.hrgenius.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByEmployeeIdOrderByWorkDateDesc(Long employeeId);
    List<AttendanceRecord> findAllByOrderByWorkDateDesc();
    Optional<AttendanceRecord> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
}
