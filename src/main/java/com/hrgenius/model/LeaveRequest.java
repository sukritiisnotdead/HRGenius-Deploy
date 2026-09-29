package com.hrgenius.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "leave_request")
@Getter @Setter
public class LeaveRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    private String leaveType;     // e.g. Casual, Sick, Annual
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status = "PENDING";   // PENDING, APPROVED, REJECTED
}
