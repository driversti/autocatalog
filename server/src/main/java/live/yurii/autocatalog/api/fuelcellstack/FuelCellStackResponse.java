package live.yurii.autocatalog.api.fuelcellstack;

import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStack;

import java.math.BigDecimal;

public record FuelCellStackResponse(
  Long id,
  String label,
  Integer peakPowerKw,
  BigDecimal hydrogenTankKg
) {
  public static FuelCellStackResponse from(FuelCellStack s) {
    return new FuelCellStackResponse(
      s.id() == null ? null : s.id().value(),
      s.label(), s.peakPowerKw(), s.hydrogenTankKg()
    );
  }
}
