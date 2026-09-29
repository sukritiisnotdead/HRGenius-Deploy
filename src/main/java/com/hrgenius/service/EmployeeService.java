package com.hrgenius.service;

import com.hrgenius.model.AppUser;
import com.hrgenius.model.Employee;
import com.hrgenius.repository.AppUserRepository;
import com.hrgenius.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employees;
    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    /** Saves the employee AND creates a login: username = part of email before @, password = welcome123 */
    public Employee create(Employee e) {
        e.setId(null);
        e.setStatus("ACTIVE");
        if (e.getJoinDate() == null) e.setJoinDate(LocalDate.now());
        Employee saved = employees.save(e);

        String username = saved.getEmail().split("@")[0].toLowerCase();
        if (!users.existsByUsername(username)) {
            AppUser u = new AppUser();
            u.setUsername(username);
            u.setPassword(encoder.encode("welcome123"));
            u.setRole("EMPLOYEE");
            u.setEmployeeId(saved.getId());
            users.save(u);
        }
        return saved;
    }
}
