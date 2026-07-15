# Sunrise Dental Clinic - Appointment and Patient Management System

![CI](https://github.com/Tharinduakash/Sunrise-Dental-Clinic-appointment-and-patient-management-system/actions/workflows/ci.yml/badge.svg)

CIS6003 Advanced Programming coursework (Cardiff Metropolitan University, WRIT1). A distributed
appointment and patient management system for Sunrise Dental Clinic, replacing paper-based
booking with a Spring Boot REST API and a JavaFX desktop client.

## Scenario

Sunrise Dental Clinic previously tracked appointments and treatment records on paper, leading to
double bookings, lost records, long waiting times, and billing errors. This system gives staff a
computerised way to register appointments, look them up by appointment number, calculate and
print bills, and get help — all behind a login screen restricted to authorised staff.

## Architecture

The project is a two-module Maven build:

- **`backend`** — Spring Boot 3 REST API. Owns the domain model, persistence (PostgreSQL +
  Flyway migrations), business logic, JWT-based authentication, and exposes everything as web
  services.
- **`client`** — JavaFX desktop application. Talks to the backend purely over HTTP/JSON, with no
  compile-time dependency on the backend module, so the two are genuinely separate distributable
  artifacts (satisfying the "distributed application with web services" requirement).

```
sunrise-dental-clinic/
├── backend/    Spring Boot REST API (domain, repositories, services, controllers, security)
├── client/     JavaFX desktop client (views, API client, DTOs)
├── docs/       UML design report, test plan report, TDD evidence, screenshots
└── .github/    CI workflow
```

## Features

- **User authentication** — JWT-secured login; only authorised staff can reach the system.
- **Register new appointment** — captures appointment number, patient name, address, contact
  number, dentist, treatment type, appointment date/time.
- **Display appointment details** — search and view full appointment/patient information by
  appointment number.
- **Calculate and print bill** — treatment cost computed from treatment type and consultation
  fee, with a printable receipt.
- **Daily/summary reports** — additional reporting to support clinic decision-making.
- **Help section** — step-by-step guidance for new staff.
- **Exit** — safe application shutdown.

## Design patterns

| Pattern | Where | Why |
|---|---|---|
| **Strategy** | `service/billing/` — `BillingStrategy` + `StandardBillingStrategy`, `SurgicalBillingStrategy`, `OrthodonticBillingStrategy` | Different treatment types price differently; each strategy encapsulates one pricing rule instead of a branching `if/else` in `BillService`. |
| **Factory** | `service/billing/BillingStrategyFactory` | Selects the correct `BillingStrategy` for a given treatment type at runtime. |
| **Observer** | `service/notification/` — `AppointmentEventPublisher`, `AppointmentObserver`, `ConsoleNotificationObserver` | Decouples appointment registration from side effects (e.g. notifications) triggered when an appointment is created. |

## Tech stack

- Java 21, Spring Boot 3.3.5 (web, data-jpa, security, validation)
- PostgreSQL + Flyway migrations, H2 for tests
- JWT (jjwt) for stateless authentication
- JavaFX 21 desktop client
- JUnit 5 + Mockito + Spring Security Test

## Building and running

```
mvn clean install          # builds backend + client, runs tests

# Backend (REST API)
cd backend
mvn spring-boot:run

# Client (JavaFX desktop app)
cd client
mvn javafx:run
```

## Testing

The service layer (billing strategies, appointment numbering, appointment/bill/report services,
JWT utilities) was developed test-first. See [`docs/task-c-test-plan.html`](docs/task-c-test-plan.html)
for the full test plan, rationale, and test data, and
[`docs/tdd-red-evidence.txt`](docs/tdd-red-evidence.txt) /
[`docs/tdd-green-evidence.txt`](docs/tdd-green-evidence.txt) for the red/green TDD cycle evidence.

## Reports

- **UML design (Task A):** [`docs/task-a-uml-design.html`](docs/task-a-uml-design.html) — use case,
  class, and sequence diagrams with design rationale.
- **Test plan (Task C):** [`docs/task-c-test-plan.html`](docs/task-c-test-plan.html)
- **Full written report (PDF submitted via Turnitin):** _add shareable link here before
  submission._

## Version control

Development history is committed in stages reflecting how the system was actually built:
project scaffold → domain/persistence layer → business logic & design patterns → REST API &
security → JavaFX client → tests → documentation → CI. See the
[commit history](https://github.com/Tharinduakash/Sunrise-Dental-Clinic-appointment-and-patient-management-system/commits/main)
and [tags](https://github.com/Tharinduakash/Sunrise-Dental-Clinic-appointment-and-patient-management-system/tags)
for milestones.

## CI

Every push and pull request to `main` runs `mvn -B verify` across both modules via GitHub Actions
(see [`.github/workflows/ci.yml`](.github/workflows/ci.yml)), compiling the backend and client and
running the full backend test suite.
