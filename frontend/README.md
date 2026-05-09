# CampusFlow Frontend

Angular frontend for CampusFlow, a role-based campus workflow platform for students, department officers, and admins.

## Tech Stack

- Angular 21
- TypeScript
- RxJS
- Standalone Angular components
- Custom SCSS
- Angular Router guards
- HTTP services and interceptor-based API access

## Features

- Professional role-based layout with sidebar and topbar
- Login, registration, and authenticated routing
- Student dashboard and request tracking
- Department request queue with approval actions
- Admin pages for departments, users, request types, and audit logs
- Notification dropdown and profile menu
- Status badges, empty states, loading states, and API error handling

## Project Path

```text
frontend/frontend
```

Run frontend commands from this directory.

## Prerequisites

- Node.js 20 or newer
- npm 10 or newer
- CampusFlow backend running on `http://localhost:8084`

## Install

```bash
npm install
```

## Run Locally

```bash
npm start
```

Open:

```text
http://localhost:4200
```

## Backend API URL

The API base URL is configured in:

```text
src/environments/environment.ts
```

Default:

```ts
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8084/api'
};
```

## Build

```bash
npm run build
```

The production build output is written to `dist/`.

## Test

```bash
npm test
```

## Important Routes

- `/welcome`
- `/login`
- `/register`
- `/student/dashboard`
- `/student/requests`
- `/student/requests/new`
- `/department/dashboard`
- `/department/queue`
- `/admin/dashboard`
- `/admin/departments`
- `/admin/users`
- `/admin/request-types`
- `/admin/audit-logs`

## Source Structure

```text
src/app/
+-- core/          API services, guards, interceptors, shared models
+-- features/      Auth, dashboards, requests, approvals, admin pages
+-- layout/        Application shell, sidebar, topbar
+-- shared/        Reusable UI components and pipes
```

## GitHub Notes

Do not commit generated folders:

- `node_modules/`
- `dist/`
- `.angular/`
- `out-tsc/`
- `coverage/`

The project root `.gitignore` and this module's `.gitignore` already exclude them.
