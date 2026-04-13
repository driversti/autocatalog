# ADR-001: ImageSource skips domain representation

**Date:** 2026-04-12  
**Status:** Accepted

## Decision
`ImageSourceService` uses `api` DTOs and `infrastructure` JPA repository directly, without a separate domain or application model in between.

## Reason
ImageSource is a metadata/lookup table with no business rules, invariants, or lifecycle. Creating `ImageSourceCommand` and `ImageSourceResult` would add 2 empty wrapper classes with zero value.

## Consequence
`application.image` technically knows about `api` and `infrastructure`. Acceptable until ImageSource gains real business logic — at that point, introduce a proper domain model.
