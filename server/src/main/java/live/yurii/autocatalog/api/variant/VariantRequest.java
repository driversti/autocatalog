package live.yurii.autocatalog.api.variant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import live.yurii.autocatalog.domain.variant.Drivetrain;

import java.util.UUID;

public record VariantRequest(
  @NotNull UUID bodyId,
  @NotNull UUID powertrainId,
  @NotNull UUID transmissionId,
  @NotNull Drivetrain drivetrain,
  @Min(1) int curbWeightKg,
  @Min(1) Integer groundClearanceMm
) {
}
