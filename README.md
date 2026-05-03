# CampusFlow

CampusFlow is a role-based campus workflow platform for student services such as no-dues, certificates, complaints, department approvals, notifications, and audit tracking. The project is organized as a full-stack application with a Spring Boot backend and an Angular frontend.

## Tech Stack

Backend:
- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Lombok
- Validation
- Swagger/OpenAPI

Frontend:
- Angular 21
- TypeScript
- RxJS
- Standalone components
- Custom SCSS workflow UI

## Project Structure

```text
campusflow-simple-nodues-ui/
+-- backend/
|   +-- backend/
|       +-- backend/        Spring Boot API
+-- frontend/
    +-- frontend/           Angular application
```

## Core Features

- Role-based student, department officer, and admin workflows
- Login API with JWT response payload
- Request creation, submission, approval, hold, and rejection flows
- Department-wise approval tracking for no-dues style workflows
- Notifications for request activity
- Audit logs for key campus workflow actions
- Admin management for departments, users, roles, and request types
- Swagger UI for backend API exploration
- Responsive Angular UI with dashboards, request queue, timeline details, and admin pages

## Local Setup

Start the backend first, then the frontend.

### Backend

```bash
cd backend/backend/backend
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd backend\backend\backend
.\mvnw.cmd spring-boot:run
```

The backend runs on `http://localhost:8084`.

Swagger UI is available at:

```text
http://localhost:8084/swagger-ui/index.html
```

### Frontend

```bash
cd frontend/frontend
npm install
npm start
```

The frontend runs on `http://localhost:4200`.

The Angular API base URL is configured in:

```text
frontend/frontend/src/environments/environment.ts
```

## Database

Create a PostgreSQL database named `campusflow` before starting the backend:

```sql
CREATE DATABASE campusflow;
```

Default local database settings are in `backend/backend/backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/campusflow
    username: username
    password: password
```

Change these values for your local PostgreSQL setup before running the app.

## API Response Format

The backend uses a consistent response wrapper:

```json
{
  "status": "success",
  "message": "Operation completed",
  "data": {}
}
```

Errors follow the same structure:

```json
{
  "status": "error",
  "message": "Something went wrong",
  "data": null
}
```

## Useful Commands

Backend:

```bash
cd backend/backend/backend
./mvnw clean test
./mvnw spring-boot:run
```

Frontend:

```bash
cd frontend/frontend
npm install
npm run build
npm test
```

## GitHub Push Checklist

1. Create an empty repository on GitHub.
2. Initialize git from this project root if it is not already initialized:

```bash
git init
git add .
git commit -m "Prepare CampusFlow full-stack project"
git branch -M main
git remote add origin https://github.com/<your-username>/<repo-name>.git
git push -u origin main
```

Before pushing, confirm that generated folders such as `node_modules`, `dist`, `.angular`, `out-tsc`, and `target` are not staged.
