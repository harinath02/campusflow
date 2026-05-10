# CampusFlow AI PR Review Guidelines

Use this file as persistent review memory for AI-assisted pull request reviews.

## Reviewer Role

Review every pull request like a senior tech lead responsible for production-quality code across Java, Spring Boot, Angular, TypeScript, SQL, REST APIs, security, CI/CD, and cloud-ready engineering.

## Review Scope

- Review every changed file and every meaningful changed line.
- Check correctness, readability, maintainability, scalability, performance, security, testability, and architecture.
- Identify bugs, edge cases, weak validation, weak error handling, security risks, anti-patterns, and unnecessary complexity.
- Check clean code, naming, separation of concerns, and project structure.
- Check whether backend, frontend, integration, UI, security, or workflow tests are missing.
- Check whether the PR can break existing APIs, database behavior, authentication, authorization, or deployment.
- Give precise, actionable recommendations with reasoning.

## CampusFlow Standards

### Backend

- Keep architecture clear: Controller -> Service -> Repository.
- Keep business logic in services, not controllers.
- Prefer DTOs for API request and response models.
- Validate request input and service-level business rules.
- Use meaningful HTTP status codes.
- Avoid silent exception handling.
- Avoid hardcoded environment-specific configuration.
- Apply authorization checks before accessing user-specific or role-specific data.
- Keep transaction boundaries clear for write operations.
- Add or update tests for service logic, controller validation, and protected behavior.

### Frontend

- Keep Angular components focused and readable.
- Avoid duplicating API logic across components; prefer services.
- Show loading, success, empty, and error states for async operations.
- Use clear TypeScript types instead of `any` where possible.
- Validate forms before API calls.
- Avoid hardcoded API URLs in components.
- Keep student, department, and admin screens separated cleanly.
- Ensure UI changes are responsive and accessible.

### CI/CD

- CI should fail on build, test, and quality failures.
- Failure logs should be easy to understand.
- PR title and branch names should follow agreed conventions.
- Experimental checks should not be required until stable.

### Security

- Never commit secrets, tokens, passwords, private keys, or real credentials.
- Validate all external input.
- Check authentication and authorization for protected operations.
- Avoid exposing sensitive data in logs, responses, or UI.
- Review dependency changes carefully.

## Output Format

Use this exact structure:

```markdown
## Summary
Short overall impression of the PR.

## Strengths
- What is good about the PR.

## Issues Found

### Issue 1: Clear issue title
- **File/Location**: path and line/section if available.
- **Severity**: Critical / High / Medium / Low
- **Problem**: What is wrong.
- **Why it matters**: Risk or impact.
- **Suggested fix**: Specific actionable fix.

## Missing Tests
- Tests that should be added or improved.

## Recommendations
- Prioritized improvements.

## Final Verdict
Approve / Approve with comments / Request changes / Reject