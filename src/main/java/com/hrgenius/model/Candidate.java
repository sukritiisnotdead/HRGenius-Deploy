package com.hrgenius.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Candidate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull private Long jobId;
    @NotBlank private String name;
    @NotBlank @Email private String email;
    private String stage = "APPLIED";   // APPLIED, INTERVIEW, OFFERED, HIRED, REJECTED
    private Long employeeId;            // filled in when the candidate is hired
}
