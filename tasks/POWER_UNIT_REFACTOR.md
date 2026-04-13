# Task: Refactor Powertrain — introduce PowerUnit composition

## Context

We decided to redesign how engines and motors are modelled in AutoCatalog.
The current schema has a single `engine` table with a `code` column that is
`NOT NULL UNIQUE`. This breaks for:

- **Electric motors** — OEMs (including Mercedes) do not publish official codes
  for e-motors. The schema cannot represent them without fake placeholder values.
- **Fuel cell vehicles (FCEV)** — require a `FuelCellStack` component in addition
  to an electric motor. The current `Powertrain` has no place for it.
- **BEV** — no combustion engine at all; `engine_id` on `Powertrain` cannot be
  made mandatory.

Adding new FK columns to `Powertrain` for every new propulsion type is not
scalable. We need a composition approach instead.

---

## Target architecture

### Principle: Table Per Concrete Type with a shared discriminator row

```
power_unit          ← shared identity + discriminator (unit_type)
  ├── engine        ← ICE-specific attributes (shares PK with power_unit)
  ├── electric_motor← e-motor attributes   (shares PK with power_unit)
  └── fuel_cell_stack← FCEV attributes     (shares PK with power_unit)

powertrain_unit     ← join table: Powertrain ↔ PowerUnit + role
```

`Powertrain` no longer has direct `engine_id` / `electric_motor_id` FK columns.
Instead, it links to N `PowerUnit` records via `powertrain_unit` with a `role`
column (PRIMARY, SECONDARY, GENERATOR).

### Real-world configurations

| Vehicle type | powertrain_unit records |
|---|---|
| ICE (A180) | engine → PRIMARY |
| MHEV (A180 48V) | engine → PRIMARY, electric_motor → SECONDARY |
| PHEV (A250e) | engine → PRIMARY, electric_motor → SECONDARY |
| BEV (EQA 250) | electric_motor → PRIMARY |
| BEV AWD (EQS 4MATIC) | electric_motor → PRIMARY, electric_motor → SECONDARY |
| FCEV (Mirai) | fuel_cell_stack → GENERATOR, electric_motor → PRIMARY |

---

## Steps

### 1. Read the codebase first

Before writing any code:
- Read `CLAUDE.md` — it describes all architectural decisions and invariants
- Read **all existing Flyway migration files** to understand the current schema
- Read all existing Java entities in the `domain` layer
- Read `Powertrain` entity and its current relationships
- Read existing `Engine` entity — note that `code` is `NOT NULL UNIQUE`
- Read `PowertrainService` and `PowertrainController` to understand existing API

---

### 2. Write a new Flyway migration

Create the next migration file (check the highest existing version and increment).

```sql
-- power_unit: shared identity row, one per any kind of propulsion unit
CREATE TABLE power_unit (
    id        BIGSERIAL PRIMARY KEY,
    unit_type VARCHAR(20) NOT NULL   -- ICE | ELECTRIC | FUEL_CELL
);

-- engine: ICE-specific, shares PK with power_unit
-- code stays NOT NULL UNIQUE — invariant preserved for ICE only
CREATE TABLE engine (
    id              BIGINT PRIMARY KEY REFERENCES power_unit(id) ON DELETE CASCADE,
    code            VARCHAR(20)  NOT NULL UNIQUE,
    displacement_cc INTEGER,
    cylinder_count  SMALLINT,
    fuel_type       VARCHAR(30),     -- GASOLINE | DIESEL | LPG | CNG | HYDROGEN_ICE
    power_kw        SMALLINT,
    torque_nm       SMALLINT,
    compression_ratio NUMERIC(4,1),
    turbo           BOOLEAN NOT NULL DEFAULT FALSE
);

-- electric_motor: e-motor-specific, shares PK with power_unit
-- label is descriptive (no OEM code exists for most e-motors)
CREATE TABLE electric_motor (
    id         BIGINT PRIMARY KEY REFERENCES power_unit(id) ON DELETE CASCADE,
    label      VARCHAR(100) NOT NULL,   -- e.g. "Rear EM 75kW"
    power_kw   SMALLINT,
    torque_nm  SMALLINT,
    motor_type VARCHAR(30)              -- PERMANENT_MAGNET | INDUCTION | WOUND_ROTOR
);

-- fuel_cell_stack: FCEV-specific, shares PK with power_unit
CREATE TABLE fuel_cell_stack (
    id                BIGINT PRIMARY KEY REFERENCES power_unit(id) ON DELETE CASCADE,
    label             VARCHAR(100) NOT NULL,
    peak_power_kw     SMALLINT,
    hydrogen_tank_kg  NUMERIC(4,1)
);

-- powertrain_unit: composition join table
-- replaces direct engine_id FK on powertrain
CREATE TABLE powertrain_unit (
    powertrain_id BIGINT NOT NULL REFERENCES powertrain(id) ON DELETE CASCADE,
    power_unit_id BIGINT NOT NULL REFERENCES power_unit(id),
    role          VARCHAR(20) NOT NULL,  -- PRIMARY | SECONDARY | GENERATOR
    PRIMARY KEY (powertrain_id, power_unit_id)
);

-- Remove the old direct engine_id FK from powertrain (if it exists)
-- Check the current schema; adapt accordingly.
ALTER TABLE powertrain DROP COLUMN IF EXISTS engine_id;
```

> **Note on `engine` table**: if the existing `engine` table already exists
> (from a previous migration), you need to migrate its data into the new
> `power_unit + engine` structure, not recreate it from scratch.
> Read the existing migrations carefully and decide whether to:
> (a) migrate existing engine rows into power_unit + engine, or
> (b) consolidate everything into a single V1 if the database is still empty.
> Check the actual DB state before deciding.

The `powertrain` table should also keep:
```sql
-- These stay on powertrain (aggregate-level, not per-unit)
drivetrain_type       VARCHAR(20) NOT NULL,  -- ICE | MHEV | HEV | PHEV | BEV | FCEV
combined_power_hp     SMALLINT,
combined_torque_nm    SMALLINT,
battery_capacity_kwh  NUMERIC(5,1),          -- nullable; set for PHEV/BEV/FCEV
electric_range_km     SMALLINT               -- nullable; set for PHEV/BEV/FCEV
```

Add a CHECK constraint:
```sql
ALTER TABLE powertrain ADD CONSTRAINT chk_battery_range
    CHECK (
        (battery_capacity_kwh IS NULL) = (electric_range_km IS NULL)
        OR battery_capacity_kwh IS NOT NULL
    );
```

---

### 3. Java domain entities

Create or update these entities in the `domain` layer following existing
conventions (check CLAUDE.md for DDD layer rules, value object style, etc.).

#### `PowerUnit.java`
```java
@Entity
@Table(name = "power_unit")
public class PowerUnit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false, length = 20)
    private PowerUnitType unitType;

    public enum PowerUnitType {
        ICE, ELECTRIC, FUEL_CELL
    }
}
```

#### `Engine.java` (updated — now shares PK with `power_unit`)
```java
@Entity
@Table(name = "engine")
public class Engine {

    @Id
    private Long id;   // shared PK — no @GeneratedValue here

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id")
    private PowerUnit unit;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    private Integer displacementCc;
    private Short cylinderCount;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private Short powerKw;
    private Short torqueNm;
    private BigDecimal compressionRatio;
    private boolean turbo;

    public enum FuelType {
        GASOLINE, DIESEL, LPG, CNG, HYDROGEN_ICE
    }

    // Factory method — always create the PowerUnit together
    public static Engine create(String code, ...) {
        var unit = new PowerUnit();
        unit.setUnitType(PowerUnit.PowerUnitType.ICE);
        var engine = new Engine();
        engine.setUnit(unit);
        engine.setCode(code);
        // ... set other fields
        return engine;
    }
}
```

#### `ElectricMotor.java` (new entity)
```java
@Entity
@Table(name = "electric_motor")
public class ElectricMotor {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id")
    private PowerUnit unit;

    @Column(nullable = false, length = 100)
    private String label;

    private Short powerKw;
    private Short torqueNm;

    @Enumerated(EnumType.STRING)
    private MotorType motorType;

    public enum MotorType {
        PERMANENT_MAGNET, INDUCTION, WOUND_ROTOR, UNKNOWN
    }
}
```

#### `FuelCellStack.java` (new entity)
```java
@Entity
@Table(name = "fuel_cell_stack")
public class FuelCellStack {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id")
    private PowerUnit unit;

    @Column(nullable = false, length = 100)
    private String label;

    private Short peakPowerKw;
    private BigDecimal hydrogenTankKg;
}
```

#### `PowertrainUnit.java` (new join entity)
```java
@Entity
@Table(name = "powertrain_unit")
public class PowertrainUnit {

    @EmbeddedId
    private PowertrainUnitId id = new PowertrainUnitId();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("powertrainId")
    @JoinColumn(name = "powertrain_id")
    private Powertrain powertrain;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("powerUnitId")
    @JoinColumn(name = "power_unit_id")
    private PowerUnit powerUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UnitRole role;

    public enum UnitRole { PRIMARY, SECONDARY, GENERATOR }

    @Embeddable
    public static class PowertrainUnitId implements Serializable {
        private Long powertrainId;
        private Long powerUnitId;
        // equals + hashCode required
    }
}
```

#### `Powertrain.java` (updated)

Remove `engine` field / FK. Add:
```java
@OneToMany(mappedBy = "powertrain", cascade = CascadeType.ALL, orphanRemoval = true)
private List<PowertrainUnit> units = new ArrayList<>();

@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private DrivetrainType drivetrainType;   // ICE | MHEV | HEV | PHEV | BEV | FCEV

private Integer combinedPowerHp;
private Integer combinedTorqueNm;
private BigDecimal batteryCapacityKwh;
private Integer electricRangeKm;

public enum DrivetrainType { ICE, MHEV, HEV, PHEV, BEV, FCEV }
```

Follow existing conventions for value objects (e.g. `PowertrainId`) if used
elsewhere in the codebase.

---

### 4. Repositories

Create Spring Data repositories for all new entities:
- `PowerUnitRepository`
- `ElectricMotorRepository`
- `FuelCellStackRepository`

Update `EngineRepository` if needed (method signatures may change since `Engine`
now has a shared PK via `@MapsId`).

---

### 5. Application layer — service and API

#### New endpoints to add

```
POST   /api/v1/electric-motors          — create e-motor
GET    /api/v1/electric-motors/{id}
PATCH  /api/v1/electric-motors/{id}/label
PATCH  /api/v1/electric-motors/{id}/specs
DELETE /api/v1/electric-motors/{id}     — guarded (409 if referenced)

POST   /api/v1/fuel-cell-stacks
GET    /api/v1/fuel-cell-stacks/{id}
DELETE /api/v1/fuel-cell-stacks/{id}    — guarded
```

#### Updated Powertrain endpoints

`POST /api/v1/powertrains` request body must change to accept a list of
power units instead of a single `engineId`:

```json
{
  "drivetrainType": "PHEV",
  "combinedPowerHp": 218,
  "combinedTorqueNm": 450,
  "batteryCapacityKwh": 15.6,
  "electricRangeKm": 80,
  "transmissionId": "...",
  "units": [
    { "powerUnitId": "...", "role": "PRIMARY" },
    { "powerUnitId": "...", "role": "SECONDARY" }
  ]
}
```

Follow the existing `XxxRequest` / `XxxResponse` / `XxxService` / `XxxController`
conventions found in CLAUDE.md and the existing codebase.

#### Delete guards

- `ElectricMotor` DELETE: 409 if any `powertrain_unit` references its `power_unit_id`
- `FuelCellStack` DELETE: same
- `Engine` DELETE: same (check `powertrain_unit`, not `powertrain.engine_id`)

---

### 6. Update CLAUDE.md

Add a section describing the PowerUnit pattern:

```markdown
## PowerUnit composition pattern

Propulsion units are modelled as a two-level structure:
- `power_unit` — shared identity row with a `unit_type` discriminator
- `engine` / `electric_motor` / `fuel_cell_stack` — concrete tables sharing
  the PK via `@MapsId` (Table Per Concrete Type, no JPA inheritance)

`Powertrain` links to N `PowerUnit` rows via the `powertrain_unit` join table
with a `role` column (PRIMARY | SECONDARY | GENERATOR).

### Invariants
- `engine.code` is NOT NULL UNIQUE — preserved for ICE only
- `electric_motor.label` is NOT NULL — descriptive, no OEM code required
- Every `Powertrain` must have at least one `PowertrainUnit` (enforced in service)
- `battery_capacity_kwh` and `electric_range_km` must both be set or both null

### Adding a new propulsion type
1. Add a new value to `PowerUnit.PowerUnitType` enum
2. Create a new concrete table + entity sharing the PK via `@MapsId`
3. Add repository + service + controller
4. No changes to `Powertrain` or `powertrain_unit` tables required
```

---

### 7. Tests

Write unit tests for:
- `PowertrainService` — creating a PHEV powertrain with two units
- `ElectricMotorService` — delete guard (409 when referenced)
- Migration integrity — Flyway migrates cleanly on empty DB

---

## Definition of done

- [ ] Flyway migration runs cleanly on an empty database (`./mvnw spring-boot:run`)
- [ ] `Engine.code` is still `NOT NULL UNIQUE` at DB level
- [ ] BEV powertrain can be created with no `engine` reference
- [ ] PHEV powertrain can be created with both engine and electric_motor units
- [ ] FCEV powertrain can be created with fuel_cell_stack + electric_motor
- [ ] All new endpoints return correct HTTP status codes
- [ ] Delete guards return `409 Conflict` with a human-readable message
- [ ] CLAUDE.md updated with PowerUnit section
- [ ] No compilation errors, no Flyway checksum conflicts
