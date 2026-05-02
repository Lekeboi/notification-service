# Notification Service

A Spring Boot REST API that accepts notification requests, stores them in a database, and dispatches them via a mock provider that simulates real-world network behaviour (500ms delay, 20% failure rate).

## Prerequisites

- Java 17+
- Maven 3.6+

## How to Run

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

## Architecture & Design

### Layered Architecture

The application follows a standard three-layer architecture:

```
Controller  →  Service  →  Repository
(HTTP)         (Logic)      (Database)
```

- **Controller** — the entry point. Its only job is to receive HTTP requests, pass them to the service, and return the response. It has no business logic.
- **Service** — where all the decisions are made. It validates input, saves to the database, calls the provider, and updates the status.
- **Repository** — handles all database interaction. By extending Spring's `JpaRepository`.

This separation means each layer has one clear responsibility. If the database changes, only the repository layer is affected. If the business rules change, only the service layer is affected.

---

### Why DTOs?

The app uses two separate objects for the notification: `NotificationRequest` and `NotificationResponse`: instead of using the `Notification` entity directly.

- **`NotificationRequest`** is what the caller sends in. It only contains the fields the caller is allowed to set (`type`, `recipient`, `message`). Fields like `id`, `status`, and timestamps are not exposed — the caller should not be able to set those.
- **`NotificationResponse`** is what gets sent back. It controls exactly what the caller sees

If the internal `Notification` entity were exposed directly, any change to the database schema could accidentally break the API contract. DTOs act as a buffer between the internal data model and the outside world.

---

### Provider Pattern

The dispatch logic sits behind a `NotificationProvider` interface:

```
NotificationProvider  (interface)
        ↑
MockNotificationProvider  (implementation)
```

This means the service only depends on the interface, not on any specific implementation. To swap the mock for a real provider, you would write a new class that implements the interface, the service itself would not need to change at all.

---

### Notification Status Lifecycle

```
Request received → PENDING → dispatch attempted → SENT
                                                → FAILED
```

Every notification is persisted to the database as `PENDING` before dispatch is attempted. This is intentional, it guarantees there is always a record of every request, even if the application crashes mid-dispatch. The `GET /v1/notifications/{id}` endpoint exists specifically so callers can check the final status after the fact.

---

## Testing the API

### On Linux / Mac (bash/zsh)

**Send an EMAIL notification:**
```bash
curl -X POST http://localhost:8080/v1/notifications \
  -H "Content-Type: application/json" \
  -d '{"type": "EMAIL", "recipient": "user@example.com", "message": "Hello"}'
```

**Send an SMS notification:**
```bash
curl -X POST http://localhost:8080/v1/notifications \
  -H "Content-Type: application/json" \
  -d '{"type": "SMS", "recipient": "+12345678901", "message": "Hello"}'
```

**Check status:**
```bash
curl http://localhost:8080/v1/notifications/{id}
```

---

### On Windows (PowerShell)

Use `Invoke-RestMethod`, PowerShell's built-in HTTP client. Do not use `curl` in PowerShell as it behaves differently.

**Send an EMAIL notification:**
```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/v1/notifications" -ContentType "application/json" -Body '{"type":"EMAIL","recipient":"user@example.com","message":"Hello"}'
```

**Send an SMS notification:**
```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/v1/notifications" -ContentType "application/json" -Body '{"type":"SMS","recipient":"+12345678901","message":"Hello"}'
```

**Check status** (replace the id with the one returned from the POST):
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/notifications/paste-id-here"
```

---

### Example Response

```json
{
  "id": "a3f1c2d4-...",
  "type": "EMAIL",
  "recipient": "user@example.com",
  "message": "Hello",
  "status": "SENT",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

> The `status` field will be either `SENT` or `FAILED` — the mock provider has a 20% chance of failure to simulate network instability.

---

## Validation Rules

| Field       | Rules                                           |
|-------------|-------------------------------------------------|
| `type`      | Required. Must be `EMAIL` or `SMS`              |
| `recipient` | Required. Must be a valid email or phone number |
| `message`   | Required. Cannot be blank                       |

## Notification Status

| Status    | Meaning                              |
|-----------|--------------------------------------|
| `PENDING` | Saved but dispatch not yet attempted |
| `SENT`    | Successfully dispatched              |
| `FAILED`  | Dispatch failed (network error)      |

## H2 Database Console

You can inspect the database at `http://localhost:8080/h2-console`.

- JDBC URL: `jdbc:h2:mem:notificationdb`
- Username: `notification_service`
- Password: *(leave blank)*
