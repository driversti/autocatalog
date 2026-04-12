---
name: API quirks
description: Known AutoCatalog API behaviors that affect data-loading workflow
type: project
---

## Duplicate prevention (as of 2026-04-11, migration V7)

All create endpoints now return **409 Conflict** on duplicate natural keys:

- `POST /api/v1/makes` → 409 on duplicate `name`.
- `POST /api/v1/models` → 409 on duplicate `(makeId, name)`.
- `POST /api/v1/generations` → 409 on duplicate `(modelId, name)`.
- `POST /api/v1/engines` → 409 on duplicate `code`.
- `POST /api/v1/transmissions` → 409 on duplicate `(type, gearCount)`.
- `POST /api/v1/variants` → 409 on duplicate `(bodyId, transmissionId, drivetrain)`.
- `POST /api/v1/bodies` → 409 on duplicate `(generationId, bodyStyle)`.

**Why:** V6 and V7 migrations added DB unique constraints plus service-level
`existsBy…` guards. A `DataIntegrityViolationException` fallback handler also
maps any constraint violation to 409 instead of leaking as 500.

**How to apply:** It's safe to POST without a GET-before-POST check — duplicates
produce a clean 409 you can treat as "already exists, move on". Pre-checking
is still fine as an optimization to skip pointless POSTs, but no longer
required for correctness. **Historical context:** before V6, the generations
endpoint silently created duplicate rows (VW Golf Mk1 was duplicated this way
on 2026-04-11 before the fix landed).

## Slug collision across makes (car_models)

Before V7, the `slug` column on `car_models` was globally unique, which
incorrectly rejected same-named models across different makes (e.g. Ford
Sierra vs GMC Sierra). V7 replaced the global constraint with
`UNIQUE (make_id, slug)` + `UNIQUE (make_id, name)`. If a data-load spans
multiple brands with overlapping names, it now works — but only against V7+.

## Variant uniqueness constraint blocks real-world engine variants

**Schema gap (as of 2026-04-11):** Variant is unique on `(bodyId, transmissionId, drivetrain)`.
This blocks creating two variants that differ only in engine (e.g., Corolla E170 1.6 MT6 FWD
and 1.8 MT6 FWD are the same body+gearbox+drivetrain combo). 

**Workaround:** Link multiple engines to the same Variant row using
`POST /api/v1/variants/{id}/engines/{engineId}`. The Variant then represents
"this body+transmission+drivetrain combo is available with any of these engines".
This is semantically imprecise but is the only option until the schema adds
engine as part of the uniqueness key or splits Variant into Variant+EngineConfig.

**Hybrid E-CVT conflict:** Toyota hybrids use an E-CVT (power-split device) which
the API models as `CVT gearCount:1` — same as a regular CVT. If the same body
already has a petrol CVT FWD variant, the hybrid CVT FWD variant gets a 409.
Workaround: use AWD drivetrain for the hybrid AWD-i variant (which is the more
common hybrid config anyway) or link the hybrid engines to the existing CVT variant.

## EngineRequest primitive fields

`torqueNm`, `powerKw`, `displacementCc`, `cylinderCount` are all primitive `int`
fields in the EngineRequest record — they CANNOT be omitted or set to null.
For electric motors: use `displacementCc: 1` (minimum valid value), `cylinderCount: 1`.
Omitting torqueNm causes a 400 error with Jackson deserialization message.

## GET /api/v1/models requires makeId query param

`GET /api/v1/models` returns 400 Bad Request if `makeId` query parameter is missing.
Always use `GET /api/v1/models?makeId={uuid}`.

## GET /api/v1/bodies requires generationId query param

`GET /api/v1/bodies?generationId={uuid}` — generationId is required.

## GET /api/v1/variants requires bodyId query param

`GET /api/v1/variants?bodyId={uuid}` — bodyId is required.
