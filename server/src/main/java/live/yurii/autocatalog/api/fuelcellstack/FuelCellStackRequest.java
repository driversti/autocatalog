package live.yurii.autocatalog.api.fuelcellstack;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FuelCellStackRequest(
  @NotBlank @Size(max = 100) String label,
  @Min(1) Integer peakPowerKw,
  BigDecimal hydrogenTankKg
) {
}
