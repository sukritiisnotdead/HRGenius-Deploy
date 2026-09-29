package com.hrgenius.controller;

import com.hrgenius.model.AppUser;
import com.hrgenius.model.Employee;
import com.hrgenius.repository.EmployeeRepository;
import com.hrgenius.service.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CurrentUser currentUser;
    private final EmployeeRepository employees;

    /** The login page calls this to check the username/password and learn the role. */
    @GetMapping("/me")
    public Map<String, Object> me() {
        AppUser u = currentUser.get();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("username", u.getUsername());
        m.put("role", u.getRole());
        m.put("employeeId", u.getEmployeeId());
        String name = u.getUsername();
        if (u.getEmployeeId() != null) {
            Employee e = employees.findById(u.getEmployeeId()).orElse(null);
            if (e != null) name = e.getFirstName() + " " + e.getLastName();
        }
        m.put("name", name);
        return m;
    }
}
