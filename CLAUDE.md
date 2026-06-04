# CLAUDE.md

## Project Overview

This project was developed as a technical challenge for X-Brain.

The application exposes a REST API for order management, persists data using H2 Database, publishes order events to RabbitMQ, and asynchronously processes deliveries through a RabbitMQ consumer.

The application is fully containerized and can be executed through Docker Compose.

---

## Technical Stack

* Java 21
* Spring Boot 4.x
* Spring Data JPA
* H2 Database
* RabbitMQ
* Spring Validation
* Spring Actuator
* SpringDoc OpenAPI (Swagger)
* Lombok
* Docker
* Docker Compose
* GitHub Actions

---

## Architecture

The project follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Messaging components are isolated under the `messaging` package.

Exception handling is centralized through a global exception handler.

---

## Main Business Flow

### Order Creation

1. Client submits an order.
2. Request is validated.
3. Order is persisted in the database.
4. Order event is published to RabbitMQ.
5. API returns the created order.

### Delivery Processing

1. RabbitMQ consumer receives the order message.
2. Delivery entity is created.
3. Delivery data is persisted.
4. Processing is logged.

---

## Domain Model

### Order

Represents a customer order.

Main fields:

* id
* customerCode
* productCodes
* totalAmount
* deliveryAddress
* status
* createdAt

Table:

```text
orders
```

### Product

Embedded value object used by Order.

Fields:

* productCode

### Delivery

Represents the delivery generated after asynchronous processing.

Main fields:

* id
* orderId
* deliveryAddress
* processedAt

Table:

```text
deliveries
```

---

## API Endpoints

### Create Order

```http
POST /api/orders
```

Creates and persists an order and publishes an event to RabbitMQ.

### Find Order By Id

```http
GET /api/orders/{id}
```

Returns a specific order.

### List Deliveries

```http
GET /deliveries
```

Returns all processed deliveries.

This endpoint was added to facilitate validation of the asynchronous workflow and persistence of delivery records.

---

## RabbitMQ

### Queue

```text
orders.queue
```

### Producer

```text
OrderPublisher
```

Responsible for publishing order events.

### Consumer

```text
DeliveryConsumer
```

Responsible for consuming order messages and creating delivery records.

---

## Error Handling

Centralized through:

```text
GlobalExceptionHandler
```

Handled scenarios:

* Order not found → HTTP 404
* Validation errors → HTTP 400
* Invalid routes/resources → HTTP 404
* Unexpected errors → HTTP 500

Responses use Spring ProblemDetail.

---

## Observability

Spring Boot Actuator is enabled.

Available endpoints include:

* health
* info
* metrics

These endpoints assist monitoring and diagnostics.

---

## API Documentation

Swagger/OpenAPI documentation is available for endpoint exploration and testing.

The documentation should remain synchronized with the REST API contracts.

---

## CI/CD

GitHub Actions pipeline runs automatically on every push.

Current pipeline responsibilities:

* Build validation
* Automated test execution

Any future contribution should keep the pipeline green.

---

## H2 Console Notes

During development, an incompatibility between Spring Boot 4.x and the default H2 Console setup was identified.

Although database persistence worked correctly, the H2 web console was initially unavailable.

After investigation, a compatible solution was implemented based on the following reference:

https://medium.com/@raushan1156/h2-console-not-working-in-spring-boot-4-0-0-7873e20c82d5

The H2 Console is now available and can be used for local inspection of persisted data.

---

## Development Guidelines

When modifying this project:

* Keep controllers focused on HTTP concerns.
* Keep business logic inside services.
* Keep persistence logic inside repositories.
* Prefer constructor injection.
* Maintain centralized exception handling.
* Preserve API contracts whenever possible.
* Maintain Docker compatibility.
* Maintain RabbitMQ asynchronous processing flow.
* Keep OpenAPI documentation updated.
* Keep GitHub Actions pipeline passing.

---

## Validation Strategy

The asynchronous workflow can be validated by:

1. Creating an order through the API.
2. Verifying message consumption through application logs.
3. Querying processed deliveries using:

```http
GET /deliveries
```

4. Optionally inspecting persisted data through the H2 Console.

---

## Possible Future Improvements

* Integration tests
* Retry and Dead Letter Queue (DLQ) strategy
* Elasticsearch integration for advanced searches
* Message versioning strategy
