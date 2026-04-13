package live.yurii.autocatalog.domain.fuelcellstack;

import java.math.BigDecimal;

/**
 * Fuel cell stack aggregate (FCEV-specific propulsion component).
 *
 * <p>The {@code id} is a database-generated BIGINT (shared PK with {@code power_unit}).
 * It is null until the stack is persisted for the first time.
 */
public class FuelCellStack {

  private FuelCellStackId id;
  private String label;
  private Integer peakPowerKw;
  private BigDecimal hydrogenTankKg;

  private FuelCellStack() {
  }

  /**
   * Creates a new FuelCellStack. ID will be assigned by the database on save.
   *
   * @param label           descriptive label; must not be blank
   * @param peakPowerKw     peak electrical output in kW; optional but must be > 0 when present
   * @param hydrogenTankKg  hydrogen storage capacity in kg; optional
   */
  public static FuelCellStack create(String label, Integer peakPowerKw, BigDecimal hydrogenTankKg) {
    if (label == null || label.isBlank())
      throw new IllegalArgumentException("label must not be blank");
    if (peakPowerKw != null && peakPowerKw <= 0)
      throw new IllegalArgumentException("peakPowerKw must be > 0");
    var s = new FuelCellStack();
    s.label = label.strip();
    s.peakPowerKw = peakPowerKw;
    s.hydrogenTankKg = hydrogenTankKg;
    return s;
  }

  /**
   * Rebuilds a FuelCellStack from persisted state without re-validating.
   * Used exclusively by repository adapters.
   */
  public static FuelCellStack reconstitute(FuelCellStackId id, String label,
                                           Integer peakPowerKw, BigDecimal hydrogenTankKg) {
    var s = new FuelCellStack();
    s.id = id;
    s.label = label;
    s.peakPowerKw = peakPowerKw;
    s.hydrogenTankKg = hydrogenTankKg;
    return s;
  }

  public FuelCellStackId id() {
    return id;
  }

  public String label() {
    return label;
  }

  public Integer peakPowerKw() {
    return peakPowerKw;
  }

  public BigDecimal hydrogenTankKg() {
    return hydrogenTankKg;
  }
}
