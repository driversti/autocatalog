package live.yurii.autocatalog.domain.electricmotor;

/**
 * Electric motor aggregate.
 *
 * <p>The {@code id} is a database-generated BIGINT (shared PK with {@code power_unit}).
 * It is null until the motor is persisted for the first time.
 *
 * <p>Use {@link #create} for new motors, {@link #reconstitute} to restore from DB.
 */
public class ElectricMotor {

  private ElectricMotorId id;
  private String label;
  private Integer powerKw;
  private Integer torqueNm;
  private MotorType motorType;

  private ElectricMotor() {
  }

  /**
   * Creates a new ElectricMotor. ID will be assigned by the database on save.
   *
   * @param label     descriptive label (e.g. "Rear EM 75kW"); must not be blank
   * @param powerKw   power output in kW; optional but must be > 0 when present
   * @param torqueNm  torque in Nm; optional but must be > 0 when present
   * @param motorType construction type; optional
   */
  public static ElectricMotor create(String label, Integer powerKw, Integer torqueNm,
                                     MotorType motorType) {
    if (label == null || label.isBlank())
      throw new IllegalArgumentException("label must not be blank");
    if (powerKw != null && powerKw <= 0)
      throw new IllegalArgumentException("powerKw must be > 0");
    if (torqueNm != null && torqueNm <= 0)
      throw new IllegalArgumentException("torqueNm must be > 0");
    var m = new ElectricMotor();
    m.label = label.strip();
    m.powerKw = powerKw;
    m.torqueNm = torqueNm;
    m.motorType = motorType;
    return m;
  }

  /**
   * Rebuilds an ElectricMotor from persisted state without re-validating.
   * Used exclusively by repository adapters.
   */
  public static ElectricMotor reconstitute(ElectricMotorId id, String label,
                                           Integer powerKw, Integer torqueNm,
                                           MotorType motorType) {
    var m = new ElectricMotor();
    m.id = id;
    m.label = label;
    m.powerKw = powerKw;
    m.torqueNm = torqueNm;
    m.motorType = motorType;
    return m;
  }

  public void updateLabel(String newLabel) {
    if (newLabel == null || newLabel.isBlank())
      throw new IllegalArgumentException("label must not be blank");
    this.label = newLabel.strip();
  }

  public void updateSpecs(Integer powerKw, Integer torqueNm, MotorType motorType) {
    if (powerKw != null && powerKw <= 0)
      throw new IllegalArgumentException("powerKw must be > 0");
    if (torqueNm != null && torqueNm <= 0)
      throw new IllegalArgumentException("torqueNm must be > 0");
    this.powerKw = powerKw;
    this.torqueNm = torqueNm;
    this.motorType = motorType;
  }

  public ElectricMotorId id() {
    return id;
  }

  public String label() {
    return label;
  }

  public Integer powerKw() {
    return powerKw;
  }

  public Integer torqueNm() {
    return torqueNm;
  }

  public MotorType motorType() {
    return motorType;
  }
}
