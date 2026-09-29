package com.hrgenius.repository;

import com.hrgenius.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    List<Payslip> findByEmployeeIdOrderByPayMonthDesc(Long employeeId);
    List<Payslip> findAllByOrderByPayMonthDesc();
    boolean existsByEmployeeIdAndPayMonth(Long employeeId, String payMonth);
}
