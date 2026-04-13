package live.yurii.autocatalog.api.powertrain;

import live.yurii.autocatalog.domain.powertrain.DrivetrainType;
import live.yurii.autocatalog.domain.powertrain.Powertrain;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PowertrainResponse(
  UUID id,
  String name,
  DrivetrainType drivetrainType,
  Integer combinedPowerHp,
  Integer combinedTorqueNm,
  BigDecimal batteryCapacityKwh,
  Integer electricRangeKm,
  List<PowertrainUnitResponse> units
) {
  public static PowertrainResponse from(Powertrain p) {
    return new PowertrainResponse(
      p.id().value(),
      p.name(),
      p.drivetrainType(),
      p.combinedPowerHp(),
      p.combinedTorqueNm(),
      p.batteryCapacityKwh(),
      p.electricRangeKm(),
      p.units().stream().map(PowertrainUnitResponse::from).toList()
    );
  }
}
