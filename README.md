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

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.5**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL** (Database)
- **Lombok** (Boilerplate reduction)
- **SpringDoc OpenAPI** (Swagger UI)
- **Maven** (Build tool)
- **Spring Security** (Authentication & Authorization)

## Prerequisites

- JDK 21 or higher
- Maven 3.x
- PostgreSQL database

## Getting Started

### 1. Database Configuration

Create a PostgreSQL database and configure the connection in `src/main/resources/application.properties`. You can use `src/main/resources/application.properties.example` as a template.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

### 2. Build the Project

```bash
./mvnw clean install
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The server will start at `http://localhost:8080`.

## API Endpoints

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
| Role  | Access |
|-------|--------|
| USER  | Own tasks only |
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
