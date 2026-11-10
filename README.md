# AI Job Application Tracker - Backend
Java 21, Spring Boot 3.5, Spring Security + JWT, JPA, MySQL, Groq LLM API.

1. Copy `.env.example` to `.env` (same folder as pom.xml) and fill values. Free Groq key: https://console.groq.com
2. `mvn spring-boot:run`
3. Swagger: http://localhost:8080/swagger-ui.html

Admin: `UPDATE users SET role='ADMIN' WHERE email='you@mail.com';` then login again.
