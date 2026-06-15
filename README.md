# Employee Payroll — JPA + JMS

A Kotlin / Spring Boot 4 payroll system with a JPA data layer, a RESTful API, and **asynchronous
salary generation over a JMS message queue** (Message Oriented Middleware).

## Modules

| Module | Role |
| --- | --- |
| `payroll-service` | REST API + JPA (H2) data store + `SalaryConsumerService` + the embedded ActiveMQ broker |
| `salary-generator` | Standalone app: downloads users over REST, publishes generated salaries to the queue |
| `contract` | Shared JMS message type (`SalaryMessage`) + queue name, used by both sides |

## Running

Requires **JDK 21** (the Gradle wrapper is included).

```bash
# 1) payroll service — REST API on :8080 + embedded broker on :61616 (seeds sample data)
./gradlew :payroll-service:bootRun

# 2) in another terminal — generator: fetch users, publish salaries to the queue, then exit
./gradlew :salary-generator:bootRun
#    override parameters, e.g.:
#    --args='--app.generation.months-per-user=4 --app.generation.min-salary=4000 --app.generation.max-bonus=0'
```

Open <http://localhost:8080>, then reload **Browse → Salaries** to see the generated rows.

## Endpoints & tools

- UI: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- H2 console: <http://localhost:8080/h2-console> (JDBC `jdbc:h2:mem:payroll`, user `sa`, no password)
- Ready-to-run request examples: [`requests.md`](requests.md)

## Build & test

```bash
./gradlew build
```

## Tech

Kotlin · Spring Boot 4 · Hibernate / Jakarta Persistence · H2 (in-memory) · ActiveMQ / JMS ·
multi-module Gradle.
