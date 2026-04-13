# AutoCatalog

Automotive encyclopedia REST API — makes, models, generations, engines, and transmissions — built with Java 25, Spring Boot 4, and PostgreSQL, structured as a DDD / Ports & Adapters project.

## Stack

- **Language & runtime**: Java 25
- **Framework**: Spring Boot 4 (Web, Data JPA, Validation)
- **Database**: PostgreSQL + Flyway migrations
- **Docs**: SpringDoc OpenAPI / Swagger UI
- **Build**: Maven (`./mvnw`)
- **Tests**: JUnit 5, Mockito, TestContainers

## Architecture

```
live.yurii.autocatalog.domain/          pure domain — zero Spring/JPA
live.yurii.autocatalog.application/     use cases / services
live.yurii.autocatalog.infrastructure/  JPA adapters, composition root
live.yurii.autocatalog.api/             REST controllers, DTOs
```

The domain layer has no framework dependencies and is tested in isolation. Repository interfaces live in the domain and are implemented by adapters in the infrastructure layer. Services are wired with `@Bean` in `ApplicationConfig`, not `@Service`.

## Quick start

```bash
# 1. start a local Postgres (or use your own)
docker run -d --name autocatalog-db -p 5432:5432 \
  -e POSTGRES_DB=autocatalog -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  postgres:latest

# 2. run the app (Flyway migrates on startup)
./mvnw spring-boot:run

# 3. open Swagger UI
open http://localhost:8080/swagger-ui.html
```

## Commands

| Command | Purpose |
|---|---|
| `./mvnw spring-boot:run` | Start the application |
| `./mvnw test` | Run the full test suite (domain, controllers, JPA adapters, e2e with TestContainers) |
| `./mvnw flyway:migrate` | Apply database migrations manually |

## License

[MIT](LICENSE)
