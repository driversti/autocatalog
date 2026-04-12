-- =============================================================================
-- V1__initial_schema.sql
-- Full initial schema for AutoCatalog.
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
-- engines  (shared reference aggregate)
-- -----------------------------------------------------------------------------
CREATE TABLE engines
(
  id               UUID         PRIMARY KEY,
  code             VARCHAR(50)  NOT NULL,
  name             VARCHAR(150) NOT NULL,
  fuel_type        VARCHAR(30)  NOT NULL,
  displacement_cc  INT          CHECK (displacement_cc IS NULL OR displacement_cc > 0),
  power_kw         INT          NOT NULL CHECK (power_kw > 0),
  torque_nm        INT          CHECK (torque_nm IS NULL OR torque_nm > 0),
  cylinder_count   INT,
  configuration    VARCHAR(20),
  system_power_kw  INT          CHECK (system_power_kw IS NULL OR system_power_kw > 0),
  CONSTRAINT uk_engines_code UNIQUE (code)
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
-- -----------------------------------------------------------------------------
CREATE TABLE bodies
(
  id                   UUID        PRIMARY KEY,
  generation_id        UUID        NOT NULL REFERENCES generations (id),
  body_style           VARCHAR(20) NOT NULL,
  length_mm            INT         NOT NULL CHECK (length_mm > 0),
  width_mm             INT         NOT NULL CHECK (width_mm > 0),
  height_mm            INT         NOT NULL CHECK (height_mm > 0),
  wheelbase_mm         INT         NOT NULL CHECK (wheelbase_mm > 0),
  trunk_volume_litres  INT         CHECK (trunk_volume_litres > 0),
  ground_clearance_mm  INT         CHECK (ground_clearance_mm > 0),
  CONSTRAINT uk_bodies_generation_style UNIQUE (generation_id, body_style)
);

-- -----------------------------------------------------------------------------
-- variants  (one technical configuration per body)
-- -----------------------------------------------------------------------------
CREATE TABLE variants
(
  id                   UUID        PRIMARY KEY,
  body_id              UUID        NOT NULL REFERENCES bodies (id),
  transmission_id      UUID        NOT NULL REFERENCES transmissions (id),
  drivetrain           VARCHAR(10) NOT NULL,
  ground_clearance_mm  INT         CHECK (ground_clearance_mm > 0),
  curb_weight_kg       INT         NOT NULL CHECK (curb_weight_kg > 0),
  system_power_kw      INT         CHECK (system_power_kw > 0),
  CONSTRAINT uk_variants_body_transmission_drivetrain UNIQUE (body_id, transmission_id, drivetrain)
);

-- -----------------------------------------------------------------------------
-- variant_engines  (M:N — hybrids link ICE + electric engine)
-- -----------------------------------------------------------------------------
CREATE TABLE variant_engines
(
  variant_id UUID NOT NULL REFERENCES variants (id),
  engine_id  UUID NOT NULL REFERENCES engines (id),
  PRIMARY KEY (variant_id, engine_id)
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
