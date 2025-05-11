# Store API Postman Collection

This collection contains API requests for testing the Store API endpoints with banking products and security roles.

## Environment Variables

The collection uses the following environment variables:

- `baseUrl`: The base URL of the API (default: `http://localhost:8080`)
- `adminAuthHeader`: Base64 encoded admin credentials (default: `YWRtaW46YWRtaW5wYXNz` - 'admin:adminpass')
- `userAuthHeader`: Base64 encoded user credentials (default: `dXNlcjp1c2VycGFzcw==` - 'user:userpass')

## Available Requests

### Products

#### Create Product (Admin Only)
- **POST** `/api/products`
- Requires admin authentication
- Test cases:
  - Success case with valid product data
  - Invalid input (missing required fields)
  - Invalid input (negative price)
  - Unauthorized access (user role)

#### Get Product by ID (User/Admin)
- **GET** `/api/products/{id}`
- Accessible by both admin and user roles
- Test cases:
  - Success case (admin role)
  - Success case (user role)
  - Not found (unknown ID)

#### Update Product Price (Admin Only)
- **PUT** `/api/products/{id}`
- Requires admin authentication
- Test cases:
  - Success case with version
  - Success case without version
  - Invalid input (negative price)
  - Version mismatch
  - Unauthorized access (user role)

#### Get All Products (User/Admin)
- **GET** `/api/products`
- Accessible by both admin and user roles
- Supports pagination and sorting
- Test cases:
  - Default pagination (admin role)
  - Default pagination (user role)
  - Custom pagination (5 per page, sorted by name)

#### Soft Delete Product (Admin Only)
- **DELETE** `/api/products/{id}`
- Requires admin authentication
- Test cases:
  - Success case (204 No Content)
  - Not found (unknown ID, 404 Not Found)

## Testing Tips

1. **Role-Based Testing**
   - Admin-only endpoints (POST/PUT) use `{{adminAuthHeader}}`
   - User/Admin endpoints (GET) have separate test cases for both roles
   - Each endpoint includes unauthorized access test cases

2. **Pagination and Sorting**
   - Default page size is 10 items
   - Supports custom page size and sorting parameters
   - Example: `?page=0&size=5&sortBy=name&direction=asc`

3. **Version Control**
   - PUT requests support both versioned and unversioned updates
   - Version mismatch returns 409 Conflict

4. **Error Handling**
   - 400 Bad Request for validation errors
   - 401 Unauthorized for missing/invalid credentials
   - 403 Forbidden for insufficient permissions
   - 404 Not Found for non-existent resources
   - 409 Conflict for version mismatches