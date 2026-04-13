package live.yurii.autocatalog.api.powertrain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import live.yurii.autocatalog.domain.powertrain.DrivetrainType;

import java.math.BigDecimal;
import java.util.List;

public record PowertrainRequest(
  @NotBlank @Size(max = 200) String name,
  @NotNull DrivetrainType drivetrainType,
  @Min(1) Integer combinedPowerHp,
  @Min(1) Integer combinedTorqueNm,
  BigDecimal batteryCapacityKwh,
  Integer electricRangeKm,
  @NotEmpty @Valid List<PowertrainUnitEntry> units
) {
}
