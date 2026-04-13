-- =============================================================================
-- V1__initial_schema.sql
-- Full initial schema for AutoCatalog — consolidated with PowerUnit composition.
-- Tables are created in FK-dependency order.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- makes
-- -----------------------------------------------------------------------------
CREATE TABLE makes
(
  id      UUID         PRIMARY KEY,
  name    VARCHAR(100) NOT NULL,
  country VARCHAR(100) NOT NULL,
  slug    VARCHAR(100) NOT NULL,
  CONSTRAINT uk_makes_name UNIQUE (name),
  CONSTRAINT uk_makes_slug UNIQUE (slug)
);

-- -----------------------------------------------------------------------------
-- car_models
-- -----------------------------------------------------------------------------
CREATE TABLE car_models
(
  id      UUID         PRIMARY KEY,
  make_id UUID         NOT NULL REFERENCES makes (id),
  name    VARCHAR(100) NOT NULL,
  slug    VARCHAR(100) NOT NULL,
  CONSTRAINT uk_car_models_make_name UNIQUE (make_id, name),
  CONSTRAINT uk_car_models_make_slug UNIQUE (make_id, slug)
);

CREATE TABLE car_model_relations
(
  from_model_id UUID        NOT NULL REFERENCES car_models (id),
  to_model_id   UUID        NOT NULL REFERENCES car_models (id),
  type          VARCHAR(50) NOT NULL,
  note          TEXT,
  PRIMARY KEY (from_model_id, to_model_id, type)
);

-- -----------------------------------------------------------------------------
-- transmissions  (shared lookup: unique on type + gear_count)
-- -----------------------------------------------------------------------------
CREATE TABLE transmissions
(
  id         UUID        PRIMARY KEY,
  type       VARCHAR(20) NOT NULL,
  gear_count INT         NOT NULL CHECK (gear_count BETWEEN 1 AND 12),
  CONSTRAINT uk_transmissions_type_gear_count UNIQUE (type, gear_count)
);

-- -----------------------------------------------------------------------------
-- generations
-- -----------------------------------------------------------------------------
CREATE TABLE generations
(
  id        UUID        PRIMARY KEY,
  model_id  UUID        NOT NULL REFERENCES car_models (id),
  name      VARCHAR(50) NOT NULL,
  year_from INT         NOT NULL,
  year_to   INT,
  CONSTRAINT uk_generations_model_name UNIQUE (model_id, name)
);

-- -----------------------------------------------------------------------------
-- bodies  (one body style per generation; dimensions shared across variants)
-- wheelbase_mm is nullable for new models without full specs
-- -----------------------------------------------------------------------------
CREATE TABLE bodies
(
  id                  UUID        PRIMARY KEY,
  generation_id       UUID        NOT NULL REFERENCES generations (id),
  body_style          VARCHAR(20) NOT NULL,
  length_mm           INT         NOT NULL CHECK (length_mm > 0),
  width_mm            INT         NOT NULL CHECK (width_mm > 0),
  height_mm           INT         NOT NULL CHECK (height_mm > 0),
  wheelbase_mm        INT         CHECK (wheelbase_mm > 0),
  trunk_volume_litres INT         CHECK (trunk_volume_litres > 0),
  ground_clearance_mm INT         CHECK (ground_clearance_mm > 0),
  CONSTRAINT uk_bodies_generation_style UNIQUE (generation_id, body_style)
);

-- -----------------------------------------------------------------------------
-- power_unit: shared identity row — one per any kind of propulsion unit.
-- Engine, ElectricMotor, and FuelCellStack all share a PK from this table.
-- unit_type discriminator: ICE | ELECTRIC | FUEL_CELL
-- -----------------------------------------------------------------------------
CREATE TABLE power_unit
(
  id        BIGSERIAL   PRIMARY KEY,
  unit_type VARCHAR(20) NOT NULL
);

-- -----------------------------------------------------------------------------
-- engines  (ICE-specific; shares PK with power_unit via FK)
-- code is NOT NULL UNIQUE — invariant preserved for ICE units only
-- -----------------------------------------------------------------------------
CREATE TABLE engines
(
  id                BIGINT       PRIMARY KEY REFERENCES power_unit (id) ON DELETE CASCADE,
  code              VARCHAR(50)  NOT NULL,
  name              VARCHAR(150),
  fuel_type         VARCHAR(30)  NOT NULL,
  displacement_cc   INT          CHECK (displacement_cc IS NULL OR displacement_cc > 0),
  power_kw          INT          NOT NULL CHECK (power_kw > 0),
  torque_nm         INT          CHECK (torque_nm IS NULL OR torque_nm > 0),
  cylinder_count    INT,
  configuration     VARCHAR(20),
  system_power_kw   INT          CHECK (system_power_kw IS NULL OR system_power_kw > 0),
  compression_ratio NUMERIC(4,1),
  turbo             BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT uk_engines_code UNIQUE (code)
);

-- -----------------------------------------------------------------------------
-- electric_motor  (e-motor-specific; shares PK with power_unit)
-- label is descriptive — no OEM code exists for most e-motors
-- -----------------------------------------------------------------------------
CREATE TABLE electric_motor
(
  id         BIGINT       PRIMARY KEY REFERENCES power_unit (id) ON DELETE CASCADE,
  label      VARCHAR(100) NOT NULL,
  power_kw   SMALLINT,
  torque_nm  SMALLINT,
  motor_type VARCHAR(30)
);

-- -----------------------------------------------------------------------------
-- fuel_cell_stack  (FCEV-specific; shares PK with power_unit)
-- -----------------------------------------------------------------------------
CREATE TABLE fuel_cell_stack
(
  id               BIGINT       PRIMARY KEY REFERENCES power_unit (id) ON DELETE CASCADE,
  label            VARCHAR(100) NOT NULL,
  peak_power_kw    SMALLINT,
  hydrogen_tank_kg NUMERIC(4,1)
);

-- -----------------------------------------------------------------------------
-- powertrains  (aggregate-level propulsion configuration)
-- drivetrain_type: ICE | MHEV | HEV | PHEV | BEV | FCEV
-- battery_capacity_kwh and electric_range_km must both be set or both null
-- -----------------------------------------------------------------------------
CREATE TABLE powertrains
(
  id                   UUID         PRIMARY KEY,
  name                 VARCHAR(200) NOT NULL,
  drivetrain_type      VARCHAR(20)  NOT NULL,
  combined_power_hp    SMALLINT,
  combined_torque_nm   SMALLINT,
  battery_capacity_kwh NUMERIC(5,1),
  electric_range_km    SMALLINT,
  CONSTRAINT uk_powertrains_name UNIQUE (name),
  CONSTRAINT chk_battery_range CHECK (
    (battery_capacity_kwh IS NULL) = (electric_range_km IS NULL)
    OR battery_capacity_kwh IS NOT NULL
  )
);

-- -----------------------------------------------------------------------------
-- powertrain_unit: composition join table  (Powertrain <-> PowerUnit with role)
-- Replaces the old powertrain_engines join table.
-- role: PRIMARY | SECONDARY | GENERATOR
-- -----------------------------------------------------------------------------
CREATE TABLE powertrain_unit
(
  powertrain_id UUID        NOT NULL REFERENCES powertrains (id) ON DELETE CASCADE,
  power_unit_id BIGINT      NOT NULL REFERENCES power_unit (id),
  role          VARCHAR(20) NOT NULL,
  PRIMARY KEY (powertrain_id, power_unit_id)
);

-- -----------------------------------------------------------------------------
-- variants  (one technical configuration per body)
-- -----------------------------------------------------------------------------
CREATE TABLE variants
(
  id                  UUID        PRIMARY KEY,
  body_id             UUID        NOT NULL REFERENCES bodies (id),
  powertrain_id       UUID        NOT NULL REFERENCES powertrains (id),
  transmission_id     UUID        NOT NULL REFERENCES transmissions (id),
  drivetrain          VARCHAR(10) NOT NULL,
  ground_clearance_mm INT         CHECK (ground_clearance_mm > 0),
  curb_weight_kg      INT         NOT NULL CHECK (curb_weight_kg > 0),
  CONSTRAINT uk_variants_body_powertrain_transmission_drivetrain
    UNIQUE (body_id, powertrain_id, transmission_id, drivetrain)
);

-- -----------------------------------------------------------------------------
-- variant_markets  (ISO 3166-1 alpha-2 codes per variant)
-- -----------------------------------------------------------------------------
CREATE TABLE variant_markets
(
  variant_id UUID       NOT NULL REFERENCES variants (id),
  market     VARCHAR(2) NOT NULL,
  PRIMARY KEY (variant_id, market)
);

-- -----------------------------------------------------------------------------
-- image_sources
-- -----------------------------------------------------------------------------
CREATE TABLE image_sources
(
  id          BIGSERIAL    PRIMARY KEY,
  image_url   TEXT         NOT NULL UNIQUE,
  source_url  TEXT,
  author      VARCHAR(255),
  license     VARCHAR(100) NOT NULL,
  license_url TEXT,
  copyright   VARCHAR(500),
  usage_terms TEXT,
  obtained_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
