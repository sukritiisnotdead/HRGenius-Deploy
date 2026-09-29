package com.hrgenius.controller;

import com.hrgenius.model.AttendanceRecord;
import com.hrgenius.repository.AttendanceRepository;
import com.hrgenius.service.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceRepository repo;
    private final CurrentUser currentUser;

    @PostMapping("/check-in")
    public AttendanceRecord checkIn() {
        Long eid = currentUser.employeeId();
        LocalDate today = LocalDate.now();
        if (repo.findByEmployeeIdAndWorkDate(eid, today).isPresent()) {
            throw new IllegalArgumentException("You have already checked in today");
        }
        AttendanceRecord r = new AttendanceRecord();
        r.setEmployeeId(eid);
        r.setWorkDate(today);
        r.setCheckIn(LocalTime.now().withNano(0));
        return repo.save(r);
    }

    @PostMapping("/check-out")
    public AttendanceRecord checkOut() {
        Long eid = currentUser.employeeId();
        AttendanceRecord r = repo.findByEmployeeIdAndWorkDate(eid, LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("Check in first"));
        if (r.getCheckOut() != null) throw new IllegalArgumentException("You have already checked out today");
        r.setCheckOut(LocalTime.now().withNano(0));
        return repo.save(r);
    }

    @GetMapping("/my")
    public List<AttendanceRecord> mine() {
        return repo.findByEmployeeIdOrderByWorkDateDesc(currentUser.employeeId());
    }

    @GetMapping
    public List<AttendanceRecord> all() {
        return repo.findAllByOrderByWorkDateDesc();
    }
}
