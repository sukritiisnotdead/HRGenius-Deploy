package com.hrgenius.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class PerformanceReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull private Long employeeId;
    @NotBlank private String period;     // e.g. Q3 2026
    @Min(1) @Max(5)
    private int rating;
    private String reviewer;
    @Column(length = 1000)
    private String comments;
}
