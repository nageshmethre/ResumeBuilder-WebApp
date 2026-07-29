# AI-Powered ATS-Friendly Resume Builder

A professional, full-stack resume engineering workspace. The platform empowers job seekers to design modern, Applicant Tracking System (ATS) compliant resumes, perform keyword analyses, utilize AI content assistance, and generate print-friendly PDF exports.

## Core Features
*   **6 Premium Templates**: Modern (split-sidebar layout), Classic, Professional, Creative, Executive (serif-font centered design), and Student/Fresher (skills-first layout).
*   **15+ Comprehensive Sections**: Including Work History, Projects, Education, Certifications, Internships, Publications, Workshops, Coding Profiles, Languages, Achievements, References, and Interests.
*   **Real-time ATS Score Checker**: Live analysis (0-100%) checking for contact information, professional summary length, experience listings, profile links, and formatting parameters.
*   **Simulated AI Content Generation**: One-click AI Professional Summary generators, keyword optimizers (adds key technology keywords), and skill suggestions based on your target job headline.
*   **Advanced Customization Panel**: Real-time styling tools to adjust theme primary colors, font families, text sizes, line spacing, page margins, A4/US Letter dimensions, and dynamic section visibility/re-ordering.
*   **Data Integrity & Versioning**: Double-safeguard auto-saving draft controls, duplicate documents, rename options, and delete controls on the dashboard.
*   **Flexible Exports/Imports**: Download print-optimized PDFs (via OpenPDF), raw HTML files, or JSON backup configurations. Restore drafts by uploading a JSON backup file.

---

## Technical Architecture
*   **Backend**: Spring Boot 3.2.5, Spring Security, JWT (JSON Web Tokens), Spring Data JPA.
*   **Database**: MySQL 8.x relational database (managed via Hibernate schema auto-updates).
*   **PDF Compiler**: OpenPDF (for exact, styling-compliant PDF compilation).
*   **Frontend**: Bootstrap 5 framework, vanilla CSS (custom variables, dark/light theme options, glassmorphism UI, focus glows), vanilla JavaScript REST API hooks.

---

## Installation & Running Locally

### 1. Prerequisites
*   **Java JDK 17 or higher** (JDK 25 recommended)
*   **MySQL Server 8.x** running locally
*   **Maven** (a portable Maven wrapper is included in `.maven/`)

### 2. Database Configuration
1. Ensure MySQL server is running.
2. Initialize a database named `resume_builder` or execute `schema.sql` locally.
3. Configure your database username and password in `src/main/resources/application.properties` or set them as environment variables:
   ```powershell
   $env:DB_URL='jdbc:mysql://localhost:3306/resume_builder?createDatabaseIfNotExist=true&serverTimezone=UTC'
   $env:DB_USERNAME='root'
   $env:DB_PASSWORD='your_password_here'
   ```

### 3. Build the Application
Compile the project and package the executable JAR using the pre-configured Maven binaries:
```powershell
.\.maven\apache-maven-3.9.9\bin\mvn.cmd clean package -DskipTests
```

### 4. Launch the Server
Start the repackaged Spring Boot JAR:
```powershell
java -jar target/resume-builder-1.0.0.jar
```
*The server will boot up and bind to port **8085** to prevent conflicts on standard port 8080.*

### 5. Access the Platform
Open your browser and navigate to:
```url
http://localhost:8085
```

---

## REST API Specification

### Authentication
*   `POST /api/auth/register`: Register username, email, and password.
*   `POST /api/auth/login`: Authenticate and receive a Bearer JWT Token.

### Resumes Console
*   `GET /api/resumes`: Fetch all resumes owned by the authenticated user.
*   `POST /api/resumes`: Create a new resume.
*   `GET /api/resumes/{id}`: Fetch detailed resume attributes by ID.
*   `PUT /api/resumes/{id}`: Update a resume draft.
*   `DELETE /api/resumes/{id}`: Delete a resume.
*   `GET /api/resumes/{id}/pdf`: Generate and download print-ready PDF document.

Enjoy building ATS-compliant resumes!
