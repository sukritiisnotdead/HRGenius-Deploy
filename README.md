# HRGenius – HR Management System (Java + Spring Boot)

Modules: Login & roles (Admin / HR / Employee) • Employees & Departments • Recruitment & Onboarding •
Attendance • Leave • Payroll • Performance reviews • HR Dashboard (analytics)

The database is a small file inside the project (H2), so you do NOT need to install MySQL.
The website is served by Spring Boot itself, so you do NOT need Node.js.

---------------------------------------------------------------
## PART A – One-time installs (about 15 minutes)

1. **Install Java 17 or 21 (JDK).**
   - Go to https://adoptium.net and download "Temurin 17 (LTS)" (or 21) for your computer, run the installer.
   - On Windows, tick the option "Set JAVA_HOME variable" / "Add to PATH" during install.
   - Check it: open Command Prompt (Windows) or Terminal (Mac) and type `java -version`.
     You should see 17.x or 21.x. (Avoid Java 23 or newer for now, the tools don't support it yet.)

2. **Install IntelliJ IDEA Community Edition** (free): https://www.jetbrains.com/idea/download
   IntelliJ already contains Maven, so you do not install Maven separately.

## PART B – Open and run the project

1. Unzip `hrgenius.zip` somewhere easy, e.g. `Documents/hrgenius`.
2. Open IntelliJ → **Open** → choose the `hrgenius` folder (the one containing `pom.xml`) → **OK**.
   If asked "Trust project?", click **Trust Project**.
3. Wait. The bottom-right shows a progress bar ("Resolving dependencies"). The first time can take
   2–5 minutes because Maven downloads libraries. **You need internet for this step.**
4. If IntelliJ asks about the SDK, or you see "Project JDK is not defined":
   File → Project Structure → Project → SDK → pick the JDK you installed (17 or 21).
   (If Lombok pops up "Enable annotation processing", click **Enable**.)
5. In the left tree open: `src/main/java/com/hrgenius/HrGeniusApplication.java`
6. Click the green ▶ triangle next to `public static void main` → **Run 'HrGeniusApplication'**.
7. The console at the bottom should end with something like `Started HrGeniusApplication in 4 seconds`.
8. Open your browser at **http://localhost:8080**

## PART C – Log in and try it

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN (sees everything) |
| hr | hr123 | HR (sees everything) |
| john | john123 | EMPLOYEE (only "My ..." pages) |

A good first test:
1. Log in as **hr** → Recruitment → add a candidate → click **Hire** (enter a salary).
2. Employees → the new person appears. Log out, and log in as them
   (username = part of email before `@`, password `welcome123`).
3. As HR → Payroll → **Generate payslips**. Then as john → My Payslips.
4. As john → My Leaves → apply. As hr → Leaves → Approve.
5. Dashboard shows the numbers and bars.

To stop the app: click the red ■ Stop button in IntelliJ.

---------------------------------------------------------------
## PART D – Make a standalone file you can run anywhere ("deploy")

In IntelliJ open the **Maven** tab (right side) → hrgenius → Lifecycle → double-click **package**.
This creates `target/hrgenius.jar`. Then, from a terminal in the project folder:

    java -jar target/hrgenius.jar

Open http://localhost:8080 again. Copy the jar to any computer with Java and it works the same.

**Docker (optional):** if you have Docker installed: `docker build -t hrgenius .` then
`docker run -p 8080:8080 hrgenius`

## VS Code instead of IntelliJ?
Install the "Extension Pack for Java" and "Spring Boot Extension Pack", install Maven
(https://maven.apache.org), then in a terminal in the project folder run `mvn spring-boot:run`.

---------------------------------------------------------------
## Problems? (very common, easy fixes)

| What you see | Fix |
|---|---|
| `Port 8080 was already in use` | Stop other apps on 8080, or add `server.port=9090` to `application.properties` and open localhost:9090 |
| Red errors everywhere, "cannot find symbol getX()" | Lombok not active: Settings → Build → Compiler → Annotation Processors → tick "Enable annotation processing", then Build → Rebuild Project |
| "Unsupported class file / release version" | Wrong JDK. Project Structure → set SDK to 17 or 21 |
| Maven can't download | Check internet; click the Maven tab's refresh (🔄) icon |
| Forgot data / want a clean start | Stop the app, delete the `data` folder in the project, run again (demo data is recreated) |
| Login says wrong password | Use exactly `admin` / `admin123` (lowercase) |

## How the project is organised (so you can explain it in viva)

    src/main/java/com/hrgenius
      model/       Database tables as Java classes (Entities)
      repository/  Interfaces that read/write the tables (Spring Data JPA)
      service/     Business logic (payroll calculation, creating logins, who is logged in)
      controller/  The REST API: URLs like /api/employees that the web page calls
      config/      Security rules (who may call what), demo data, error handling
    src/main/resources/static   The website: index.html, css/style.css, js/app.js
    src/main/resources/application.properties   Settings (port, database)

Flow of one request: browser (app.js) → Controller → Repository → Database and back as JSON.

Security: passwords are stored hashed (BCrypt). Every API call carries the login (HTTP Basic).
`SecurityConfig` decides: employees may only use their own "My ..." endpoints; HR/Admin may use the rest.

Payroll rules (in `PayrollService`, change freely): HRA = 40% of basic, PF = 12% of basic,
tax = 10% of gross above 50,000, net = gross − PF − tax.

## Ideas to extend it later
Edit-employee form, JWT login instead of Basic, MySQL, leave balances, PDF payslips, charts, unit tests.
