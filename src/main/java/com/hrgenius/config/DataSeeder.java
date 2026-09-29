package com.hrgenius.config;

import com.hrgenius.model.*;
import com.hrgenius.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Runs at startup. Adds demo data only when the database is empty. */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final EmployeeRepository empRepo;
    private final JobOpeningRepository jobRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;

        Department eng = dept("Engineering");
        Department hr = dept("Human Resources");
        dept("Finance");
        dept("Sales");

        Employee admin = emp("Asha", "Admin", "admin@hrgenius.com", "System Administrator", hr, "90000");
        Employee hrEmp = emp("Hema", "Rao", "hr@hrgenius.com", "HR Manager", hr, "70000");
        Employee john = emp("John", "Doe", "john@hrgenius.com", "Software Engineer", eng, "60000");

        user("admin", "admin123", "ADMIN", admin.getId());
        user("hr", "hr123", "HR", hrEmp.getId());
        user("john", "john123", "EMPLOYEE", john.getId());

        JobOpening job = new JobOpening();
        job.setTitle("Java Developer");
        job.setDepartmentId(eng.getId());
        job.setDescription("Build Spring Boot services for our products.");
        jobRepo.save(job);
    }

    private Department dept(String name) {
        Department d = new Department();
        d.setName(name);
        return deptRepo.save(d);
    }

    private Employee emp(String first, String last, String email, String title, Department d, String salary) {
        Employee e = new Employee();
        e.setFirstName(first);
        e.setLastName(last);
        e.setEmail(email);
        e.setJobTitle(title);
        e.setDepartmentId(d.getId());
        e.setSalary(new BigDecimal(salary));
        e.setJoinDate(LocalDate.now().minusMonths(6));
        return empRepo.save(e);
    }

    private void user(String username, String password, String role, Long employeeId) {
        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPassword(encoder.encode(password));
        u.setRole(role);
        u.setEmployeeId(employeeId);
        userRepo.save(u);
    }
}
