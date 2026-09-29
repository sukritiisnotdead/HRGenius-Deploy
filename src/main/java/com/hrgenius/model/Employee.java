package com.hrgenius.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank @Email @Column(unique = true)
    private String email;
    private String phone;
    private String jobTitle;
    private Long departmentId;
    @NotNull @Positive
    private BigDecimal salary;      // monthly basic salary
    private LocalDate joinDate;
    private String status = "ACTIVE";   // ACTIVE or INACTIVE
}
