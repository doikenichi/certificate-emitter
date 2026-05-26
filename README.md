# Certificate Emitter

![Java](https://img.shields.io/badge/Java-25-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F)
![React](https://img.shields.io/badge/React-19.2.6-61DAFB)
![Build](https://img.shields.io/badge/build-TODO-lightgrey)
![License](https://img.shields.io/badge/license-%20%20GNU%20GPLv3%20-green?style=plastic)

Certificate Emitter is a Spring Boot and React web application for issuing course certificates from student answer sheets. A user provides the Google Drive file names for an answer spreadsheet, a DOCX certificate template, and an HTML email template. The backend downloads the files, scores each student against the configured quiz, generates certificates for approved students, converts the rendered DOCX files to PDF with Gotenberg, and emails the PDF certificates to the students.

## Key Features

- React/Vite UI for starting certificate issuance from three file-name inputs.
- JSON API endpoint for the certificate import and emission workflow.
- Google Drive integration for locating and downloading:
  - Google Sheets answer sheets exported as `.xlsx`.
  - `.docx` certificate templates.
  - HTML email templates.
- Answer-sheet parsing with Poiji.
- Quiz scoring against PostgreSQL-backed quiz/question data.
- Liquibase-managed database schema and seed data.
- Certificate rendering from DOCX placeholders.
- DOCX-to-PDF conversion through `gotenberg/gotenberg:8`.
- Email delivery through Spring Mail / JavaMail SMTP settings.
- Dockerfiles for backend and frontend, plus a root `docker-compose.yml`.
- Backend and frontend test suites with JaCoCo and Vitest coverage configuration.

## Application Workflow

1. The user opens the React frontend.
2. The user submits:
   - `formAnswersName`: Google Sheets answer sheet name.
   - `certificateTemplate`: Google Drive DOCX certificate template file name.
   - `emailTemplateName`: Google Drive HTML email template file name.
3. The frontend sends `POST /api/certificates/import`.
4. The backend downloads the answer sheet from Google Drive and exports it as `.xlsx`.
5. The backend reads rows with headers `Nome`, `Email`, and `Q1` through `Q10`.
6. Rows are validated and evaluated against the seeded quiz questions.
7. Answers, students, takes, responses, and certificate state are persisted.
8. Approved students receive generated certificates.
9. Certificate placeholders are replaced in the DOCX template:
   - `{{nome}}`
   - `{{%}}`
   - `{{data}}`
10. Gotenberg converts generated DOCX certificates to PDF.
11. Spring Mail sends each approved student an email with the PDF certificate attached.
12. The API returns a summary with row counts, certificate counts, and import/email errors.

## Architecture Overview

```text
React + Vite frontend
  -> POST /api/certificates/import
Spring Boot REST API
  -> Google Drive API: download answer sheet, certificate template, email template
  -> Poiji: parse answer spreadsheet
  -> PostgreSQL + Liquibase + Spring Data JPA: persist quiz, students, takes, responses, certificates
  -> DOCX renderer: replace certificate placeholders
  -> Gotenberg: convert DOCX to PDF
  -> JavaMail/Spring Mail: send certificate emails
```

The current backend exposes one certificate API controller:

```http
POST /api/certificates/import
```

OpenAPI UI is included through `springdoc-openapi-starter-webmvc-ui`. When the backend is running, verify the exact URL in your environment; Springdoc commonly serves Swagger UI at:

```text
http://localhost:8080/swagger-ui/index.html
```

## Technology Stack

| Area | Technology | Evidence |
| --- | --- | --- |
| Backend | Spring Boot `4.0.6` | `gradle.properties` |
| Language/runtime | Java `25` toolchain | `build.gradle` |
| Build | Gradle wrapper | `gradlew`, `build.gradle` |
| API docs | Springdoc OpenAPI `3.0.3` | `build.gradle` |
| Persistence | Spring Data JPA, PostgreSQL driver `42.7.11` | `build.gradle` |
| Migrations | Liquibase | `build.gradle`, `src/main/resources/db/changelog` |
| Email | Spring Boot Mail / JavaMail | `build.gradle`, `EmailSenderService` |
| Google APIs | Google Drive and Gmail API clients | `build.gradle`, `google/` |
| Spreadsheet parsing | Poiji `5.4.0` | `build.gradle`, `PoijiAnswerSheetReader` |
| Document conversion | Gotenberg `8` | `docker-compose.yml` |
| Frontend | React `19.2.6`, Vite `8.0.12`, TypeScript `6.0.2` | `frontend/package.json` |
| Frontend tests | Vitest `4.1.7`, Testing Library, jsdom | `frontend/package.json` |
| Containerization | Docker, Docker Compose, Nginx | `Dockerfile`, `frontend/Dockerfile`, `docker-compose.yml` |

## Repository Structure

```text
certificate-emitter/
  src/
    main/
      java/com/br/shizen/certificateemitter/
        config/                 Spring configuration for mail and Gotenberg
        dto/                    API and service DTOs
        entity/                 JPA entities
        google/                 Google Drive/Gmail client helpers
        microsoft/              Office-related helpers
        repository/             Spring Data repositories
        services/               Certificate, answer sheet, email, and temp path services
        web/rest/controller/    REST controllers and API exception handling
      resources/
        db/changelog/           Liquibase migrations and seed data
        static/                 Legacy/static assets
        templates/              Legacy server-rendered templates
    test/                       Backend tests
  frontend/
    src/
      controllers/              React state/controller hooks
      models/                   Frontend data types/defaults
      services/                 API client
      views/                    React views
      test/                     Vitest setup
    Dockerfile                  Frontend build and Nginx runtime image
    nginx.conf                  Nginx SPA/API proxy configuration
  docs/                         Engineering notes and migration docs
  Dockerfile                    Backend build/runtime image
  docker-compose.yml            PostgreSQL, Gotenberg, backend, frontend
  build.gradle                  Backend build definition
  gradle.properties             Version properties
```

Generated folders such as `build/`, `.gradle/`, `frontend/dist/`, `frontend/node_modules/`, `frontend/coverage/`, and `tmp/` are not required for normal source review.

## Prerequisites

Install these tools for local development:

| Tool | Version |
| --- | --- |
| Java JDK | 25 |
| Gradle | Use the included wrapper |
| Node.js | Compatible with the frontend toolchain; Docker uses Node `24-alpine` |
| npm | Comes with Node.js |
| Docker / Docker Compose | Required for the compose workflow |
| PostgreSQL | Optional locally; compose provides PostgreSQL `17.10-alpine` |
| Gotenberg | Optional locally; compose provides `gotenberg/gotenberg:8` |
| Google Cloud OAuth credentials | Required for Google Drive access |
| Gmail or SMTP-compatible account | Required for email delivery |

## Environment Variables

The committed `.env.example` contains the required application variables:

```dotenv
DATABASE_NAME=
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=
MAIL_HOST=
MAIL_PORT=
MAIL_USERNAME=
MAIL_PASSWORD=
```

`docker-compose.yml` also passes these runtime values to the backend:

```dotenv
SPRING_PROFILES_ACTIVE=dev
LOCAL_TEMP=/app/tmp/
GOOGLE_CREDENTIALS_PATH=/credentials.json
GOTENBERG_BASE_URL=http://gotenberg:3000
```

For local development, create a root `.env` file:

```powershell
Copy-Item .env.example .env
```

Example local values:

```dotenv
DATABASE_NAME=certificate_emitter_dev
DATABASE_URL=jdbc:postgresql://localhost:5432/certificate_emitter_dev
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-account@gmail.com
MAIL_PASSWORD=your-gmail-app-password
GOTENBERG_BASE_URL=http://localhost:3000
```

Security note: `.gitignore` excludes `.env`, `.env.*`, and Google credential files under `src/main/resources`. If real credentials have ever been committed or shared, rotate them before using the project again.

## Local Development Setup

### 1. Configure environment

Create `.env` from `.env.example` and fill in PostgreSQL and mail settings.

The active Spring profile defaults to `dev` in `src/main/resources/application.yaml`, and `application-dev.yaml` imports `.env.dev` if it exists. For a local setup, prefer using your own `.env` or untracked profile-specific file rather than committing credentials.

### 2. Start infrastructure

Start PostgreSQL and Gotenberg with Docker Compose:

```powershell
docker compose up postgres gotenberg
```

PostgreSQL is exposed on:

```text
localhost:5432
```

Gotenberg is exposed on:

```text
http://localhost:3000
```

### 3. Run the backend

```powershell
.\gradlew.bat bootRun
```

Backend URL:

```text
http://localhost:8080
```

### 4. Run the frontend

```powershell
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

Vite proxies `/api` requests to `http://localhost:8080`.

## Running With Docker Compose

Create `.env` first:

```powershell
Copy-Item .env.example .env
```

Then start the full stack:

```powershell
docker compose up --build
```

Services:

| Service | Container role | Host port |
| --- | --- | --- |
| `postgres` | PostgreSQL database | `5432` |
| `gotenberg` | DOCX-to-PDF conversion | `3000` |
| `backend` | Spring Boot API | `8080` |
| `frontend` | Nginx-served React app | `5173` -> container `80` |

Open:

```text
http://localhost:5173
```

Needs verification: Google OAuth credentials in the current code are loaded as a classpath resource through `getResourceAsStream(...)`. The compose file sets `GOOGLE_CREDENTIALS_PATH=/credentials.json`, but it does not mount a credentials file. For containerized Google API access, verify whether credentials should be packaged into the backend image, mounted and loaded from the filesystem, or refactored to support external secret mounts.

## Backend Setup

Install dependencies and run tests/builds through the Gradle wrapper:

```powershell
.\gradlew.bat test
.\gradlew.bat bootJar
```

Run the application:

```powershell
.\gradlew.bat bootRun
```

The backend uses:

- `src/main/resources/application.yaml` for default profile activation.
- `src/main/resources/application-dev.yaml` for datasource, mail, Liquibase, local temp, Gotenberg, and server settings.
- `src/test/resources/application.yaml` for test configuration.

Default backend port:

```text
8080
```

## Frontend Setup

Install dependencies:

```powershell
cd frontend
npm install
```

Run the Vite development server:

```powershell
npm run dev
```

Build static assets:

```powershell
npm run build
```

Preview the production build:

```powershell
npm run preview
```

Lint:

```powershell
npm run lint
```

The Docker frontend image builds the React app with Node `24-alpine` and serves `dist/` through Nginx `1.29-alpine`. `frontend/nginx.conf` proxies `/api/` to `http://backend:8080/api/` inside Docker Compose.

## Database Setup And Migrations

The application uses PostgreSQL and Liquibase.

Liquibase is enabled in `application-dev.yaml`:

```yaml
spring:
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
```

Migration files:

```text
src/main/resources/db/changelog/db.changelog-master.yaml
src/main/resources/db/changelog/changes/001-create-sequences.yaml
src/main/resources/db/changelog/changes/002-create-core-tables.yaml
src/main/resources/db/changelog/changes/003-create-association-tables.yaml
src/main/resources/db/changelog/changes/004-insert-initial-quiz-questions.yaml
```

The master changelog currently includes migrations `001` through `004`. `005-repair-certificate-schema.yaml` exists but is commented out.

The seed migration creates one quiz named `final certificate exam` and 10 Shiatsu-related questions. The evaluator currently expects exactly one quiz in the database and maps spreadsheet columns `Q1` through `Q10` to the first 10 questions returned for that quiz.

## Google Drive API Setup

The backend uses Google OAuth client credentials to access Drive and Gmail scopes:

```text
DriveScopes.DRIVE
GmailScopes.GMAIL_SEND
```

Credential behavior found in the code:

- Default credential resource path: `/credentials.json`.
- Token storage directory: `tokens`.
- OAuth local receiver port: `8888`.
- Google Drive is initialized on startup.
- `GoogleDrive.initialize()` currently calls `files().emptyTrash()`.

Setup outline:

1. Create an OAuth client in Google Cloud Console.
2. Enable Google Drive API.
3. Enable Gmail API if using the Gmail API helper.
4. Download the OAuth client JSON.
5. Provide it as `credentials.json` in the location expected by the app.
6. Run the app locally once and complete the OAuth browser authorization flow.
7. Preserve the generated `tokens/` directory for subsequent local runs.

Needs verification: the current Spring property is named `google.credentialsPath`, and the implementation reads it as a classpath resource. For production or Docker, prefer a secret mount and update the code if filesystem-based credential loading is required.

## Gmail / JavaMail Setup

The active email delivery path uses `EmailSenderService` and Spring Mail:

```yaml
spring:
  mail:
    host: ${MAIL_HOST}
    port: ${MAIL_PORT}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

For Gmail SMTP, use:

```dotenv
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-account@gmail.com
MAIL_PASSWORD=your-gmail-app-password
```

The email subject is currently:

```text
Your certificate is ready!
```

The email HTML template content is loaded from Google Drive and `{{nome}}` is replaced with the student name before sending.

## Gotenberg Setup

Gotenberg is used by `GotenbergDocumentToPdfConverter` through Spring's `RestClient`.

Default local URL:

```text
http://localhost:3000
```

Docker Compose service:

```yaml
gotenberg:
  image: gotenberg/gotenberg:8
  command:
    - gotenberg
    - --chromium-ignore-certificate-errors
  ports:
    - "3000:3000"
```

Conversion endpoint used by the backend:

```text
/forms/libreoffice/convert
```

## Certificate Generation Flow

Certificate generation is coordinated by `CertificateService`.

For each approved student:

1. Persist a certificate record with `issued=false`.
2. Copy the DOCX template to a student-specific DOCX file.
3. Replace placeholders in `word/document.xml`.
4. Convert the DOCX file to PDF with Gotenberg.
5. Persist the certificate record with `issued=true`.
6. Return the rendered file paths in `CertificateIssueStatus`.

Current certificate template placeholders:

| Placeholder | Replacement |
| --- | --- |
| `{{nome}}` | Uppercase student name |
| `{{%}}` | Rounded score percentage |
| `{{data}}` | Current date formatted as `dd/MM/yyyy` |

Only students whose score is greater than or equal to `evaluation.minApproval` receive certificates. The current development configuration sets:

```yaml
evaluation:
  minApproval: 75
```

## Email Delivery Flow

Email delivery is coordinated by `EmailSenderService`.

For each issued certificate:

1. Skip unissued certificate statuses.
2. Choose the PDF certificate when available; otherwise use the rendered certificate file.
3. Skip students with missing email addresses or missing attachments.
4. Replace `{{nome}}` in the HTML email template.
5. Send a multipart HTML email with the certificate attached.
6. Return counts for attempted, sent, skipped, and errored emails.

The API response merges email errors into the final `errors` list.

## API Documentation

### Start Certificate Emission

```http
POST /api/certificates/import
Content-Type: application/json
```

Request body:

```json
{
  "formAnswersName": "respostas_alunos",
  "certificateTemplate": "modelo.docx",
  "emailTemplateName": "template_email.html"
}
```

Response body:

```json
{
  "rowsRead": 1,
  "rowsImported": 1,
  "rowsSkipped": 0,
  "studentsCreated": 0,
  "takesCreated": 0,
  "responsesCreated": 10,
  "responsesUpdated": 0,
  "certificatesGenerated": 1,
  "errors": []
}
```

Error response shape from the API exception handler:

```json
{
  "status": "error",
  "message": "Request failed message"
}
```

Validation uses `@NotBlank` on the request fields. Needs verification: the current exception handler explicitly handles `IOException`, `UncheckedIOException`, and `IllegalStateException`; validation error response formatting should be confirmed for blank request fields.

## Testing

### Backend Tests

Run:

```powershell
.\gradlew.bat test
```

Generate JaCoCo reports:

```powershell
.\gradlew.bat jacocoTestReport
```

Report locations:

```text
build/jacocoHtml/
build/customJacocoReportDir/
```

Coverage verification is configured with a minimum of `0.5`:

```powershell
.\gradlew.bat jacocoTestCoverageVerification
```

### Frontend Tests

Run:

```powershell
cd frontend
npm test
```

Run coverage:

```powershell
npm run test:coverage
```

Vitest coverage thresholds:

| Metric | Threshold |
| --- | --- |
| Lines | 80% |
| Functions | 80% |
| Branches | 70% |
| Statements | 80% |

Coverage output:

```text
frontend/coverage/
```

## Build Instructions

Build backend JAR:

```powershell
.\gradlew.bat bootJar
```

Build frontend:

```powershell
cd frontend
npm run build
```

Build Docker images through Compose:

```powershell
docker compose build
```

Build and run all services:

```powershell
docker compose up --build
```

## Deployment Notes

This repository contains container definitions, but production deployment needs additional verification.

Before deploying:

- Provide PostgreSQL credentials through a secret manager or deployment environment, not committed files.
- Provide Google OAuth credentials and token storage securely.
- Verify Google OAuth works in a non-interactive/container environment.
- Verify SMTP credentials and sender identity.
- Decide whether `tokens/` should be mounted as persistent storage.
- Mount or provision a writable temp directory for generated documents.
- Review `GoogleDrive.initialize()`, which currently empties Google Drive trash during startup.
- Confirm whether the commented Liquibase migration `005-repair-certificate-schema.yaml` is required.
- Configure HTTPS and reverse proxy settings outside this repository.
- Add CI build/test status if publishing on GitHub.

Do not assume the current compose file is production-ready without addressing the items above.

## Troubleshooting

| Problem | What to check |
| --- | --- |
| Frontend cannot call API | Confirm backend is running on `localhost:8080`; Vite proxies `/api` to that port. |
| Docker frontend cannot call backend | Confirm both services are in Compose and `frontend/nginx.conf` proxies to `http://backend:8080/api/`. |
| Backend cannot connect to PostgreSQL | Check `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, and whether PostgreSQL is listening on `5432`. |
| Liquibase validation or schema errors | Check included changelogs and whether migration `005` is intentionally disabled. |
| Google credentials not found | Confirm `credentials.json` is available at the classpath path configured by `google.credentialsPath`. |
| OAuth browser flow fails | Confirm local port `8888` is available and the OAuth client supports the redirect URI used by `LocalServerReceiver`. |
| Gotenberg conversion fails | Confirm Gotenberg is running at `GOTENBERG_BASE_URL` and the `/forms/libreoffice/convert` endpoint is reachable. |
| Emails are not sent | Check SMTP host, port, username, password/app password, TLS settings, and provider account restrictions. |
| No certificates generated | Confirm student scores meet `evaluation.minApproval` and the answer sheet has headers `Nome`, `Email`, `Q1` through `Q10`. |
| Template placeholders remain unchanged | Confirm the DOCX template contains `{{nome}}`, `{{%}}`, and `{{data}}` in `word/document.xml` text nodes. |

## Security And Secrets Management

- Do not commit `.env`, `.env.*`, Google credentials, Gmail app passwords, OAuth tokens, generated certificates, or student answer sheets.
- Treat answer sheets and certificates as personally identifiable information.
- Rotate any credentials that were committed, shared, or used in test files.
- Store production secrets in a secret manager or deployment platform variables.
- Review generated PDFs and temporary DOCX files before deciding how long they should be retained.
- Keep `tokens/` out of source control.
- Review Google API scopes before production use; the current Drive scope is broad.

## Contributing

1. Create a focused branch.
2. Keep credentials and generated files out of commits.
3. Run backend tests:

   ```powershell
   .\gradlew.bat test
   ```

4. Run frontend tests and lint:

   ```powershell
   cd frontend
   npm test
   npm run lint
   ```

5. Update this README when changing setup, environment variables, ports, workflows, or external integrations.
6. Prefer small pull requests with clear behavior changes and test coverage for service logic.

## License

GNU Affero General Public License v3.0
