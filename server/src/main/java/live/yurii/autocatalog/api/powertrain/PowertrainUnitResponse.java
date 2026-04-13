package live.yurii.autocatalog.api.powertrain;

import live.yurii.autocatalog.domain.powertrain.PowertrainUnit;
import live.yurii.autocatalog.domain.powertrain.UnitRole;

/**
 * Power unit entry within a powertrain response.
 */
public record PowertrainUnitResponse(Long powerUnitId, UnitRole role) {
  public static PowertrainUnitResponse from(PowertrainUnit pu) {
    return new PowertrainUnitResponse(pu.powerUnitId().value(), pu.role());
  }
}
