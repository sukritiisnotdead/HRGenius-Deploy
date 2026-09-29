package com.hrgenius.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** A login account. role is ADMIN, HR or EMPLOYEE. */
@Entity
@Table(name = "app_user")
@Getter @Setter
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;   // stored as a BCrypt hash, never plain text
    @Column(nullable = false)
    private String role;
    private Long employeeId;   // links the login to an employee record
}
