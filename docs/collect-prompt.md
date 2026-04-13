Read CLAUDE.md carefully before making any API calls.

## Task
Verify the API is running, then collect **exhaustive** data for the manufacturer below.

---

## Manufacturer
Name:    Audi
Country: Germany

## Models to collect
- A1

---

## Completeness requirement (CRITICAL)

This is an automotive encyclopedia. Partial data is worse than no data.

**For EVERY model listed above, you MUST collect EVERY generation ever produced** —
from the very first to the current one. Do not skip older or discontinued generations.
Example: VW Golf has generations Mk1 (1974), Mk2 (1983), Mk3 (1991), Mk4 (1997),
Mk5 (2003), Mk6 (2008), Mk7 (2012), Mk8 (2019) — all eight must be collected,
not just the last two.

**For EVERY generation, collect EVERY body style** it was sold in (hatchback, sedan,
wagon, convertible, etc.). Do not pick only the most common one.

**For EVERY body, collect EVERY powertrain + transmission + drivetrain combination**
that was offered. Include all petrol, diesel, hybrid, PHEV, EV, and LPG variants.
Include performance variants (GTI, R, RS, AMG, M, etc.).

### How to research thoroughly
1. Start with the model's Wikipedia page — it lists all generations with year ranges.
2. For each generation, open its dedicated Wikipedia article or section for the full
   engine/transmission table.
3. Cross-reference with manufacturer press materials or auto databases.
4. If a generation had a facelift with new engines, include those engines too —
   they belong to the same generation (do not create a separate generation for facelifts
   unless the industry universally treats it as a distinct generation).

### Work model-by-model, generation-by-generation
Process ONE model completely before moving to the next.
Within each model, process ONE generation completely before moving to the next.
This prevents skipping and ensures nothing is missed.

## Loading instructions

### Powertrain concept (CRITICAL — new schema)

A **Powertrain** represents a specific engine configuration installed in ONE car.
It is NOT a list of alternative engine options — it is the exact engine(s) that
power a single vehicle.

- **ICE car** → one Powertrain with one engine (type: ICE)
- **Pure EV** → one Powertrain with one electric motor (type: ELECTRIC)
- **EV with dual motors** → one Powertrain with two electric motors (type: ELECTRIC),
  set totalPowerKw to combined system power
- **Self-charging hybrid** → one Powertrain with ICE + electric motor (type: HYBRID),
  set totalPowerKw to combined system power
- **PHEV** → one Powertrain with ICE + electric motor (type: PHEV),
  set totalPowerKw to combined system power
- **Mild hybrid** → one Powertrain with ICE + electric motor (type: MHEV),
  set totalPowerKw to combined system power

**Each distinct engine option gets its own Powertrain.**
A Golf with a 1.6L and a 1.8L engine = TWO separate Powertrains, not one.

**NEVER put alternative engine options into the same Powertrain.**
Only combine engines that physically coexist and work together in the same car
(e.g., ICE + electric motor in a hybrid).

### Loading order (strict — respect FK deps)
1. Make
2. Model (with makeId)
3. Engines (shared pool — create once, reuse by ID everywhere)
4. Transmissions (shared pool — create once, reuse)
5. Powertrains (references engines by ID — create one per engine configuration)
6. Generation (with modelId)
7. Body (with generationId + dimensions)
8. Variant (with bodyId + powertrainId + transmissionId + drivetrain)

Always GET before POST to avoid duplicates (HTTP 409 = skip and continue).

---

## Domain model reference

### Make
```json
{ "name": "Toyota", "country": "Japan" }
```

### Model
```json
{ "makeId": "<uuid>", "name": "Corolla" }
```

### Generation
```json
{ "modelId": "<uuid>", "name": "E210", "yearFrom": 2018, "yearTo": null }
```
yearTo = null means currently in production.

### Engine
```json
{
  "code": "2ZR-FXS",
  "name": "1.8 Dynamic Force",
  "fuelType": "PETROL",
  "displacementCc": 1798,
  "powerKw": 72,
  "torqueNm": 142,
  "cylinderCount": 4,
  "configuration": "I4"
}
```
Electric motor example (nullable fields):
```json
{
  "code": "1MM",
  "name": "Front Motor",
  "fuelType": "ELECTRIC",
  "displacementCc": null,
  "powerKw": 53,
  "torqueNm": null,
  "cylinderCount": null,
  "configuration": null
}
```

### Transmission
```json
{ "type": "CVT", "gearCount": 1 }
```
For SINGLE_SPEED (EV): `{ "type": "SINGLE_SPEED", "gearCount": 1 }`

### Powertrain
ICE example (single engine):
```json
{
  "name": "1.8L GX GTI",
  "type": "ICE",
  "totalPowerKw": 82,
  "totalTorqueNm": 145,
  "engineEntries": [
    { "engineId": "<uuid>", "role": "PRIMARY" }
  ]
}
```
Hybrid example (ICE + electric motor):
```json
{
  "name": "2.5L Dynamic Force Hybrid",
  "type": "HYBRID",
  "totalPowerKw": 160,
  "totalTorqueNm": null,
  "engineEntries": [
    { "engineId": "<uuid-ice>", "role": "PRIMARY" },
    { "engineId": "<uuid-electric>", "role": "SECONDARY_ELECTRIC" }
  ]
}
```
Dual-motor EV example:
```json
{
  "name": "Dual Motor AWD",
  "type": "ELECTRIC",
  "totalPowerKw": 250,
  "totalTorqueNm": 580,
  "engineEntries": [
    { "engineId": "<uuid-front>", "role": "PRIMARY" },
    { "engineId": "<uuid-rear>", "role": "SECONDARY_ELECTRIC" }
  ]
}
```

**Powertrain rules:**
- `name` — human-readable, e.g. "2.0 TFSI", "1.6 TDI", "e-tron 50"
- `type` — one of: ICE, ELECTRIC, HYBRID, MHEV, PHEV
- `totalPowerKw` — for single engine: same as engine's powerKw.
  For multi-engine: the combined system output (NOT the sum of individual engines
  unless that is the real combined output)
- `totalTorqueNm` — combined torque, or null if not available
- `engineEntries` — list of engines. `role` is PRIMARY or SECONDARY_ELECTRIC
- One engine per Powertrain for ICE cars. Multiple ONLY for hybrids/dual-motor EVs.

### Body
```json
{
  "generationId": "<uuid>",
  "bodyStyle": "SEDAN",
  "lengthMm": 4630,
  "widthMm": 1780,
  "heightMm": 1435,
  "wheelbaseMm": 2700,
  "trunkVolumeLiters": 470
}
```
All dimension fields are optional — use null if not found.

### Variant
```json
{
  "bodyId": "<uuid>",
  "powertrainId": "<uuid>",
  "transmissionId": "<uuid>",
  "drivetrain": "FWD",
  "curbWeightKg": 1455,
  "groundClearanceMm": 132
}
```
- `curbWeightKg` — required integer, not nullable. Always provide a real value.
- `groundClearanceMm` — optional, use null if not found.
- Markets can be added separately via `POST /api/v1/variants/{id}/markets/{code}`
  using ISO 3166-1 alpha-2 codes (e.g., "DE", "US", "JP").

---

## Allowed enum values

FuelType:        PETROL | DIESEL | HYBRID | PLUG_IN_HYBRID | ELECTRIC | HYDROGEN | LPG
PowertrainType:  ICE | ELECTRIC | HYBRID | MHEV | PHEV
EngineRole:      PRIMARY | SECONDARY_ELECTRIC
TransmissionType: MANUAL | AUTOMATIC | DCT | CVT | SINGLE_SPEED
BodyStyle:       SEDAN | HATCHBACK | WAGON | COUPE | CONVERTIBLE | SUV | CROSSOVER | MINIVAN | PICKUP | VAN
Drivetrain:      FWD | RWD | AWD | FOUR_WD

---

## Unit conversions
- HP → kW : kW = round(HP / 1.35962)
- liters → cc : cc = liters * 1000
- Always store metric units

---

## Data quality rules
- Verify values from at least 2 independent sources
- If a value is uncertain — set null, never guess
- Engine code must be the official manufacturer code (e.g. "2GR-FE", "N57D30", "EA888")
- yearFrom >= 1886
- powerKw > 0 always
- Do not create a Variant if you cannot find at least: powertrain + transmission + drivetrain
- curbWeightKg is a required integer field (not nullable) — always provide a real value

## Completeness checklist (verify before finishing each model)
Before moving to the next model, verify:
- [ ] All generations from first to latest are present (check Wikipedia for the full list)
- [ ] Every body style per generation is covered (hatchback, sedan, wagon, coupe, convertible, etc.)
- [ ] Every engine option has its own Powertrain (petrol, diesel, hybrid, EV, performance trims)
- [ ] Every transmission option is represented (manual, automatic, DCT, CVT, single-speed)
- [ ] Every drivetrain option is represented (FWD, RWD, AWD where applicable)
If anything is missing, go back and add it before proceeding.

---

## HTTP status handling
| Status | Meaning           | Action                          |
|--------|-------------------|---------------------------------|
| 201    | Created           | continue                        |
| 409    | Already exists    | skip, reuse existing ID         |
| 404    | Dependency missing| create it first, then retry     |
| 400    | Bad request       | log reason + skip, do not retry |

---

## Final report
When finished, print a summary:
=== Collection complete ===
Make:          {{MAKE_NAME}}
Models:        X
Generations:   X (list per model, e.g. "Golf: Mk1, Mk2, Mk3, Mk4, Mk5, Mk6, Mk7, Mk8")
Bodies:        X
Engines:       X (created) + X (reused)
Transmissions: X (created) + X (reused)
Powertrains:   X (created) + X (reused)
Variants:      X
Skipped:       X items (duplicates)
Failed:        X items — <reasons>

Per-model breakdown (REQUIRED):
| Model | Generations | Bodies | Powertrains | Variants |
|-------|------------|--------|-------------|----------|
| ...   | ...        | ...    | ...         | ...      |
