package com.hrgenius.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class JobOpening {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank private String title;
    private Long departmentId;
    @Column(length = 1000)
    private String description;
    private String status = "OPEN";   // OPEN or CLOSED
}
