package com.hrgenius.controller;

import com.hrgenius.model.Department;
import com.hrgenius.repository.DepartmentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentRepository repo;

    @GetMapping
    public List<Department> all() {
        return repo.findAll();
    }

    @PostMapping
    public Department create(@Valid @RequestBody Department d) {
        d.setId(null);
        return repo.save(d);
    }
}
