# store-api-springboot

A minimal store management API built with Spring Boot, Java 17, H2, and role-based security.

---

## 🎯 Goal

Design and implement a minimal but realistic store management backend with role-based access and clear structure. Focus is placed on clean API contracts, error handling, and maintainability.

---

## ✅ Target API Endpoints

| Method | Endpoint                    | Role Access | Description                                    |
|--------|----------------------------|-------------|------------------------------------------------|
| POST   | `/api/products`            | ADMIN       | Create a new product                           |
| GET    | `/api/products`            | USER, ADMIN | List all products with pagination and sorting  |
| GET    | `/api/products/{id}`       | USER, ADMIN | Get a product by ID                           |
| PUT    | `/api/products/{id}`       | ADMIN       | Update product price with optimistic locking   |
| DELETE | `/api/products/{id}`       | ADMIN       | Soft delete a product                         |

### Base URL
- Local: `http://localhost:8080`
- All endpoints require Basic Authentication
- Content-Type: `application/json`

### Common Query Parameters
- **GET `/api/products`**:
  - `page`: Page number (default: 0)
  - `size`: Items per page (default: 10)
  - `sortBy`: Field to sort by (default: "id")
  - `direction`: Sort direction - "asc" or "desc" (default: "asc")

---

## ▶️ How to Run

```bash
mvn spring-boot:run
```

---

## 🧪 How to Test

```bash
mvn test
```

---

## 📡 API Examples

### Create a Product (ADMIN only)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -u admin:adminpass \
  -d '{
    "name": "Premium Widget",
    "price": 99.99
  }'
```

Response:
```json
{
  "id": 1,
  "name": "Premium Widget",
  "price": 99.99,
  "version": 0,
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

### List All Products (USER/ADMIN)
```bash
curl -X GET "http://localhost:8080/api/products?page=0&size=10&sortBy=id&direction=asc" \
  -u user:userpass
```

Response:
```json
{
  "content": [
    {
      "id": 1,
      "name": "Premium Widget",
      "price": 99.99,
      "version": 0,
      "createdAt": "2024-01-01T10:00:00Z",
      "updatedAt": "2024-01-01T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "direction": "ASC"
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0
}
```

Query Parameters:
- `page`: Page number (default: 0)
- `size`: Items per page (default: 10)
- `sortBy`: Field to sort by (default: "id")
- `direction`: Sort direction - "asc" or "desc" (default: "asc")

### Get Product by ID (USER/ADMIN)
```bash
curl -X GET http://localhost:8080/api/products/1 \
  -u user:userpass
```

Response:
```json
{
  "id": 1,
  "name": "Premium Widget",
  "price": 99.99,
  "version": 0,
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

### Update Product Price (ADMIN only)
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -u admin:adminpass \
  -d '{
    "price": 89.99,
    "version": 1
  }'
```

Response:
```json
{
  "id": 1,
  "name": "Premium Widget",
  "price": 89.99,
  "version": 2,
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:15:00Z"
}
```

Note: The `version` field is optional. If provided, the update will only succeed if it matches the current version in the database (optimistic locking). If omitted, the update will proceed regardless of the current version.

### Delete Product (ADMIN only)
```bash
curl -X DELETE http://localhost:8080/api/products/1 \
  -u admin:adminpass
```

Response: HTTP 204 No Content

Note: This performs a soft delete, marking the product as deleted in the database while preserving its data.

---

## 🧪 Postman Collection

A comprehensive Postman collection is provided in the `/postman` directory for testing all API endpoints. The collection includes:

- Pre-configured environment variables
- Test cases for all endpoints
- Role-based authentication tests
- Pagination and sorting examples
- Error handling scenarios

### Setup

1. Import `postman/Store API.postman_collection.json` into Postman
2. The collection uses these environment variables:
   - `baseUrl`: `http://localhost:8080`
   - `adminAuthHeader`: Base64 encoded admin credentials
   - `userAuthHeader`: Base64 encoded user credentials

### Test Cases Include

- Success scenarios for all endpoints
- Role-based access control testing
- Input validation error cases
- Pagination and sorting variations
- Version control testing for updates
- Error handling for various HTTP status codes

For detailed documentation about the test cases and usage, see `/postman/README.md`.

---

## 📚 API Documentation

### Swagger UI
The API documentation is available through Swagger UI at:
- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### OpenAPI Specification
The OpenAPI specification is available at:
- [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) (JSON)
- [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml) (YAML)

Features:
- Interactive API documentation
- Try-it-out functionality with authentication
- Detailed request/response schemas
- Role-based security documentation
- Built-in authentication support

---

## 🔐 Security

### Authentication & Roles
- In-memory authentication using Spring Security

### Roles
- `ADMIN`: full access (create, read, update, delete)  
- `USER`: read-only access (list and get by ID)

### Test Users
- `admin / adminpass`  
- `user / userpass`

---

## 🛢️ Persistence
- Uses **H2**, an in-memory embedded database  
- Managed via **Spring Data JPA**  
- Accessible through: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- Use:
  - JDBC URL `jdbc:h2:mem:storedb`
  - username: `sa`
  - password: `pass`

---

## 🏗️ Architecture Overview

This project follows a classic **layered architecture** with:

### Core Components
- **Controllers**: REST endpoints, input validation, API docs
- **Services**: Business logic, transactions, caching
- **Data Layer**: JPA entities and repositories, pagination, auditing

### Cross-Cutting Features
- **Security**: Role-based access with Spring Security
- **Observability**: Structured logging with request tracking
- **Error Handling**: Global exception handling with standard responses
- **Caching**: In-memory caching with Caffeine
- **DTOs**: Clean API contracts and validation
- **Documentation**: OpenAPI/Swagger + Postman collection

Target: Maintainable, testable codebase with clear component boundaries.

## 🔧 Tech Stack

### Core
- **Language**: Java 17
- **Framework**: Spring Boot
- **Build Tool**: Maven

### Security & Validation
- **Authentication**: Spring Security
- **Input Validation**: Jakarta Validation

### Data
- **ORM**: Spring Data JPA
- **Database**: H2 (in-memory)
- **Caching**: Caffeine

### Documentation
- **API Docs**: SpringDoc OpenAPI (Swagger)
- **API Testing**: Postman

### Observability
- **Logging**: SLF4J / Logback / MDC
- **Testing**: JUnit 5

---

## 🧠 Design Decisions

- **Top-Down Approach**: Started from API contract to ensure clarity and focus on business 1st.
- **H2 In-Memory DB**: Simulates real persistence with minimal setup; integrates cleanly with Spring Data JPA.
- **In-Memory Security**: Simple, role-based authentication using Spring Security to demonstrate access control.
- **Layered Structure**: Separation of concerns (Controller → Service → Repository) for clarity and testability.
- **Optimistic Locking**: Version-based concurrency control for price updates.
- **Soft Deletes**: Products are marked as deleted rather than physically removed.
- **Auditing**: Automatic tracking of creation and modification timestamps.
- **Pagination**: Built-in support for large datasets with sorting options.
- **Minimal CI Testing**: Unit & integration testing for service layer to demonstrate code testability, as per assignment scope.

## ❌ Excluded by Design
- Frontend
- Persistent external DB
- OAuth2 / JWT authentication



