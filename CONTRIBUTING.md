# Contributing to AutoCatalog

Thanks for your interest in contributing. This document covers the minimum you need to know to get a change merged.

## Getting set up

1. **Prerequisites**: JDK 25, Docker (for TestContainers), Git.
2. Clone the repo and run the tests:
   ```bash
   git clone https://github.com/driversti/autocatalog.git
   cd autocatalog
   ./mvnw test
   ```
3. Start a local Postgres and run the app (see [README](README.md#quick-start)).

## Architecture rules

AutoCatalog follows DDD with a Ports & Adapters layering. Please respect the boundaries:

- **`domain/`** — pure Java. Must not import Spring or JPA. Aggregates use static `create()` and `reconstitute()` factories, expose no setters, and change state only through explicit methods. Repository *interfaces* live here.
- **`application/`** — use cases and services. No Spring annotations. Orchestrates domain objects and ports.
- **`infrastructure/`** — JPA entities, repository adapters, Flyway migrations, and `ApplicationConfig` (the composition root that wires services as `@Bean`s). This is the only layer that knows about Spring and JPA internals.
- **`api/`** — REST controllers, DTOs, and exception handling.

If a change crosses a boundary (e.g. a service needs a new repository method), add the port to `domain/` first, then implement it in `infrastructure/`.

## Coding standards

- **Java**: target Java 25 language features. Prefer `records` for DTOs and value objects, Lombok for JPA entities and beans.
- **Errors**: `Optional<T>` over `null`. Exceptions for exceptional cases. Use `EntityNotFoundException` → 404 and `IllegalStateException` → 409 (both mapped in `GlobalExceptionHandler`).
- **Tests**: every feature needs coverage. Domain logic → unit tests. Persistence → adapter tests with mocked JPA or TestContainers. Controllers → MockMvc tests. User-visible flows → e2e tests under `src/test/java/.../e2e/`.
- **Migrations**: add a new versioned Flyway file (`V{N}__description.sql`). Never edit an applied migration.
- **Comments**: document *why*, not *what*. Javadoc for public APIs; skip the obvious.

## Commit messages

Use [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` — new capability
- `fix:` — bug fix
- `chore:` — tooling, dependencies, housekeeping
- `test:` — adding or updating tests
- `refactor:` — non-behavioral code changes
- `docs:` — documentation only

Keep the subject line under 72 characters and explain the **why** in the body when it isn't obvious from the diff.

## Pull request process

1. Fork and create a feature branch off `main`.
2. Make your change and add tests. **Run `./mvnw test` locally before pushing** — e2e tests require Docker.
3. Open a PR with a clear description: what you changed, why, and how to test it.
4. Expect review feedback. Address it with new commits (don't force-push during review unless asked).
5. Once approved and CI is green, your change will be squashed or merged.

## Security issues

Please don't file security bugs as public issues. See [SECURITY.md](SECURITY.md) for the private reporting process.
