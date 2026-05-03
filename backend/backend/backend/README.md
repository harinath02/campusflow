# CampusFlow Backend

Spring Boot API for CampusFlow, a role-based campus service workflow platform. The backend owns authentication, users, departments, request types, requests, approval status, notifications, audit logs, and supporting campus service modules.

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Data JPA
- PostgreSQL
- Lombok
- Jakarta Validation
- Swagger/OpenAPI via Springdoc

## Module Path

```text
backend/backend/backend
```

Run commands from this directory.

## Prerequisites

- Java 21
- PostgreSQL
- Maven wrapper included in the project

## Database Setup

Create a local PostgreSQL database:

```sql
CREATE DATABASE campusflow;
```

Update `src/main/resources/application.yml` if your local credentials differ:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/campusflow
    username: postgres
    password: postgres
```

The app currently uses `spring.jpa.hibernate.ddl-auto=update` for local development.

## Run Locally

Linux/macOS:

```bash
cd backend/backend/backend
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
cd backend\backend\backend
.\mvnw.cmd spring-boot:run
```

The API runs on:

```text
http://localhost:8084
```

Swagger UI:

```text
http://localhost:8084/swagger-ui/index.html
```

## Build and Test

```bash
./mvnw clean test
./mvnw clean package
```

The packaged application is generated under `target/`.

## Main API Areas

- `POST /api/auth/login`
- `/api/users`
- `/api/roles`
- `/api/departments`
- `/api/request-types`
- `/api/requests`
- `/api/no-dues`
- `/api/certificates`
- `/api/complaints`
- `/api/notifications`
- `/api/audit-logs`

## Local Configuration

Important settings are in:

```text
src/main/resources/application.yml
```

Do not commit production secrets. For deployment, move credentials and JWT secrets to environment variables or your hosting provider's secret manager.

## Notes for Reviewers

- Controllers return DTOs instead of exposing JPA entities directly.
- The API response wrapper keeps success and error responses consistent.
- The app includes global exception handling for validation, not found, duplicate resource, bad request, and generic server errors.
- Swagger/OpenAPI is enabled for quick endpoint testing.

