# JobTrackr: AI Job Application Tracker (Backend)

A full-stack job application tracker that helps job seekers organise applications, follow every stage of the hiring pipeline and use an LLM to compare their resume with a job description, draft cover letters, prepare for interviews and tailor their resume.

> Applications are submitted on the original company or job site. JobTrackr is the personal assistant around the process: tracking, reminders and AI help.

**Frontend repo:** https://github.com/sonu-chawda/job-tracker-frontend


## Features

**For users**
- Register and login with JWT authentication
- Add, edit and delete job applications (company, role, location, salary, job link, notes, job description)
- Status pipeline: Wishlist, Applied, Online Test, Interview, Offer, Rejected
- Automatic status history for every change
- Pagination, sorting and status filter
- Dashboard stats (totals per status)
- Save a base resume and use it for all AI features

**AI features (Groq LLM API)**
- Resume vs job description match score with matched and missing skills
- Cover letter draft
- Interview questions based on the resume and the role
- Follow-up email draft
- Resume tailoring for a specific job

**For admins**
- List all users, block or unblock accounts
- Platform stats (total users and applications)

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 (Web, Data JPA, Security, Validation) |
| Auth | Spring Security, JWT (jjwt), BCrypt, role based access (USER, ADMIN) |
| Database | MySQL 8, Hibernate |
| AI | Groq API (OpenAI compatible chat completions), default model `llama-3.3-70b-versatile` |
| Docs | Swagger UI (springdoc-openapi) |
| Build | Maven |

## Architecture

```
React  ->  JwtAuthFilter  ->  Controller  ->  Service  ->  Repository  ->  MySQL
                                                  |
                                                  +-> AiClient -> Groq API
```

```
src/main/java/com/jobtracker
  controller/   REST endpoints (Auth, Application, Ai, Resume, Admin)
  service/      Business logic (AuthService, ApplicationService, AiService, AiClient)
  repository/   Spring Data JPA repositories
  model/        Entities and enums (User, JobApplication, StatusHistory, Resume)
  dto/          Request and response records
  security/     JwtService, JwtAuthFilter, SecurityConfig
  exception/    Global exception handling
```

## Design decisions

- **Ownership check at query level.** Every application lookup uses `findByIdAndUserId`, so a user can never read or change another user's data.
- **Transactions.** Status change and status history are saved together in one `@Transactional` method.
- **DTOs.** Entities are never exposed, so fields like the password hash never leave the server.
- **Centralised errors.** `@RestControllerAdvice` returns consistent JSON errors with the right status codes (400, 403, 404, 429, 502, 504).
- **AI safety.** The prompts instruct the model to use only facts from the resume and never invent skills. The LLM client has timeouts, handles rate limits (429) and retries once if the model returns invalid JSON.
- **Secrets stay out of code.** Database password, JWT secret and API key come from environment variables.

## API overview

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Create account, returns JWT |
| POST | `/api/auth/login` | Login, returns JWT |
| GET | `/api/applications` | List (params: `page`, `size`, `sortBy`, `dir`, `status`) |
| POST | `/api/applications` | Create application |
| GET / PUT / DELETE | `/api/applications/{id}` | Read, update, delete |
| PATCH | `/api/applications/{id}/status` | Change status (saves history) |
| GET | `/api/applications/{id}/history` | Status history |
| GET | `/api/applications/stats` | Dashboard counts |
| GET / PUT | `/api/resume` | Read or save base resume |
| POST | `/api/ai/{id}/analyze` | Match score and skill gaps |
| POST | `/api/ai/{id}/cover-letter` | Cover letter draft |
| POST | `/api/ai/{id}/interview-questions` | Interview questions |
| POST | `/api/ai/{id}/follow-up` | Follow-up email draft |
| POST | `/api/ai/{id}/tailor-resume` | Tailored resume |
| GET | `/api/admin/users` | List users (ADMIN) |
| PATCH | `/api/admin/users/{id}/toggle-active` | Block or unblock (ADMIN) |
| GET | `/api/admin/stats` | Platform stats (ADMIN) |

All endpoints except `/api/auth/**` need the header `Authorization: Bearer <token>`. Full interactive docs: `http://localhost:8080/swagger-ui.html`.

## Getting started

**Prerequisites:** Java 21, Maven, MySQL 8, a free Groq API key from https://console.groq.com

1. Clone the repo
   ```bash
   git clone https://github.com/sonu-chawda/job-tracker-backend.git
   cd job-tracker-backend
   ```
2. Create your environment file
   ```bash
   cp .env.example .env
   ```
   Fill in the values:
   ```
   DB_USER=root
   DB_PASSWORD=your_mysql_password
   JWT_SECRET=a-long-random-secret-of-at-least-32-characters
   GROQ_API_KEY=your_groq_key
   ```
   Optional: `GROQ_MODEL` to use a different model.
3. Run
   ```bash
   mvn spring-boot:run
   ```
   The database and tables are created automatically. The API runs on `http://localhost:8080`.
4. To create an admin, register a user and run:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'you@example.com';
   ```
   Then login again to get a new token.

## AI usage notes

The free Groq tier has per minute and per day limits. If you hit them the API returns a clear "rate limit reached" message; wait a minute and retry. Limits and available models change over time, so check the Groq console if a model name stops working.

## Roadmap

- Unit tests with JUnit 5 and Mockito
- Save AI results to the database
- Per user daily AI limit
- Resume PDF upload and tailored resume download
- Docker support and deployment
- Password change, forgot password and Google login

## Author

**Sonu Chawda**, Java Full Stack Developer
LinkedIn: https://www.linkedin.com/in/sonuchawda

