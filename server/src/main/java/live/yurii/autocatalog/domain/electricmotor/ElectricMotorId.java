package live.yurii.autocatalog.domain.electricmotor;

import java.util.Objects;

/**
 * Typed identifier for an ElectricMotor.
 * Uses Long because electric_motor.id is a BIGINT (shared PK with power_unit.id BIGSERIAL).
 */
public record ElectricMotorId(Long value) {
  public ElectricMotorId {
    Objects.requireNonNull(value, "ElectricMotorId value must not be null");
  }

  public static ElectricMotorId of(Long value) {
    return new ElectricMotorId(value);
  }
}
