package com.hrgenius.controller;

import com.hrgenius.model.Candidate;
import com.hrgenius.model.Employee;
import com.hrgenius.model.JobOpening;
import com.hrgenius.repository.CandidateRepository;
import com.hrgenius.repository.JobOpeningRepository;
import com.hrgenius.service.EmployeeService;
import com.hrgenius.service.Errors;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecruitmentController {

    private final JobOpeningRepository jobs;
    private final CandidateRepository candidates;
    private final EmployeeService employeeService;

    private static final Set<String> STAGES = Set.of("APPLIED", "INTERVIEW", "OFFERED", "REJECTED");

    // ----- Job openings -----
    @GetMapping("/jobs")
    public List<JobOpening> allJobs() {
        return jobs.findAll();
    }

    @PostMapping("/jobs")
    public JobOpening createJob(@Valid @RequestBody JobOpening j) {
        j.setId(null);
        j.setStatus("OPEN");
        return jobs.save(j);
    }

    @PutMapping("/jobs/{id}/status")
    public JobOpening setJobStatus(@PathVariable Long id, @RequestParam String value) {
        if (!value.equals("OPEN") && !value.equals("CLOSED")) throw new IllegalArgumentException("Status must be OPEN or CLOSED");
        JobOpening j = jobs.findById(id).orElseThrow(() -> Errors.notFound("Job"));
        j.setStatus(value);
        return jobs.save(j);
    }

    // ----- Candidates -----
    @GetMapping("/candidates")
    public List<Candidate> allCandidates() {
        return candidates.findAll();
    }

    @PostMapping("/candidates")
    public Candidate addCandidate(@Valid @RequestBody Candidate c) {
        c.setId(null);
        c.setStage("APPLIED");
        c.setEmployeeId(null);
        return candidates.save(c);
    }

    @PutMapping("/candidates/{id}/stage")
    public Candidate setStage(@PathVariable Long id, @RequestParam String value) {
        if (!STAGES.contains(value)) throw new IllegalArgumentException("Stage must be one of " + STAGES);
        Candidate c = candidates.findById(id).orElseThrow(() -> Errors.notFound("Candidate"));
        if ("HIRED".equals(c.getStage())) throw new IllegalArgumentException("Candidate is already hired");
        c.setStage(value);
        return candidates.save(c);
    }

    /** Onboarding: turns a candidate into an employee (with a login) in one step. */
    @PostMapping("/candidates/{id}/hire")
    public Employee hire(@PathVariable Long id, @RequestParam(defaultValue = "30000") BigDecimal salary) {
        Candidate c = candidates.findById(id).orElseThrow(() -> Errors.notFound("Candidate"));
        if ("HIRED".equals(c.getStage())) throw new IllegalArgumentException("Candidate is already hired");
        JobOpening job = jobs.findById(c.getJobId()).orElseThrow(() -> Errors.notFound("Job"));

        String[] parts = c.getName().trim().split("\\s+", 2);
        Employee e = new Employee();
        e.setFirstName(parts[0]);
        e.setLastName(parts.length > 1 ? parts[1] : "-");
        e.setEmail(c.getEmail());
        e.setJobTitle(job.getTitle());
        e.setDepartmentId(job.getDepartmentId());
        e.setSalary(salary);
        Employee saved = employeeService.create(e);

        c.setStage("HIRED");
        c.setEmployeeId(saved.getId());
        candidates.save(c);
        return saved;
    }
}
