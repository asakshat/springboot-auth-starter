# Spring Boot Boilerplate with JWT Auth, Refresh Tokens, Email Verification & Password Reset

A spring boot starter project I made so that I don't have to focus much on the authentication side when building other side projects.
---

##  Features

- ✅ Spring Boot 3.x
- ✅ JWT Authentication (Access + Refresh Tokens)
- ✅ Email Verification via Token
- ✅ Forgot Password & Reset Functionality
- ✅ Role-Based Access Control (RBAC)
- ✅ PostgreSQL integration
- ✅ Secure password hashing (BCrypt)
- ✅ Token Expiry & Revocation Handling

---

##  Project Structure

```bash
src/
├── main/
│   ├── java/com/example/auth/
│   │   ├── config/             # Security, JWT, and Mail configs
│   │   ├── controller/         # Auth and user endpoints
│   │   ├── dto/                # Request/response objects
│   │   ├── entity/             # JPA Entities (User, Token, etc.)
│   │   ├── event/              # Email events and listeners
│   │   ├── exception/          # Custom exceptions & handlers
│   │   ├── repository/         # JPA Repositories
│   │   ├── security/           # JWT utils, filters, and providers
│   │   ├── service/            # Business logic
│   │   └── AuthApplication.java
│   └── resources/
│       └── application.properties     # Configuration

```
## .env File example

```
# JWT Configuration
JWT_SECRET_KEY=nqasdasdMasdac/O0W+usEWfL4sdadsasdtQc5Vfw=

# Email Configuration
EMAIL=test@gmail.com
APP_PASSWORD=basdasdadfasd

# Database Configuration
JDBC_URL=jdbc:postgresql://localhost:5432/postgres_db
JDBC_USER=postgres
JDBC_PASSWORD=postgres

```



### ToDos:

- [ ] Integrate Oauth i.e github, gmail ,etc
- [ ] Refactor for scalability
- [ ] Clean up code

