package live.yurii.autocatalog.api.engine;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import live.yurii.autocatalog.domain.engine.FuelType;

public record EngineRequest(
  @NotBlank @Size(max = 50) String code,
  @NotBlank @Size(max = 150) String name,
  @NotNull FuelType fuelType,
  @Min(1) Integer displacementCc,
  @Positive int powerKw,
  @Min(1) Integer torqueNm,
  @Min(1) @Max(16) Integer cylinderCount,
  @Size(max = 20) String configuration,
  @Positive Integer systemPowerKw
) {
}
