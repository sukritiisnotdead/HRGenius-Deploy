package com.hrgenius.controller;

import com.hrgenius.model.Payslip;
import com.hrgenius.repository.PayslipRepository;
import com.hrgenius.service.CurrentUser;
import com.hrgenius.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayslipRepository repo;
    private final PayrollService service;
    private final CurrentUser currentUser;

    @PostMapping("/generate")
    public Map<String, String> generate(@RequestParam String month) {
        int n = service.generateForMonth(month);
        return Map.of("message", n + " payslip(s) generated for " + month);
    }

    @GetMapping
    public List<Payslip> all() {
        return repo.findAllByOrderByPayMonthDesc();
    }

    @GetMapping("/my")
    public List<Payslip> mine() {
        return repo.findByEmployeeIdOrderByPayMonthDesc(currentUser.employeeId());
    }
}
