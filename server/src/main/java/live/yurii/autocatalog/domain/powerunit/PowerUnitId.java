package live.yurii.autocatalog.domain.powerunit;

import java.util.Objects;

/**
 * Typed identifier for a PowerUnit.
 * Uses Long because power_unit.id is a BIGSERIAL in the database.
 */
public record PowerUnitId(Long value) {
  public PowerUnitId {
    Objects.requireNonNull(value, "PowerUnitId value must not be null");
  }

  public static PowerUnitId of(Long value) {
    return new PowerUnitId(value);
  }
}
