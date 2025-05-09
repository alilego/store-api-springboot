# ProjectPlan.md

## 🛠️ Project Development Plan (Top-Down Approach)

This document outlines the step-by-step plan to implement the Store Management API using a top-down approach — starting from the API contract and building internals iteratively.

---

### ✅ Step 1: API Design and Planning (Top-Level Contract)

- ✅ Define business use cases and main user roles (ADMIN, USER)
- ✅ List required API endpoints in `README.md` (GET, POST, PUT /products)
- ✅ Specify role-based access for each endpoint
- ✅ Create `ProjectPlan.md` with clear development steps

---

### ✅ Step 2: Scaffold the Spring Boot Project

- ✅ Generate Spring Boot + Maven project with:
  - Spring Web 
  - Lombok
- ✅ Set Java version to 17
- ✅ Create base package structure:
```
store-api-springboot/
├── controller/         # REST endpoints (ProductController)
├── service/            # Business logic (ProductService)
├── model/              # JPA entity (Product)
├── dto/                # Data Transfer Objects
├── repository/         # Spring Data JPA repository
├── config/             # Security configuration
├── exception/          # Global exception handlers
├── docs/               # API documentation
├── resources/
│   └── application.yml # DB config, H2 console
├── test/               # Unit test for ProductService
└── StoreApiApplication.java
```
- ✅ Document example request/response payloads

---

### ✅ Step 3: API Interface (Stub Only)

- ✅ Create `ProductController`
- ✅ Define method stubs for:
- `POST /products`
- `GET /products/{id}`
- `PUT /products/{id}`
- `GET /products` (list all)
- ✅ Return hardcoded or placeholder responses
- ✅ Add basic logging in controller methods

---

### ✅ Step 4:  Domain Model and Persistence - use Spring Data JPA & H2 Database

- ✅ Define `Product` entity (`id`, `name`, `price`, `version`, `createdAt`, `updatedAt`)
- ✅ Use JPA annotations (`@Entity`, `@Id`, `@GeneratedValue`)
- ✅ Enable JPA auditing via `@EnableJpaAuditing`
- ✅ Use `@CreatedDate` and `@LastModifiedDate` with Spring Data auditing
- ✅ Create `ProductRepository` extending `JpaRepository`
- ✅ Add optimistic locking to `Product` using `@Version`

---

### ✅ Step 5: Service Layer Implementation

- ✅ Create `ProductService` and implement methods:
- `addProduct(Product product)`
- `getProductById(Long id)`
- `updatePrice(Long id, BigDecimal price)`
- `getAllProducts()`
- ✅ Connect with `ProductRepository`
- ✅ Throw `ProductNotFoundException` where appropriate

---

### ✅ Step 6: Security Configuration - use Spring Security

- ✅ Create `SecurityConfig` class
- ✅ Define in-memory users and roles:
- `admin / adminpass` → ROLE_ADMIN
- `user / userpass` → ROLE_USER
- ✅ Protect endpoints using URL-based rules:
- `GET /products/**` → USER, ADMIN
- `POST` and `PUT` → ADMIN only

---

### ✅ Step 7: Logging & Observability

- ✅ Use SLF4J with Logback (default in Spring Boot)
- ✅ Add loggers in all key classes 
- ✅ Log at appropriate levels:
  - `INFO` for business actions (e.g., "Product added", "Price updated") & security events
  - `WARN` or `ERROR` for exceptions and unexpected conditions
  - `DEBUG` for SQL queries 
- ✅ Avoid logging sensitive data (credentials, tokens, user IDs)
- ✅ Add request ID traceability using MDC (`Mapped Diagnostic Context`)
- ✅ Customize log format to include: datetime, log level, API request id, thread, message
  
---

### ⬜ Step 8: Error Handling

- ✅ Create custom exception `ProductNotFoundException`
- ✅ Create `GlobalExceptionHandler` using `@ControllerAdvice`
- ✅ Return standardized error response structure:
  - Include: timestamp, status, error, message, path
- [ ] Return appropriate HTTP status codes (e.g., 400, 403, 404, 409, 500)
- [ ] Handle key exceptions explicitly:
  - `OptimisticLockException` → 409 Conflict
  - `MethodArgumentNotValidException` (validation failure) → 400 Bad Request
  - `HttpMessageNotReadableException` (malformed JSON) → 400 Bad Request
  - `AccessDeniedException` → 403 Forbidden
  - `DataIntegrityViolationException` → 409 Conflict or 400
- [ ] Fallback: handle `Exception` → 500 Internal Server Error
- [ ] Log all handled exceptions with context (including request details if helpful)
- [ ] Ensure no sensitive data (credentials, IDs, stack traces) leaks into client responses

---

### ⬜ Step 9: Testing

- ✅ Add unit test for `ProductService`
  - Test happy path and error scenarios
- [ ] Add functional test for `ProductService`
- ✅ Add CI - run tests upon git push

---

### ✅ Step 10: Infrastructure & Configuration

- ✅ Enable H2 console (`/h2-console`) in `application.yml`
- ✅ Configure datasource: `jdbc:h2:mem:storedb`


---

### ⬜ Step 11: Optional Stretch Goals

- [ ] Add pagination to `GET /products`
- ✅ Add input validation (`@Valid`, DTOs)

---

### ⬜ Step 12: Finalization & Delivery

- [ ] Finalize `README.md` with final features and instructions:
- Usage instructions
- Example API calls
- Test credentials
- Design decisions
- Architecture overview
- [ ] Polish code: remove stubs, organize imports, format
- [ ] Verify project runs cleanly
