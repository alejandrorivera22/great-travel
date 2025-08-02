#  great-travel

This README is also available in [Spanish ES](./README.es.md)

**great-travel**  is a backend API designed for a travel platform that 
allows users to book flights, hotels, and tours. It includes features 
like user authentication, flight and hotel search, booking reservations, 
and managing tours, among others. Built with Spring Boot, the API also 
includes JWT-based authentication, caching with Redis, and comprehensive
API documentation with Swagger.
---
## Technologies Used

| Tool                  | Purpose                               |
|-----------------------|---------------------------------------|
| Java 17               | Main programming language             |
| Spring Boot 3         | Backend framework                     |
| Spring Web            | REST API exposure                     |
| Spring Data JPA       | Persistence with Hibernate            |
| Spring Security + JWT | Authentication & authorization      |
| Redis + Redisson      | Product caching                       |
| PostgreSQL            | Relational database                   |
| H2                    | In-memory database for testing        |
| Swagger (OpenAPI)     | Interactive API documentation         |
| Docker Compose        | Containers for MySQL and Redis        |
| JUnit, Mockito        | Unit & integration testing            |
| Lombok                | Boilerplate code reduction            |

---

## ✅ Features

- `JWT-based authentication` (sign up and login)
- `User management` (create, paginate, update, assign roles)
- `Flight management` (search, filter by price, origin/destination, etc.)
- `Hotel management` (search, filter by price, rating, etc.)
- `Reservation management` create, update, delete reservations
- `Tour management`  (create tours from flights and hotels)
- `Redis cache` to improve performance
- `Role-based access control`: (Admin, Customer)
-  `Swagger API Documentation` for easy testing

---
## Project Structure
- `api/` — API controllers, route definitions and DTOs
- `config/` — General configurations (Swagger, security, etc.)
- `domain/` — Entities and repositories for database interaction
- `infrastructure/` — Interfaces and business logic
- `resources/` — Configuration files
- `util/` — Utility classes (roles, custom exceptions, etc.)
- `test/` — Unit tests

### Architecture
The project follows a layered architecture inspired by the principles of
Clean Architecture. Responsibilities are clearly separated between the
controllers (API), services (business logic),
domain (entities and repositories), and configuration layers.
This structure enhances maintainability and scalability,
while also making unit testing much easier to implement.

---

##  Local Installation

### 1. Prerequisites

- Java 17 installed
- Docker and Docker Compose
- Maven

### 2. Clone the repository
git clone https://github.com/alejandrorivera22/great-travel.git
cd great_travel

### 3. Start PostgreSQL  and Redis
docker-compose up -d

### 4. Build and run the application
- ./mvnw clean install
- ./mvnw spring-boot:run

### 5. Access the API at:
- http://localhost:8080/great_travel

### 6. Access the Swagger UI documentation:
- http://localhost:8080/great_travel/swagger-ui/index.html
---

## Predefined Test Users

These users are preloaded into the database (`data.sql`)  
and can be used to simulate authentication and authorization  
according to the different roles available in the system.

| Rol      | Username    | Contraseña       |
|----------|-------------|------------------|
| Admin    | `admin`     | `adminpassword`  |
| Customer | `john_doe ` | `password123`    |

> Passwords are encrypted using BCrypt.  
> These credentials are provided for local testing purposes only.

---

###  How to Test JWT Authentication in Swagger

1. Open Swagger UI (`http://localhost:8080/great_travel/swagger-ui/index.html`) in your browser.
2. Go to `POST /auth/login` and authenticate using one of the predefined users.
3. Copy the JWT token found in the `token` field of the response.
4. Click on the **"Authorize"** button (lock icon).
5. Paste the token.

##  Author

**Alejandro Rivera**
- [![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?logo=linkedin)](https://www.linkedin.com/in/alejandro-rivera-verdayes-443895375/)
- [![GitHub](https://img.shields.io/badge/GitHub-000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/alejandrorivera22)