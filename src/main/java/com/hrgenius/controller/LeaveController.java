package com.hrgenius.controller;

import com.hrgenius.model.LeaveRequest;
import com.hrgenius.repository.LeaveRepository;
import com.hrgenius.service.CurrentUser;
import com.hrgenius.service.Errors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveRepository repo;
    private final CurrentUser currentUser;

    @PostMapping("/apply")
    public LeaveRequest apply(@RequestBody LeaveRequest in) {
        if (in.getStartDate() == null || in.getEndDate() == null) throw new IllegalArgumentException("Start and end dates are required");
        if (in.getEndDate().isBefore(in.getStartDate())) throw new IllegalArgumentException("End date is before start date");
        in.setId(null);
        in.setEmployeeId(currentUser.employeeId());
        in.setStatus("PENDING");
        return repo.save(in);
    }

    @GetMapping("/my")
    public List<LeaveRequest> mine() {
        return repo.findByEmployeeIdOrderByStartDateDesc(currentUser.employeeId());
    }

    @GetMapping
    public List<LeaveRequest> all() {
        return repo.findAllByOrderByStartDateDesc();
    }

    @PutMapping("/{id}/status")
    public LeaveRequest decide(@PathVariable Long id, @RequestParam String value) {
        if (!value.equals("APPROVED") && !value.equals("REJECTED")) throw new IllegalArgumentException("Status must be APPROVED or REJECTED");
        LeaveRequest l = repo.findById(id).orElseThrow(() -> Errors.notFound("Leave request"));
        l.setStatus(value);
        return repo.save(l);
    }
}
