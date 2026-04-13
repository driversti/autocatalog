package live.yurii.autocatalog.domain.fuelcellstack;

import java.util.Objects;

/**
 * Typed identifier for a FuelCellStack.
 * Uses Long because fuel_cell_stack.id is a BIGINT (shared PK with power_unit.id BIGSERIAL).
 */
public record FuelCellStackId(Long value) {
  public FuelCellStackId {
    Objects.requireNonNull(value, "FuelCellStackId value must not be null");
  }

  public static FuelCellStackId of(Long value) {
    return new FuelCellStackId(value);
  }
}
