# Story 0.3: Local Development Environment Setup

Status: ready-for-dev

## Story

As a developer,
I want a Docker Compose configuration that starts MySQL 8 and MailHog with a single command, plus `.env.example` files and a README with full setup instructions,
so that every team member has an identical, reproducible dev environment ready in under 2 minutes.

## Acceptance Criteria

1. `docker-compose up -d` starts a MySQL 8 container on port 3306 with the `ecotrack` database initialized
2. `docker-compose up -d` starts a MailHog container on port 1025 (SMTP) and 8025 (web UI)
3. Both containers use named volumes for data persistence across restarts
4. `.env.example` files exist at project root, `frontend/`, and `backend/` with all required variable names and example values (no real secrets)
5. `README.md` (project root) documents the complete setup sequence: prerequisites, clone, env file copy, `docker-compose up`, `ng serve`, `mvn spring-boot:run`
6. The Angular dev server proxies `/api` calls to Spring Boot without CORS errors, using the `CORS_ALLOWED_ORIGINS` env var on the backend

## Tasks / Subtasks

- [ ] Task 1 — Write `docker-compose.yml` at project root (AC: #1, #2, #3)
  - [ ] 1.1 Define `mysql` service: image `mysql:8.0`, port `3306:3306`, env vars from `.env`, named volume `mysql_data`
  - [ ] 1.2 Define `mailhog` service: image `mailhog/mailhog`, ports `1025:1025` (SMTP) and `8025:8025` (web UI)
  - [ ] 1.3 Add `depends_on: mysql` to a `backend` profile (optional — see Dev Notes)
  - [ ] 1.4 Define named volumes at the bottom: `mysql_data:`
  - [ ] 1.5 Verify `docker-compose up -d` starts both containers cleanly

- [ ] Task 2 — Write `.env.example` files (AC: #4)
  - [ ] 2.1 Create `.env.example` at project root (MySQL + MailHog container vars)
  - [ ] 2.2 Create `frontend/.env.example` (`API_URL`, `SSE_URL`, `POLL_INTERVAL`)
  - [ ] 2.3 Create `backend/.env.example` (all vars referenced in `application.properties`)
  - [ ] 2.4 Copy each `.env.example` to `.env` locally (never commit `.env`)
  - [ ] 2.5 Verify `.env` is in `.gitignore` at project root

- [ ] Task 3 — Configure Angular proxy for local dev (AC: #6)
  - [ ] 3.1 Create `frontend/proxy.conf.json` routing `/api` to `http://localhost:8080`
  - [ ] 3.2 Update `frontend/angular.json` to reference `proxy.conf.json` under `serve.options`
  - [ ] 3.3 Verify `ng serve` proxies `/api/v1/...` calls to Spring Boot without CORS error

- [ ] Task 4 — Write root `README.md` setup section (AC: #5)
  - [ ] 4.1 Add "Prerequisites" section (Node 20+, Java 21, Maven, Docker Desktop)
  - [ ] 4.2 Add "Getting started" steps (clone → copy `.env` files → `docker-compose up -d` → `ng serve` + `mvn spring-boot:run`)
  - [ ] 4.3 Add "Verify" section: URLs to check (Angular app, Swagger UI, MailHog UI)
  - [ ] 4.4 Add "Team workflow" section with `sprint-status.yaml` update instructions

- [ ] Task 5 — Smoke test full stack (AC: all)
  - [ ] 5.1 `docker-compose up -d` — both containers `Up`
  - [ ] 5.2 `mvn spring-boot:run -Dspring-boot.run.profiles=dev` — app starts, Flyway V1 runs, no error
  - [ ] 5.3 `ng serve` — app loads at `http://localhost:4200`
  - [ ] 5.4 Angular app makes a request to `/api/v1/` — proxied to Spring Boot, no CORS error in browser console
  - [ ] 5.5 MailHog UI accessible at `http://localhost:8025`

## Dev Notes

### `docker-compose.yml` full content

```yaml
version: "3.9"

services:
  mysql:
    image: mysql:8.0
    container_name: ecotrack-mysql
    restart: unless-stopped
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE:-ecotrack}
      MYSQL_USER: ${MYSQL_USER:-ecotrack}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  mailhog:
    image: mailhog/mailhog:latest
    container_name: ecotrack-mailhog
    restart: unless-stopped
    ports:
      - "1025:1025"   # SMTP — used by Spring Boot JavaMailSender
      - "8025:8025"   # Web UI — view sent emails at http://localhost:8025

volumes:
  mysql_data:
    name: ecotrack_mysql_data
```

> **No `backend` or `frontend` service** in Docker Compose — students run these locally via `ng serve` and `mvn spring-boot:run`. Docker Compose is for infrastructure only.

### Root `.env.example`

```dotenv
# ── MySQL ────────────────────────────────────────────────────────────────────
MYSQL_ROOT_PASSWORD=changeme_root
MYSQL_DATABASE=ecotrack
MYSQL_USER=ecotrack
MYSQL_PASSWORD=changeme

# ── Spring Boot (also used in backend/.env) ───────────────────────────────────
DB_URL=jdbc:mysql://localhost:3306/ecotrack?useSSL=false&allowPublicKeyRetrieval=true
DB_USERNAME=ecotrack
DB_PASSWORD=changeme
JWT_SECRET=change_this_to_a_long_random_secret_min_32_chars
JWT_EXPIRATION_HOURS=8
CORS_ALLOWED_ORIGINS=http://localhost:4200
SMTP_HOST=localhost
SMTP_PORT=1025
SMTP_USERNAME=
SMTP_PASSWORD=
LOG_LEVEL=DEBUG
UPLOAD_DIR=./uploads
```

### `backend/.env.example`

```dotenv
DB_URL=jdbc:mysql://localhost:3306/ecotrack?useSSL=false&allowPublicKeyRetrieval=true
DB_USERNAME=ecotrack
DB_PASSWORD=changeme
JWT_SECRET=change_this_to_a_long_random_secret_min_32_chars
JWT_EXPIRATION_HOURS=8
CORS_ALLOWED_ORIGINS=http://localhost:4200
SMTP_HOST=localhost
SMTP_PORT=1025
SMTP_USERNAME=
SMTP_PASSWORD=
LOG_LEVEL=DEBUG
UPLOAD_DIR=./uploads
```

### `frontend/.env.example`

```dotenv
API_URL=http://localhost:4200/api
SSE_URL=http://localhost:8080/api/v1/sse
POLL_INTERVAL=30000
```

> The Angular app reads `apiUrl` from `environment.ts` (not from `.env` directly) — the `.env.example` documents the intended values for reference and for any future CI/CD build step.

### Angular proxy configuration

```json
// frontend/proxy.conf.json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "info"
  }
}
```

In `frontend/angular.json`, under `projects.ecotrack-office-frontend.architect.serve.options`:
```json
{
  "proxyConfig": "proxy.conf.json"
}
```

> With this proxy, the Angular app calls `/api/v1/users` which is forwarded to `http://localhost:8080/api/v1/users` by the dev server — eliminating CORS entirely in local dev. In production, a reverse proxy (nginx, etc.) would do the same.

### `.gitignore` — verify these are present

```gitignore
# Environment files — never commit real secrets
.env
frontend/.env
backend/.env

# Upload directory
uploads/

# Docker volumes (managed by Docker, not git)
# (no need to add — Docker volumes are outside the repo)
```

### README.md — setup sequence to document

```markdown
## Getting Started

### Prerequisites

| Tool | Minimum version | Check |
|------|----------------|-------|
| Node.js | 20.19.0 or 22.12.0+ | `node -v` |
| npm | 10+ | `npm -v` |
| Java | 21 LTS | `java -version` |
| Maven | 3.9+ | `mvn -v` |
| Docker Desktop | latest | `docker -v` |
| Angular CLI | 21 | `ng version` |

### Setup steps

1. Clone the repository
   ```bash
   git clone <repo-url>
   cd ecotrack-office
   ```

2. Copy environment files
   ```bash
   cp .env.example .env
   cp frontend/.env.example frontend/.env
   cp backend/.env.example backend/.env
   ```
   Edit `.env` and `backend/.env` — replace `changeme` values with your own secrets.

3. Start infrastructure
   ```bash
   docker-compose up -d
   ```
   Verify: `docker ps` should show `ecotrack-mysql` and `ecotrack-mailhog` as `Up`.

4. Start the backend
   ```bash
   cd backend
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   Swagger UI: http://localhost:8080/swagger-ui.html

5. Start the frontend (new terminal)
   ```bash
   cd frontend
   ng serve
   ```
   App: http://localhost:4200

### Verify everything works

| URL | Expected |
|-----|----------|
| http://localhost:4200 | Angular app loads |
| http://localhost:8080/swagger-ui.html | Swagger UI with JWT auth |
| http://localhost:8025 | MailHog web UI (email inbox) |
| http://localhost:8080/actuator/health | `{"status":"UP"}` |
```

### Project Structure Notes

Files created or modified in this story:

```
ecotrack-office/            ← project root
├── docker-compose.yml      ← NEW
├── .env.example            ← NEW
├── .gitignore              ← UPDATE (add .env entries)
├── README.md               ← UPDATE (add setup section)
│
├── frontend/
│   ├── .env.example        ← NEW
│   ├── proxy.conf.json     ← NEW
│   └── angular.json        ← UPDATE (add proxyConfig)
│
└── backend/
    └── .env.example        ← NEW
```

> **Do NOT** add a `backend` or `frontend` service to `docker-compose.yml` — students run the app locally. Docker Compose is infrastructure only (MySQL + MailHog).

### Architecture Constraints

- **`UPLOAD_DIR`** env var must be defined even in Story 0.3 — it's used by the backend for incident photo storage (Story 4.1). Define it as `./uploads` for local dev.
- **MailHog** replaces a real SMTP server in dev. Spring Boot's `JavaMailSender` connects to `localhost:1025` — no auth required.
- **MySQL 8** — the `ecotrack` database must exist when Spring Boot starts (Flyway won't create the DB itself, only tables within it).
- **CORS handled by proxy** in local dev — `CORS_ALLOWED_ORIGINS` on the backend is still set to `http://localhost:4200` in case the proxy is bypassed.
- The `MYSQL_ROOT_PASSWORD` is only used by Docker internally — Spring Boot connects via `MYSQL_USER` / `MYSQL_PASSWORD`.

### Potential Pitfalls

1. **MySQL takes ~10s to initialize on first run** — if Spring Boot starts before MySQL is ready, the connection will fail. Run `docker-compose up -d` and wait for `docker logs ecotrack-mysql` to show `ready for connections` before starting Spring Boot.
2. **Port conflicts** — if port 3306 is already in use (local MySQL), change the host port: `"3307:3306"` in `docker-compose.yml` and update `DB_URL` accordingly.
3. **`flyway-mysql` dependency** — Story 0.2 must add `flyway-mysql` (not just `flyway-core`) for MySQL 8 compatibility. If this is missing, Flyway will fail silently or throw a `FlywayException`.
4. **`allowPublicKeyRetrieval=true`** in the JDBC URL is required for MySQL 8 with `useSSL=false` — without it, the driver throws `Public Key Retrieval is not allowed`.
5. **Named volume naming** — use `ecotrack_mysql_data` (with project prefix) to avoid collision with other projects using `mysql_data`.

### References

- Docker Compose decision: [Source: architecture.md#Infrastructure-Deployment]
- `UPLOAD_DIR` env var: [Source: architecture.md#Infrastructure-Deployment]
- `CORS_ALLOWED_ORIGINS`: [Source: architecture.md#Authentication-Security]
- `SMTP_HOST`/`SMTP_PORT`: [Source: architecture.md#Dependencies-Included (spring-boot-starter-mail)]
- Angular proxy: [Source: architecture.md#Frontend-Architecture]
- Story acceptance criteria: [Source: epics.md#Story-0.3]

## Dev Agent Record

### Agent Model Used

Claude Sonnet 4.6 (bmad-create-story workflow)

### Debug Log References

_None_

### Completion Notes List

_To be filled by the developer after implementation._

### File List

_To be filled by the developer: list every file created or modified._
