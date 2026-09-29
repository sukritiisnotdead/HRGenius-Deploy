package com.hrgenius.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter @Setter
public class AttendanceRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    @Column(name = "work_date")
    private LocalDate workDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
}
