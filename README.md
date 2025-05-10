# store-api-springboot

A minimal store management API built with Spring Boot, Java 17, H2, and role-based security.

---

## 🎯 Goal

Design and implement a minimal but realistic store management backend with role-based access and clear structure. Focus is placed on clean API contracts, Java 17+ features, error handling, and maintainability.

---

## ✅ Target API Endpoints

| Method | Endpoint            | Role Access | Description               |
|--------|---------------------|-------------|---------------------------|
| POST   | `/products`         | ADMIN       | Add a new product         |
| GET    | `/products`         | USER, ADMIN | List all products         |
| GET    | `/products/{id}`    | USER, ADMIN | Retrieve a product by ID  |
| PUT    | `/products/{id}`    | ADMIN       | Change the product's price|

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

### List All Products (USER/ADMIN)
```bash
curl -X GET http://localhost:8080/api/products \
  -u user:userpass
```

### Get Product by ID (USER/ADMIN)
```bash
curl -X GET http://localhost:8080/api/products/1 \
  -u user:userpass
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

Note: The `version` field is optional. If provided, the update will only succeed if it matches the current version in the database. If omitted, the update will proceed regardless of the current version.

---

## 🔐 Security

### Authentication & Roles
- In-memory authentication using Spring Security

### Roles
- `ADMIN`: full access  
- `USER`: read-only access

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
This project follows a classic **layered architecture**. 
Target: clear separation of concerns & maintainability. It is intentionally simple and pragmatic to suit the scope of the assignment


## 🔧 Tech Stack
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- H2 (in-memory DB)
- Maven
- JUnit 5
- SLF4J / Logback for logging

---

## 🧠 Design Decisions

- **Top-Down Approach**: Started from API contract to ensure clarity and focused on business 1st.
- **H2 In-Memory DB**: Simulates real persistence with minimal setup; integrates cleanly with Spring Data JPA.
- **In-Memory Security**: Simple, role-based authentication using Spring Security to demonstrate access control.
- **Layered Structure**: Separation of concerns (Controller → Service → Repository) for clarity and testability.
- **Minimal Testing**: One unit test to demonstrate code testability, as per assignment scope.
- **No Swagger**: Omitted for simplicity, with API documented directly in README.

## ❌ Excluded by Design
- Frontend
- Persistent external DB
- Swagger/OpenAPI
- OAuth2 / JWT authentication

---

## ▶️ How to Run

```bash
mvn spring-boot:run
```

## 🧪 How to Test

```bash
mvn test
```

