package com.hrgenius.service;

import com.hrgenius.model.Employee;
import com.hrgenius.model.Payslip;
import com.hrgenius.repository.EmployeeRepository;
import com.hrgenius.repository.PayslipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final EmployeeRepository employees;
    private final PayslipRepository payslips;

    /**
     * Simple demo rules (change them to whatever you like):
     *   HRA = 40% of basic, PF = 12% of basic,
     *   tax = 10% of the gross above 50,000.
     *   net = gross - PF - tax
     * Returns how many payslips were created (employees already paid for that month are skipped).
     */
    public int generateForMonth(String month) {
        if (month == null || !month.matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("Month must look like 2026-09");
        }
        int count = 0;
        for (Employee e : employees.findByStatus("ACTIVE")) {
            if (payslips.existsByEmployeeIdAndPayMonth(e.getId(), month)) continue;

            BigDecimal basic = e.getSalary();
            BigDecimal hra = basic.multiply(new BigDecimal("0.40"));
            BigDecimal gross = basic.add(hra);
            BigDecimal pf = basic.multiply(new BigDecimal("0.12"));
            BigDecimal threshold = new BigDecimal("50000");
            BigDecimal tax = gross.compareTo(threshold) > 0
                    ? gross.subtract(threshold).multiply(new BigDecimal("0.10"))
                    : BigDecimal.ZERO;
            BigDecimal net = gross.subtract(pf).subtract(tax);

            Payslip p = new Payslip();
            p.setEmployeeId(e.getId());
            p.setPayMonth(month);
            p.setBasic(scale(basic));
            p.setHra(scale(hra));
            p.setGross(scale(gross));
            p.setPf(scale(pf));
            p.setTax(scale(tax));
            p.setNetPay(scale(net));
            p.setGeneratedOn(LocalDate.now());
            payslips.save(p);
            count++;
        }
        return count;
    }

    private BigDecimal scale(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
