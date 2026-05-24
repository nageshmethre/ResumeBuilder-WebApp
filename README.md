# Resume Builder — Quick Start

This is a simple Spring Boot web app that lets you create, preview, and download resumes as PDF.

Goal: get the app running locally on Windows in a few easy steps.

## What you need
- Java 17 (JDK)
- MySQL Server 8.x
- Command prompt or PowerShell

## Easy Setup

1. Place the project folder at `C:\resume-builder` so it contains `application.properties` and `resume-builder-1.0.0.jar`.

2. Install Java 17 and verify:

```powershell
winget install Oracle.JDK.17
java -version
```

3. Install MySQL and ensure the server is running:

```powershell
winget install Oracle.MySQL
```

If needed, add MySQL to PATH:

```powershell
setx PATH "%PATH%;C:\Program Files\MySQL\MySQL Server 8.0\bin" /M
```

4. Initialize the database (run the SQL in `schema.sql`) or create a database named `resume_builder`.

5. Confirm or set DB credentials in `src/main/resources/application.properties` or via environment variables:

PowerShell example:
```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/resume_builder'
$env:DB_USERNAME='root'
$env:DB_PASSWORD='your_password'
```

6. Start the app (from `C:\resume-builder`):

```powershell
java -jar resume-builder-1.0.0.jar
```

7. Open the app in your browser:

http://localhost:8080

Stop the app with `Ctrl+C` in the terminal.

## Quick Troubleshooting
- "java not found": confirm `java -version` shows Java 17.
- MySQL errors: ensure MySQL service is running and credentials match `application.properties`.
- Port 8080 in use: change `server.port` in `application.properties`.

## If you want more
- I can add examples for `application.properties` values, or update the original README file in your Downloads folder.

Enjoy building resumes!

