package com.hrgenius.config;

import com.hrgenius.model.*;
import com.hrgenius.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Adds demo data for HRGenius.
 * Existing data is preserved and demo data is added only once.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final EmployeeRepository empRepo;
    private final JobOpeningRepository jobRepo;
    private final CandidateRepository candidateRepo;
    private final AttendanceRepository attendanceRepo;
    private final LeaveRepository leaveRepo;
    private final PayslipRepository payslipRepo;
    private final PerformanceReviewRepository reviewRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {

        // Keep the original basic accounts/data working
        Department eng = getOrCreateDepartment("Engineering");
        Department hr = getOrCreateDepartment("Human Resources");
        Department finance = getOrCreateDepartment("Finance");
        Department sales = getOrCreateDepartment("Sales");
        Department marketing = getOrCreateDepartment("Marketing");

        Employee admin = getOrCreateEmployee(
                "Asha", "Admin",
                "admin@hrgenius.com",
                "System Administrator",
                hr,
                "90000"
        );

        Employee hrEmp = getOrCreateEmployee(
                "Hema", "Rao",
                "hr@hrgenius.com",
                "HR Manager",
                hr,
                "70000"
        );

        Employee john = getOrCreateEmployee(
                "John", "Doe",
                "john@hrgenius.com",
                "Software Engineer",
                eng,
                "60000"
        );

        createUserIfMissing("admin", "admin123", "ADMIN", admin.getId());
        createUserIfMissing("hr", "hr123", "HR", hrEmp.getId());
        createUserIfMissing("john", "john123", "EMPLOYEE", john.getId());

        /*
         * Demo data is added only once.
         * This prevents Render/local restarts from creating duplicates.
         */
        if (empRepo.findAll().stream()
                .noneMatch(e -> "priya.sharma@hrgenius.com".equals(e.getEmail()))) {

            seedEmployees(eng, hr, finance, sales, marketing);
        }

        if (jobRepo.count() <= 1) {
            seedJobs(eng, finance, marketing, hr);
        }

        if (candidateRepo.count() == 0) {
            seedCandidates();
        }

        if (attendanceRepo.count() == 0) {
            seedAttendance();
        }

        if (leaveRepo.count() == 0) {
            seedLeaves();
        }

        if (reviewRepo.count() == 0) {
            seedReviews();
        }

        System.out.println("HRGenius demo data check completed.");
    }

    // ---------------------------------------------------------
    // DEPARTMENTS
    // ---------------------------------------------------------

    private Department getOrCreateDepartment(String name) {
        return deptRepo.findAll()
                .stream()
                .filter(d -> name.equalsIgnoreCase(d.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setName(name);
                    return deptRepo.save(d);
                });
    }

    // ---------------------------------------------------------
    // EMPLOYEES
    // ---------------------------------------------------------

    private Employee getOrCreateEmployee(
            String first,
            String last,
            String email,
            String title,
            Department department,
            String salary
    ) {
        return empRepo.findAll()
                .stream()
                .filter(e -> email.equalsIgnoreCase(e.getEmail()))
                .findFirst()
                .orElseGet(() -> {
                    Employee e = new Employee();
                    e.setFirstName(first);
                    e.setLastName(last);
                    e.setEmail(email);
                    e.setJobTitle(title);
                    e.setDepartmentId(department.getId());
                    e.setSalary(new BigDecimal(salary));
                    e.setJoinDate(LocalDate.now().minusMonths(6));
                    e.setStatus("ACTIVE");
                    return empRepo.save(e);
                });
    }

    private void seedEmployees(
            Department eng,
            Department hr,
            Department finance,
            Department sales,
            Department marketing
    ) {

        getOrCreateEmployee(
                "Priya", "Sharma",
                "priya.sharma@hrgenius.com",
                "Data Analyst",
                eng,
                "58000"
        );

        getOrCreateEmployee(
                "Rahul", "Mehta",
                "rahul.mehta@hrgenius.com",
                "Backend Developer",
                eng,
                "65000"
        );

        getOrCreateEmployee(
                "Neha", "Verma",
                "neha.verma@hrgenius.com",
                "Frontend Developer",
                eng,
                "62000"
        );

        getOrCreateEmployee(
                "Rohan", "Gupta",
                "rohan.gupta@hrgenius.com",
                "Software Engineer",
                eng,
                "60000"
        );

        getOrCreateEmployee(
                "Kavya", "Rao",
                "kavya.rao@hrgenius.com",
                "HR Executive",
                hr,
                "45000"
        );

        getOrCreateEmployee(
                "Ananya", "Kapoor",
                "ananya.kapoor@hrgenius.com",
                "Recruitment Specialist",
                hr,
                "50000"
        );

        getOrCreateEmployee(
                "Vivek", "Malhotra",
                "vivek.malhotra@hrgenius.com",
                "Finance Analyst",
                finance,
                "55000"
        );

        getOrCreateEmployee(
                "Ishita", "Singh",
                "ishita.singh@hrgenius.com",
                "Accountant",
                finance,
                "48000"
        );

        getOrCreateEmployee(
                "Arjun", "Kapoor",
                "arjun.kapoor@hrgenius.com",
                "Sales Executive",
                sales,
                "42000"
        );

        getOrCreateEmployee(
                "Meera", "Joshi",
                "meera.joshi@hrgenius.com",
                "Sales Manager",
                sales,
                "68000"
        );

        getOrCreateEmployee(
                "Aditya", "Verma",
                "aditya.verma@hrgenius.com",
                "Marketing Executive",
                marketing,
                "44000"
        );

        getOrCreateEmployee(
                "Simran", "Kaur",
                "simran.kaur@hrgenius.com",
                "Content Specialist",
                marketing,
                "46000"
        );

        getOrCreateEmployee(
                "Nikhil", "Bansal",
                "nikhil.bansal@hrgenius.com",
                "DevOps Engineer",
                eng,
                "72000"
        );

        getOrCreateEmployee(
                "Sanya", "Mehta",
                "sanya.mehta@hrgenius.com",
                "HR Associate",
                hr,
                "40000"
        );

        getOrCreateEmployee(
                "Varun", "Shah",
                "varun.shah@hrgenius.com",
                "Financial Analyst",
                finance,
                "57000"
        );

        System.out.println("Demo employees added.");
    }

    // ---------------------------------------------------------
    // USERS
    // ---------------------------------------------------------

    private void createUserIfMissing(
            String username,
            String password,
            String role,
            Long employeeId
    ) {
        if (userRepo.existsByUsername(username)) {
            return;
        }

        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPassword(encoder.encode(password));
        u.setRole(role);
        u.setEmployeeId(employeeId);

        userRepo.save(u);
    }

    // ---------------------------------------------------------
    // JOBS
    // ---------------------------------------------------------

    private void seedJobs(
            Department eng,
            Department finance,
            Department marketing,
            Department hr
    ) {

        createJob(
                "Java Developer",
                eng,
                "Build and maintain Spring Boot services for HRGenius."
        );

        createJob(
                "Data Analyst",
                finance,
                "Analyse HR and business data and prepare reports."
        );

        createJob(
                "Marketing Associate",
                marketing,
                "Support digital marketing and content campaigns."
        );

        createJob(
                "HR Executive",
                hr,
                "Support recruitment, employee relations and HR operations."
        );

        createJob(
                "Backend Developer",
                eng,
                "Develop REST APIs and backend services."
        );

        System.out.println("Demo jobs added.");
    }

    private void createJob(
            String title,
            Department department,
            String description
    ) {
        JobOpening job = new JobOpening();
        job.setTitle(title);
        job.setDepartmentId(department.getId());
        job.setDescription(description);
        job.setStatus("OPEN");

        jobRepo.save(job);
    }

    // ---------------------------------------------------------
    // CANDIDATES
    // ---------------------------------------------------------

    private void seedCandidates() {

        List<JobOpening> jobs = jobRepo.findAll();

        if (jobs.isEmpty()) {
            return;
        }

        createCandidate(
                jobs.get(0).getId(),
                "Riya Mehta",
                "riya.mehta@example.com",
                "APPLIED"
        );

        createCandidate(
                jobs.get(0).getId(),
                "Karan Singh",
                "karan.singh@example.com",
                "INTERVIEW"
        );

        createCandidate(
                jobs.get(1).getId(),
                "Aman Gupta",
                "aman.gupta@example.com",
                "OFFERED"
        );

        createCandidate(
                jobs.get(1).getId(),
                "Sneha Kapoor",
                "sneha.kapoor@example.com",
                "APPLIED"
        );

        createCandidate(
                jobs.get(2).getId(),
                "Dev Sharma",
                "dev.sharma@example.com",
                "REJECTED"
        );

        createCandidate(
                jobs.get(3).getId(),
                "Pooja Verma",
                "pooja.verma@example.com",
                "INTERVIEW"
        );

        System.out.println("Demo candidates added.");
    }

    private void createCandidate(
            Long jobId,
            String name,
            String email,
            String stage
    ) {
        Candidate c = new Candidate();
        c.setJobId(jobId);
        c.setName(name);
        c.setEmail(email);
        c.setStage(stage);
        c.setEmployeeId(null);

        candidateRepo.save(c);
    }

    // ---------------------------------------------------------
    // ATTENDANCE
    // ---------------------------------------------------------

    private void seedAttendance() {

        List<Employee> employees = empRepo.findAll();

        for (Employee employee : employees) {

            if (!"ACTIVE".equals(employee.getStatus())) {
                continue;
            }

            for (int i = 1; i <= 5; i++) {

                AttendanceRecord record = new AttendanceRecord();

                record.setEmployeeId(employee.getId());
                record.setWorkDate(LocalDate.now().minusDays(i));

                record.setCheckIn(
                        LocalTime.of(9, 0)
                                .plusMinutes(employee.getId() % 15)
                );

                record.setCheckOut(
                        LocalTime.of(17, 30)
                                .plusMinutes(employee.getId() % 15)
                );

                attendanceRepo.save(record);
            }
        }

        System.out.println("Demo attendance added.");
    }

    // ---------------------------------------------------------
    // LEAVES
    // ---------------------------------------------------------

    private void seedLeaves() {

        List<Employee> employees = empRepo.findAll();

        if (employees.size() < 5) {
            return;
        }

        createLeave(
                employees.get(1).getId(),
                "Casual",
                LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(19),
                "Personal work",
                "APPROVED"
        );

        createLeave(
                employees.get(2).getId(),
                "Sick",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(9),
                "Not feeling well",
                "APPROVED"
        );

        createLeave(
                employees.get(3).getId(),
                "Annual",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(9),
                "Family vacation",
                "PENDING"
        );

        createLeave(
                employees.get(4).getId(),
                "Casual",
                LocalDate.now().plusDays(14),
                LocalDate.now().plusDays(14),
                "Personal work",
                "PENDING"
        );

        createLeave(
                employees.get(5).getId(),
                "Sick",
                LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(29),
                "Medical appointment",
                "REJECTED"
        );

        System.out.println("Demo leaves added.");
    }

    private void createLeave(
            Long employeeId,
            String type,
            LocalDate start,
            LocalDate end,
            String reason,
            String status
    ) {
        LeaveRequest leave = new LeaveRequest();

        leave.setEmployeeId(employeeId);
        leave.setLeaveType(type);
        leave.setStartDate(start);
        leave.setEndDate(end);
        leave.setReason(reason);
        leave.setStatus(status);

        leaveRepo.save(leave);
    }

    // ---------------------------------------------------------
    // PERFORMANCE
    // ---------------------------------------------------------

    private void seedReviews() {

        List<Employee> employees = empRepo.findAll();

        for (int i = 1; i < employees.size() && i <= 8; i++) {

            Employee employee = employees.get(i);

            PerformanceReview review = new PerformanceReview();

            review.setEmployeeId(employee.getId());
            review.setPeriod("Q3 2026");
            review.setRating(3 + (i % 3));
            review.setReviewer("hr");
            review.setComments(
                    "Good performance with consistent progress and contribution to the team."
            );

            reviewRepo.save(review);
        }

        System.out.println("Demo performance reviews added.");
    }
}