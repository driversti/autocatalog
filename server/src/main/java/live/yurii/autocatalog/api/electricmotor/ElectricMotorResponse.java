package live.yurii.autocatalog.api.electricmotor;

import live.yurii.autocatalog.domain.electricmotor.ElectricMotor;
import live.yurii.autocatalog.domain.electricmotor.MotorType;

public record ElectricMotorResponse(
  Long id,
  String label,
  Integer powerKw,
  Integer torqueNm,
  MotorType motorType
) {
  public static ElectricMotorResponse from(ElectricMotor m) {
    return new ElectricMotorResponse(
      m.id() == null ? null : m.id().value(),
      m.label(), m.powerKw(), m.torqueNm(), m.motorType()
    );
  }
}
