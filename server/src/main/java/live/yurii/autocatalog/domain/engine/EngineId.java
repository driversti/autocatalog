package live.yurii.autocatalog.domain.engine;

import java.util.Objects;

/**
 * Typed identifier for an Engine.
 * Uses Long because engine.id is a BIGINT (shared PK with power_unit.id BIGSERIAL).
 */
public record EngineId(Long value) {
  public EngineId {
    Objects.requireNonNull(value, "EngineId value must not be null");
  }

  public static EngineId of(Long value) {
    return new EngineId(value);
  }
}
