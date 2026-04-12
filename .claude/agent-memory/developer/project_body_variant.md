---
name: Body/Variant implementation
description: Body and Variant aggregates added in V10 migration; Generation collection tables dropped; enums moved to domain/body and domain/variant packages
type: project
---

Body and Variant aggregates implemented in April 2026. Generation simplified to name/yearFrom/yearTo/modelId only.

**Why:** Domain model decision documented in CLAUDE.md — Body holds body style + dimensions, Variant holds technical config (engines M:N, transmission, drivetrain, weight, markets).

**How to apply:** BodyStyle enum is in `domain/body/`, Drivetrain enum is in `domain/variant/`. Old `BodyStyleType` and `DrivetrainType` in `domain/generation/` are orphaned (kept for safety, not referenced). V10 migration drops generation_engines, generation_transmissions, generation_body_styles, generation_drivetrains and creates bodies, variants, variant_engines, variant_markets tables.
