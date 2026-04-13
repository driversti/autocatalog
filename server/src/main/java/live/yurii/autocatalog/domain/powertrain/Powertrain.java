package live.yurii.autocatalog.domain.powertrain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a named powertrain configuration: one or more power units working together,
 * with their combined output figures.
 *
 * <p>Examples:
 * <ul>
 *   <li>ICE A180: one engine with role PRIMARY
 *   <li>PHEV A250e: engine PRIMARY + electric motor SECONDARY
 *   <li>BEV EQA 250: one electric motor with role PRIMARY
 *   <li>FCEV Mirai: fuel cell stack GENERATOR + electric motor PRIMARY
 * </ul>
 *
 * <p>Factory invariants enforced by {@link #create}:
 * <ul>
 *   <li>At least one unit must be provided
 *   <li>Exactly one unit must have role PRIMARY
 *   <li>{@code batteryCapacityKwh} and {@code electricRangeKm} must both be set or both null
 * </ul>
 */
public class Powertrain {

  private PowertrainId id;
  private String name;
  private DrivetrainType drivetrainType;
  private Integer combinedPowerHp;
  private Integer combinedTorqueNm;
  private BigDecimal batteryCapacityKwh;
  private Integer electricRangeKm;
  private List<PowertrainUnit> units = new ArrayList<>();

  private Powertrain() {
  }

  /**
   * Creates a new Powertrain, validating mandatory fields and generating a new ID.
   *
   * @param name              human-readable label (e.g. "2.0 TDI 150 hp")
   * @param drivetrainType    the propulsion strategy classification
   * @param combinedPowerHp   combined system power in HP; optional
   * @param combinedTorqueNm  combined system torque in Nm; optional but must be > 0 when present
   * @param batteryCapacityKwh battery capacity; must be paired with electricRangeKm
   * @param electricRangeKm   electric range; must be paired with batteryCapacityKwh
   * @param units             at least one unit entry required; exactly one must be PRIMARY
   */
  public static Powertrain create(String name, DrivetrainType drivetrainType,
                                  Integer combinedPowerHp, Integer combinedTorqueNm,
                                  BigDecimal batteryCapacityKwh, Integer electricRangeKm,
                                  List<PowertrainUnit> units) {
    if (name == null || name.isBlank())
      throw new IllegalArgumentException("name must not be blank");
    Objects.requireNonNull(drivetrainType, "drivetrainType must not be null");
    if (combinedTorqueNm != null && combinedTorqueNm <= 0)
      throw new IllegalArgumentException("combinedTorqueNm must be > 0");
    if ((batteryCapacityKwh == null) != (electricRangeKm == null))
      throw new IllegalArgumentException(
        "batteryCapacityKwh and electricRangeKm must both be set or both null");
    if (units == null || units.isEmpty())
      throw new IllegalArgumentException("at least one unit entry is required");
    long primaryCount = units.stream().filter(u -> u.role() == UnitRole.PRIMARY).count();
    if (primaryCount != 1)
      throw new IllegalArgumentException("exactly one unit must have role PRIMARY");

    var p = new Powertrain();
    p.id = PowertrainId.generate();
    p.name = name.strip();
    p.drivetrainType = drivetrainType;
    p.combinedPowerHp = combinedPowerHp;
    p.combinedTorqueNm = combinedTorqueNm;
    p.batteryCapacityKwh = batteryCapacityKwh;
    p.electricRangeKm = electricRangeKm;
    p.units = new ArrayList<>(units);
    return p;
  }

  /**
   * Rebuilds a Powertrain from persisted state without re-validating.
   * Used exclusively by repository adapters.
   */
  public static Powertrain reconstitute(PowertrainId id, String name, DrivetrainType drivetrainType,
                                        Integer combinedPowerHp, Integer combinedTorqueNm,
                                        BigDecimal batteryCapacityKwh, Integer electricRangeKm,
                                        List<PowertrainUnit> units) {
    var p = new Powertrain();
    p.id = id;
    p.name = name;
    p.drivetrainType = drivetrainType;
    p.combinedPowerHp = combinedPowerHp;
    p.combinedTorqueNm = combinedTorqueNm;
    p.batteryCapacityKwh = batteryCapacityKwh;
    p.electricRangeKm = electricRangeKm;
    p.units = new ArrayList<>(units);
    return p;
  }

  public void rename(String newName) {
    if (newName == null || newName.isBlank())
      throw new IllegalArgumentException("name must not be blank");
    this.name = newName.strip();
  }

  public PowertrainId id() {
    return id;
  }

  public String name() {
    return name;
  }

  public DrivetrainType drivetrainType() {
    return drivetrainType;
  }

  public Integer combinedPowerHp() {
    return combinedPowerHp;
  }

  public Integer combinedTorqueNm() {
    return combinedTorqueNm;
  }

  public BigDecimal batteryCapacityKwh() {
    return batteryCapacityKwh;
  }

  public Integer electricRangeKm() {
    return electricRangeKm;
  }

  public List<PowertrainUnit> units() {
    return Collections.unmodifiableList(units);
  }
}
