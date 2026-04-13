package live.yurii.autocatalog.api.powertrain;

import jakarta.validation.constraints.NotNull;
import live.yurii.autocatalog.domain.powertrain.UnitRole;

/**
 * A power unit entry within a powertrain creation request.
 */
public record PowertrainUnitEntry(
  @NotNull Long powerUnitId,
  @NotNull UnitRole role
) {
}
