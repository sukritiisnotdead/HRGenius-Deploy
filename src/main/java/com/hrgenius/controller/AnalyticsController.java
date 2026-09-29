package com.hrgenius.controller;

import com.hrgenius.model.*;
import com.hrgenius.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    private final JobOpeningRepository jobs;
    private final CandidateRepository candidates;
    private final LeaveRepository leaves;
    private final PayslipRepository payslips;

    @GetMapping
    public Map<String, Object> summary() {
        List<Employee> active = employees.findByStatus("ACTIVE");

        Map<Long, String> deptNames = departments.findAll().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        Map<String, Long> headcount = new LinkedHashMap<>();
        for (Employee e : active) {
            String name = deptNames.getOrDefault(e.getDepartmentId(), "Unassigned");
            headcount.merge(name, 1L, Long::sum);
        }

        Map<String, Long> stages = new LinkedHashMap<>();
        List<Candidate> allCandidates = candidates.findAll();
        for (Candidate c : allCandidates) {
            stages.merge(c.getStage(), 1L, Long::sum);
        }

        List<Payslip> all = payslips.findAll();
        String latest = all.stream().map(Payslip::getPayMonth).max(String::compareTo).orElse(null);
        BigDecimal payrollTotal = all.stream()
                .filter(p -> p.getPayMonth().equals(latest))
                .map(Payslip::getNetPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("totalEmployees", active.size());
        r.put("openJobs", jobs.countByStatus("OPEN"));
        r.put("pendingLeaves", leaves.countByStatus("PENDING"));
        r.put("totalCandidates", allCandidates.size());
        r.put("lastPayrollTotal", payrollTotal);
        r.put("headcountByDepartment", headcount);
        r.put("candidatesByStage", stages);
        return r;
    }
}
