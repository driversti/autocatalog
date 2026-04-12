# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

AutoCatalog — automotive encyclopedia REST API (makes, models, generations, engines, transmissions). Java 25, Spring Boot 4, PostgreSQL, Flyway, SpringDoc OpenAPI.

## Commands

```bash
./mvnw spring-boot:run                 # run app (Flyway migrates on startup; devtools + docker-compose auto-starts Postgres via compose.yaml)
./mvnw test                            # full test suite: domain unit, controller slice, JPA adapter, e2e with Testcontainers
./mvnw test -Dtest=EngineTest          # single test class
./mvnw test -Dtest=EngineTest#createsEngine  # single test method
./mvnw flyway:migrate                  # apply migrations manually
./mvnw spring-boot:build-image         # build OCI image
```

Swagger UI: http://localhost:8080/swagger-ui.html

The app uses `spring-boot-docker-compose` — running `./mvnw spring-boot:run` will auto-start the Postgres service defined in `compose.yaml` (db name `ac`, user `acuser`). The `db/` directory holds mounted Postgres data and should not be committed.

## Architecture — DDD + Ports & Adapters

Four packages under `live.yurii.autocatalog`, each with sub-packages per aggregate (`engine`, `generation`, `make`, `model`, `transmission`, plus `shared`):

```
domain/         pure Java — no Spring, no JPA, no Lombok. Entities, value objects, repository interfaces, domain exceptions.
application/    use cases / services. Plain classes, no Spring annotations. Depend only on domain.
infrastructure/ JPA entities + repository adapters (implement domain repo interfaces), ApplicationConfig (Composition Root).
api/            REST controllers, request/response DTOs (records), exception handler.
```

**The Composition Root pattern is load-bearing.** Services are wired manually with `@Bean` methods in `infrastructure/config/ApplicationConfig.java` — not via `@Service` / component scan. This is what lets `application/` stay free of Spring annotations. When adding a new service, register it there; do not annotate it with `@Service`.

**Domain aggregate pattern** (see `domain/engine/Engine.java` as the canonical example):
- Private no-arg constructor; state mutated only via explicit methods.
- Static factory `create(...)` — validates invariants, generates a new `*Id`.
- Static factory `reconstitute(...)` — rebuilds from persisted state without re-validating. Used exclusively by JPA adapters when mapping rows back to domain objects.
- Getters are record-style (`id()`, `name()`), not JavaBean `getX()`.
- IDs are typed value objects (`EngineId`, `MakeId`, …), not raw UUIDs/longs.

**Repository interfaces live in `domain/<aggregate>/`**; their implementations live in `infrastructure/persistence/<aggregate>/*RepositoryAdapter.java` and translate between the domain aggregate and a `*JpaEntity`. Never leak JPA entities out of the infrastructure layer.

## Architecture decisions

### DDD + Ports & Adapters
- Domain layer has zero Spring/JPA dependencies — enforced, never break this
- Services are plain Java classes wired via @Bean in ApplicationConfig (Composition Root)
- No @Service on domain/application classes
- Repository interfaces live in domain, implementations in infrastructure

### Reconstitute pattern
Every aggregate has two factory methods:
- `create(...)` — validates and generates ID, used for new entities
- `reconstitute(...)` — no validation, used by repository adapters to restore from DB

### Naming
- `CarModel` not `Model` — avoids conflict with JPA/java.lang
- `MakeId`, `ModelId`, `GenerationId` etc — typed IDs as records, never raw UUID in domain

## Domain model decisions

### Body → Variant (replacing Trim)
Decided to model as two levels instead of flat Trim:
- `Body` — belongs to Generation, holds body style + dimensions (length, width, height,
  wheelbase, trunk volume). Dimensions are same for all variants of this body.
- `Variant` — belongs to Body, holds technical config (engines M:N, transmission,
  drivetrain, ground clearance, curb weight, system power for hybrids, markets)
- Reasoning: old models with no variants → one Body + one Variant, no model violence
- Trim level names (Sport, Luxury) — explicitly NOT modelled, too market-specific

### Generation (simplified)
After Body/Variant introduction, Generation holds ONLY:
- name (e.g. "E210", "Mk7")
- yearFrom, yearTo
- modelId
  All technical specs moved to Variant. Tables dropped:
- generation_engines, generation_transmissions,
  generation_body_styles, generation_drivetrains

### Engine (shared reference aggregate)
- Shared across generations, models, makes
- Has official manufacturer code (e.g. "2GR-FE", "N57")
- For hybrids: ICE engine + separate ELECTRIC engine, both linked to Variant
- systemPowerKw lives on Variant (combined hybrid output), not on Engine

### Transmission (shared reference / lookup table)
- Unique on (type, gear_count) — it's a lookup, not a unique physical object
- No manufacturer code — unlike Engine

### Markets
- Stored as Set<String> of ISO 3166-1 alpha-2 codes on Variant
- e.g. ["UA", "EU", "US", "JP"]
- No separate Market entity — YAGNI

### Uniqueness invariants (enforced at service + DB level)
- Make: unique name
- CarModel: unique (make_id, name) and unique (make_id, slug)
- Generation: unique (model_id, name)
- Engine: unique code
- Transmission: unique (type, gear_count)
- Body: unique (generation_id, body_style)
- Variant: TBD — likely unique (body_id, transmission_id, drivetrain)

## Database migrations

Flyway migrations in `src/main/resources/db/migration/` are append-only and versioned `V{n}__description.sql`. When changing a schema, add a new `V{n}` — do not edit existing migrations. Pair schema changes with backfills in a follow-up migration when needed (see `V8__engine_system_power_and_rename_5afe.sql` + `V9__backfill_hybrid_system_power.sql` for the pattern).

## Testing layout

Tests under `src/test/java/live/yurii/autocatalog/` mirror the main-source structure:
- `domain/` — pure unit tests, no Spring context.
- `api/` — controller slice tests (`@WebMvcTest`).
- `infrastructure/` — JPA adapter tests (`@DataJpaTest`) against Testcontainers Postgres.
- `e2e/` — full Spring Boot tests with Testcontainers Postgres (`TestcontainersConfiguration.java`, `TestAutocatalogApplication.java`).

Always run `./mvnw test` after changes.

## Rules

- **Domain layer must never import Spring or JPA.** If you find yourself adding `org.springframework.*` or `jakarta.persistence.*` to `domain/`, you're in the wrong layer.
- **No Lombok in this project** — despite global preferences. Use plain Java + records for DTOs.
- **DTOs are records** in `api/*/` (see `EngineRequest`, `EngineResponse`).
- **Conventional commits**: `feat:`, `fix:`, `chore:`, `refactor:`, `test:`, `docs:`.
- **Always ask before creating commits.**
