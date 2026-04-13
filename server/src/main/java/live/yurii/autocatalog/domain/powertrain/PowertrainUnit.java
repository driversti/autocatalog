package live.yurii.autocatalog.domain.powertrain;

import live.yurii.autocatalog.domain.powerunit.PowerUnitId;

import java.util.Objects;

/**
 * Value object that pairs a power unit reference with its role in the powertrain.
 */
public record PowertrainUnit(PowerUnitId powerUnitId, UnitRole role) {
  public PowertrainUnit {
    Objects.requireNonNull(powerUnitId, "powerUnitId must not be null");
    Objects.requireNonNull(role, "role must not be null");
  }
}
