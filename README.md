# Contact Service REST API

A Spring Boot application that provides a REST API for managing contacts using SQLite database.

## Features

- REST API for CRUD operations on contacts
- Content negotiation supporting JSON, XML, CSV, and text formats
- Pagination support for listing contacts
- SQLite database for data persistence
- Dockerized application
- OpenAPI documentation with Swagger UI
- Interactive welcome page with API overview

## API Endpoints

### GET /api/contacts

- Retrieves a list of contacts
- Supports pagination with `page` and `limit` parameters
- Default: page=1, limit=10
- Example: `/api/contacts?page=1&limit=10`

### GET /api/contacts/{id}

- Retrieves a specific contact by ID

### POST /api/contacts

- Creates a new contact

### PUT /api/contacts/{id}

- Updates an existing contact

### DELETE /api/contacts/{id}

- Deletes a contact

## Content Negotiation

All endpoints support multiple formats:

- JSON (default): `/api/contacts`
- XML: `/api/contacts?format=xml`
- CSV: `/api/contacts?format=csv`
- Text: `/api/contacts?format=txt`

You can also use the `Accept` header to specify the desired format.

## API Documentation

The API is documented using OpenAPI 3.0 (Swagger) specification:

- Welcome Page: http://localhost:8080/
- OpenAPI JSON: http://localhost:8080/api-docs
- Swagger UI: http://localhost:8080/swagger-ui.html

The Swagger UI provides an interactive documentation where you can:

- Explore available endpoints and operations
- View request/response formats and schemas
- Test API calls directly from the browser
