package com.hrgenius.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
public class Payslip {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    private String payMonth;      // like 2026-09
    private BigDecimal basic;
    private BigDecimal hra;
    private BigDecimal gross;
    private BigDecimal pf;
    private BigDecimal tax;
    private BigDecimal netPay;
    private LocalDate generatedOn;
}
