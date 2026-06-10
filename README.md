# Task Manager API

A Spring Boot-based RESTful API for managing tasks and users. This project provides a backend for a task management application, allowing users to create, update, and track their tasks.

## Features

- **User Management**: Create, read, update, and delete users.
- **Task Management**: Full CRUD operations for tasks.
- **Task Assignment**: Tasks are associated with users.
- **Task Statuses**: Track task progress (e.g., TODO, IN_PROGRESS, DONE).
- **API Documentation**: Interactive API documentation using Swagger/OpenAPI.
- **Global Exception Handling**: Centralized error management for consistent API responses.
- **Authentication**: Form-based login with session management
- **Authorization**: Role-based access control (USER and ADMIN)
- **Password Security**: BCrypt password hashing
- **Profiles**: Separate `dev` (H2 in-memory + seed data) and `prod` (PostgreSQL) configurations
- **Externalized Configuration**: Validated `app.settings.`* properties exposed via a metadata endpoint
- **Internationalization (i18n)**: Localized error/validation messages (English and Spanish)
- **Structured Logging**: Profile-driven Log4j2 logging to console and a rolling file

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.5**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL** (prod database) / **H2** (dev in-memory database)
- **Lombok** (Boilerplate reduction)
- **SpringDoc OpenAPI** (Swagger UI)
- **Maven** (Build tool)
- **Spring Security** (Authentication & Authorization)
- **Bean Validation** (Hibernate Validator, wired to the message bundles)
- **Log4j2** (Logging)

## Prerequisites

- JDK 21 or higher
- Maven 3.x
- PostgreSQL database (only required for the `prod` profile; the `dev` profile uses an in-memory H2 database)

## Getting Started

### 1. Build the Project

```bash
./mvnw clean install
```

### 2. Run the Application

The application defaults to the `dev` profile (in-memory H2 with seed data), so it runs out of the box with no database setup:

```bash
./mvnw spring-boot:run
```

The server will start at `http://localhost:8080`.

See [Profiles](#profiles) below for running against PostgreSQL with the `prod` profile.

## Profiles

The default active profile is `dev` (set via `spring.profiles.active=dev` in `application.properties`).


| Profile | Database                            | Schema                                | Seed data            | Logging config           |
| ------- | ----------------------------------- | ------------------------------------- | -------------------- | ------------------------ |
| `dev`   | H2 in-memory (`jdbc:h2:mem:taskdb`) | `create-drop` (rebuilt on every boot) | `data.sql` is loaded | `log4j2-dev.xml` (DEBUG) |
| `prod`  | PostgreSQL (`week7-lab`)            | `validate` (never auto-mutated)       | none                 | `log4j2-prod.xml` (WARN) |


In the `dev` profile the H2 web console is available at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:taskdb`, user `sa`, empty password).

### Selecting a profile

From the CLI with Maven:

```bash
# Dev profile (default)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Prod profile (requires a running PostgreSQL instance)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

On a built JAR or from an IDE run configuration, use the standard Spring property:

```bash
java -jar target/*.jar --spring.profiles.active=prod
# or as a JVM/system property
java -Dspring.profiles.active=prod -jar target/*.jar
```

For the `prod` profile, configure the PostgreSQL connection in `src/main/resources/application-prod.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Seed data (dev profile)

The `dev` profile loads `src/main/resources/data.sql`, which creates three users (`admin`, `alice`, `bob`) and a handful of sample tasks. All seed users share the password `password` (stored as a BCrypt hash).

## Custom Properties

Application metadata is externalized under the `app.settings.*` prefix and bound to the validated `AppSettings` configuration class. These can be overridden per profile.


| Property                            | Type    | Validation  | Role                                                       |
| ----------------------------------- | ------- | ----------- | ---------------------------------------------------------- |
| `app.settings.title`                | String  | `@NotBlank` | Human-readable API title returned by the metadata endpoint |
| `app.settings.pagination-limit`     | int     | `@Min(1)`   | Default maximum number of items returned per page          |
| `app.settings.contact-email`        | String  | `@Email`    | Contact email surfaced in API metadata                     |
| `app.settings.registration-enabled` | boolean | —           | Feature flag that gates new user registration              |


These values are exposed through the metadata endpoint:

```bash
curl http://localhost:8080/api/info
```

```json
{
  "title": "...",
  "contactEmail": "...",
  "paginationLimit": 20,
  "registrationEnabled": true
}
```

## Internationalization (i18n)

Error and validation messages are localized via message bundles in `src/main/resources`:

- `messages.properties` — default fallback
- `messages_en.properties` — English
- `messages_es.properties` — Spanish

Locale is resolved from the `Accept-Language` request header (`AcceptHeaderLocaleResolver`), with English as the default and `en`/`es` supported. Localized output covers:

- API error payloads (e.g. user/task not found, duplicate user, registration disabled) raised through the global exception handler.
- Bean Validation messages on the request DTOs (e.g. username size, invalid email), which are wired to the same bundles.

### Testing localization

Trigger a localized error by requesting a non-existent resource with different `Accept-Language` headers:

```bash
# English
curl -H "Accept-Language: en" http://localhost:8080/api/tasks/9999

# Spanish
curl -H "Accept-Language: es" http://localhost:8080/api/tasks/9999
```

You can likewise observe localized validation messages by posting an invalid body (e.g. a too-short username or malformed email) to `POST /api/users` with the desired `Accept-Language` header.

## Logging

Logging is configured with Log4j2 and is profile-driven (the active config is selected by `logging.config` in each profile's properties file):

- `dev` → `log4j2-dev.xml` at `INFO` level.
- `prod` → `log4j2-prod.xml` at `WARN` level.

Both profiles write to the console and to a rolling file at `**logs/app.log**` (relative to the working directory). The rolling policy rolls the file daily and whenever it reaches 10MB, keeping at most 30 archived files (compressed as `logs/app-<date>-<index>.log.gz`).

## API Endpoints

### Metadata

- `GET /` - Greeting message
- `GET /api/info` - API metadata sourced from `app.settings.*` configuration

### Users

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update user details
- `DELETE /api/users/{id}` - Delete a user

### Tasks

- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update task details
- `DELETE /api/tasks/{id}` - Delete a task

## Security

Authentication is handled via form-based login with server-side sessions.

- **Login**: `POST /login` (username + password)
- **Logout**: `POST /logout` (invalidates session)
- **Session timeout**: 30 minutes of inactivity

### Roles


| Role  | Access               |
| ----- | -------------------- |
| USER  | Own tasks only       |
| ADMIN | All tasks, all users |


> All users register with the USER role by default.
> ADMIN role must be assigned manually via the database

> Note: CSRF is disabled — this is a REST API with no browser forms.

## API Documentation

Once the application is running, you can access the interactive Swagger UI at:
`http://localhost:8080/swagger-ui.html`

The OpenAPI specification is available at:
`http://localhost:8080/v3/api-docs`

## Project Structure

- `src/main/java/org/example/todoapi/controller`: REST controllers
- `src/main/java/org/example/todoapi/service`: Business logic layer
- `src/main/java/org/example/todoapi/repository`: Data access layer (JPA)
- `src/main/java/org/example/todoapi/entity`: JPA entities
- `src/main/java/org/example/todoapi/dto`: Data Transfer Objects
- `src/main/java/org/example/todoapi/exception`: Custom exceptions and global handler
- `src/main/java/org/example/todoapi/config`: Configuration classes (e.g., OpenAPI)

