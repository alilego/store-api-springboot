# Store API Documentation

## Product Endpoints

### Create a Product
**POST** `/api/products`

**Role Required:** ADMIN

**Request Body:**
```json
{
    "name": "Premium Widget",
    "price": 99.99
}
```

**Response (201 Created):**
```json
{
    "id": 1,
    "name": "Premium Widget",
    "price": 99.99,
    "version": 1,
    "createdAt": "2024-05-05T12:00:00Z",
    "updatedAt": "2024-05-05T12:00:00Z"
}
```

### List All Products
**GET** `/api/products`

**Role Required:** USER, ADMIN

**Response (200 OK):**
```json
{
    "content": [
        {
            "id": 1,
            "name": "Premium Widget",
            "price": 99.99,
            "version": 1,
            "createdAt": "2024-05-05T12:00:00Z",
            "updatedAt": "2024-05-05T12:00:00Z"
        },
        {
            "id": 2,
            "name": "Basic Widget",
            "price": 49.99,
            "version": 1,
            "createdAt": "2024-05-05T12:30:00Z",
            "updatedAt": "2024-05-05T12:30:00Z"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        }
    },
    "totalElements": 2,
    "totalPages": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": true,
    "numberOfElements": 2,
    "empty": false
}
```

### Get Product by ID
**GET** `/api/products/{id}`

**Role Required:** USER, ADMIN

**Response (200 OK):**
```json
{
    "id": 1,
    "name": "Premium Widget",
    "price": 99.99,
    "version": 1,
    "createdAt": "2024-05-05T12:00:00Z",
    "updatedAt": "2024-05-05T12:00:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
    "timestamp": "2024-05-05T12:00:00Z",
    "status": 404,
    "error": "Not Found",
    "message": "Product with id 999 not found",
    "path": "/api/products/999"
}
```

### Update Product Price
**PUT** `/api/products/{id}`

**Role Required:** ADMIN

**Request Body:**
```json
{
    "price": 89.99
}
```

**Response (200 OK):**
```json
{
    "id": 1,
    "name": "Premium Widget",
    "price": 89.99,
    "version": 2,
    "createdAt": "2024-05-05T12:00:00Z",
    "updatedAt": "2024-05-05T13:00:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
    "timestamp": "2024-05-05T12:00:00Z",
    "status": 404,
    "error": "Not Found",
    "message": "Product with id 999 not found",
    "path": "/api/products/999"
}
```

## Common Error Responses

### 401 Unauthorized
```json
{
    "timestamp": "2024-05-05T12:00:00Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Full authentication is required to access this resource",
    "path": "/api/products"
}
```

### 403 Forbidden
```json
{
    "timestamp": "2024-05-05T12:00:00Z",
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied",
    "path": "/api/products"
}
```

### 400 Bad Request
```json
{
    "timestamp": "2024-05-05T12:00:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "errors": [
        {
            "field": "price",
            "message": "Price must be greater than 0"
        }
    ],
    "path": "/api/products"
}
``` 