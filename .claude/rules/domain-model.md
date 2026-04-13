# AutoCatalog Domain Model

## Architecture layers
```
domain/         — pure Java, zero Spring/JPA annotations
application/    — use cases, services (no Spring annotations)
infrastructure/ — JPA entities, adapters, ApplicationConfig (Composition Root)
api/            — REST controllers, DTOs, exception handler
```

## Key patterns
- Domain classes use static factory `create()` and `reconstitute()` methods
- No setters in domain — state changes via explicit methods
- Repository interfaces live in domain, implementations in infrastructure
- Services are wired via `@Bean` in `ApplicationConfig`, not `@Service`
