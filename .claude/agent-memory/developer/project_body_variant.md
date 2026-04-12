---
name: Body/Variant implementation
description: Body and Variant aggregates; final domain model decisions after git history reset in April 2026
type: project
---

Body and Variant aggregates implemented in April 2026. Git history reset to a single clean commit `d059fe9`. Only V1 migration exists.

**Why:** Domain model finalized — Body holds body style + ALL physical dimensions including groundClearanceMm; Variant overrides groundClearanceMm when different (e.g. sport suspension).

**How to apply:**
- `Body`: generationId, bodyStyle (unique per generation), lengthMm/widthMm/heightMm/wheelbaseMm (required ints), trunkVolumeLitres/groundClearanceMm (nullable Integer). `create()` takes 6 params (no optionals); set via `setTrunkVolumeLitres()` / `setGroundClearanceMm()`.
- `Variant`: bodyId, transmissionId, drivetrain, curbWeightKg (required); engineIds (Set, M:N), groundClearanceMm/systemPowerKw/markets (optional). Uniqueness on (body_id, transmission_id, drivetrain).
- `Engine`: `displacementCc` and `torqueNm` are now `Integer` (nullable) — valid for electric motors. `powerKw` stays `int` (required). `create()` validates: null is allowed but if provided must be > 0.
- BodyStyle enum in `domain/body/`, Drivetrain enum in `domain/variant/`.
- All in single V1 migration — no V2..V10 exist.
