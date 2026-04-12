# AutoCatalog

Automotive encyclopedia REST API built with Java 25, Spring Boot 4, PostgreSQL.

## Stack
- Java 25, Spring Boot 4, Maven
- PostgreSQL + Flyway migrations
- Spring Data JPA
- SpringDoc OpenAPI (Swagger UI: http://localhost:8080/swagger-ui.html)

## Architecture (DDD + Ports & Adapters)
```
live.yurii.autocatalog.domain/          — pure domain, zero Spring
live.yurii.autocatalog.application/     — use cases / services
live.yurii.autocatalog.infrastructure/  — JPA, config (Composition Root)
live.yurii.autocatalog.api/             — REST controllers, DTOs
```

## Commands
```bash
./mvnw spring-boot:run     # start app
./mvnw test                # run tests
./mvnw flyway:migrate      # run DB migrations
```

## Rules
- Domain layer must never import Spring or JPA
- Always run tests after making changes
- Use conventional commits: feat:, fix:, chore:
