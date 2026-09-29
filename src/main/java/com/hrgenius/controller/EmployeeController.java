package com.hrgenius.controller;

import com.hrgenius.model.Employee;
import com.hrgenius.repository.EmployeeRepository;
import com.hrgenius.service.CurrentUser;
import com.hrgenius.service.EmployeeService;
import com.hrgenius.service.Errors;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository repo;
    private final EmployeeService service;
    private final CurrentUser currentUser;

    @GetMapping
    public List<Employee> all() {
        return repo.findAll();
    }

    @GetMapping("/me")
    public Employee me() {
        return repo.findById(currentUser.employeeId()).orElseThrow(() -> Errors.notFound("Employee"));
    }

    @PostMapping
    public Employee create(@Valid @RequestBody Employee e) {
        return service.create(e);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @Valid @RequestBody Employee in) {
        Employee e = repo.findById(id).orElseThrow(() -> Errors.notFound("Employee"));
        e.setFirstName(in.getFirstName());
        e.setLastName(in.getLastName());
        e.setEmail(in.getEmail());
        e.setPhone(in.getPhone());
        e.setJobTitle(in.getJobTitle());
        e.setDepartmentId(in.getDepartmentId());
        e.setSalary(in.getSalary());
        return repo.save(e);
    }

    /** We never delete people (their history would break). We mark them INACTIVE instead. */
    @DeleteMapping("/{id}")
    public Map<String, String> deactivate(@PathVariable Long id) {
        Employee e = repo.findById(id).orElseThrow(() -> Errors.notFound("Employee"));
        e.setStatus("INACTIVE");
        repo.save(e);
        return Map.of("message", "Employee marked inactive");
    }
}
