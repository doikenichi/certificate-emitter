Act as a senior technical writer, senior full-stack engineer, DevOps engineer, and open-source maintainer.

Your task is to review my project repository and create a world-class `README.md` file.

Project name:
Certificate Emitter

Project purpose:
Certificate Emitter is a web application that intakes a student answer sheet, certificate model, and email body template. It scores the answer sheet and, if the student is approved, issues and sends the certificate by email to the address registered in the student profile.

Technology stack:
- Backend: Spring Boot
- Frontend: React with Vite
- Build tool: Gradle
- Database: PostgreSQL
- Email: Gmail via JavaMail
- File storage/integration: Google Drive API
- Document conversion: gotenberg/gotenberg:8 for DOCX-to-PDF conversion
- Document manipulation: Apache POI / POI-related library for Word document handling
- Containerization:
    - Backend Dockerfile at project root
    - Frontend Dockerfile under `frontend/`
    - `docker-compose.yml` at project root

Known repository structure:
- `.codex/`
- `.gradle/`
- `.idea/`
- `build/`
- `docs/`
- `frontend/`
    - `coverage/`
    - `dist/`
    - `node_modules/`
    - `public/`
    - `src/`
    - `.dockerignore`
    - `.gitignore`
    - `Dockerfile`
    - `eslint.config.js`
    - `index.html`
    - `nginx.conf`
    - `package.json`
    - `package-lock.json`
    - `README.md`
    - `tsconfig.app.json`
    - `tsconfig.json`
    - `tsconfig.node.json`
    - `vite.config.ts`
    - `vitest.config.ts`
- `gradle/`
- `openspec/`
- `src/`
- `tmp/`
- `tokens/`
- `.dockerignore`
- `.env.dev`
- `.env.example`
- `.env.prod`
- `.env.test`
- `.gitattributes`
- `.gitignore`
- `build.gradle`
- `docker-compose.yml`
- `Dockerfile`
- `gradle.properties`
- `gradlew`
- `gradlew.bat`
- `HELP.md`
- `settings.gradle`

Before writing the README:
1. Review the repository files and infer the real project behavior from evidence.
2. Inspect at minimum:
    - `build.gradle`
    - `settings.gradle`
    - `docker-compose.yml`
    - root `Dockerfile`
    - `frontend/Dockerfile`
    - `frontend/package.json`
    - `frontend/vite.config.ts`
    - `frontend/vitest.config.ts`
    - `frontend/nginx.conf`
    - `.env.example`
    - `.env.dev`
    - `.env.test`
    - `.env.prod`
    - relevant backend files under `src/`
    - relevant frontend files under `frontend/src/`
    - files under `docs/`, `scripts/`, and `openspec/` if they clarify usage
3. Do not invent features, commands, environment variables, ports, APIs, credentials, or architecture details.
4. If something is not clear from the repository, mark it as `TODO:` or `Needs verification:` instead of guessing.
5. If there is conflicting information, explain the conflict and choose the safest wording.
6. Exclude generated/build folders from analysis unless needed:
    - `build/`
    - `.gradle/`
    - `frontend/dist/`
    - `frontend/node_modules/`
    - `frontend/coverage/`
    - `tmp/`

Create a professional `README.md` suitable for:
- GitHub
- recruiters/employers
- developers onboarding to the project
- future maintainers

The README should include, when applicable:

1. Project title
2. Badges/placeholders for:
    - Java version
    - Spring Boot version
    - React version
    - build status
    - license
3. Executive summary
4. Key features
5. Business workflow / application workflow
6. Architecture overview
7. Technology stack table
8. Repository structure
9. Prerequisites
10. Environment variables
11. Local development setup
12. Running with Docker Compose
13. Backend setup
14. Frontend setup
15. Database setup and migrations, if present
16. Google Drive API setup
17. Gmail / JavaMail setup
18. Gotenberg setup
19. Certificate generation flow
20. Email delivery flow
21. Testing instructions
    - backend tests
    - frontend tests
    - coverage, if configured
22. Build instructions
23. Deployment notes
24. API documentation section
    - mention OpenAPI/Swagger only if actually present
25. Troubleshooting section
26. Security and secrets management notes
27. Known limitations / TODOs
28. Contribution guidelines
29. License section

Style requirements:
- Use clear, professional, technically precise language.
- Do not over-market the project.
- Make it easy for another developer to run the project locally.
- Prefer copy-pasteable commands.
- Use Markdown tables where useful.
- Use fenced code blocks for commands, environment examples, and directory structure.
- Keep the README practical and maintainable.
- Avoid vague claims like “highly scalable” unless the repository proves it.
- Avoid saying the project is production-ready unless the repository clearly supports that.
- Use placeholders only when required, clearly marked as `TODO`.

Important:
- The final output must be a complete replacement for the root `README.md`.
- Do not include analysis notes before or after the README.
- Output only the final Markdown content.