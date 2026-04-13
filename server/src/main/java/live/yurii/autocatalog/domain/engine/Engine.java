package live.yurii.autocatalog.domain.engine;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * ICE engine aggregate.
 *
 * <p>The {@code id} is a database-generated BIGINT (shared PK with {@code power_unit}).
 * It is null until the engine is persisted for the first time.
 *
 * <p>Use {@link #create} for new engines, {@link #reconstitute} to restore from DB.
 */
public class Engine {

  private EngineId id;
  private String code;
  private String name;
  private FuelType fuelType;
  private Integer displacementCc;
  private int powerKw;
  private Integer torqueNm;
  private Integer cylinderCount;
  private String configuration;
  private Integer systemPowerKw;
  private BigDecimal compressionRatio;
  private boolean turbo;

  private Engine() {
  }

  /**
   * Creates a new Engine. ID will be assigned by the database on save.
   */
  public static Engine create(String code, String name, FuelType fuelType,
                              Integer displacementCc, int powerKw, Integer torqueNm) {
    validate(displacementCc, powerKw, torqueNm);
    var e = new Engine();
    e.code = requireNonBlank(code, "code");
    e.name = name == null || name.isBlank() ? null : name.strip();
    e.fuelType = Objects.requireNonNull(fuelType, "fuelType must not be null");
    e.displacementCc = displacementCc;
    e.powerKw = powerKw;
    e.torqueNm = torqueNm;
    e.turbo = false;
    return e;
  }

  /**
   * Rebuilds an Engine from persisted state without re-validating.
   * Used exclusively by repository adapters.
   */
  public static Engine reconstitute(EngineId id, String code, String name, FuelType fuelType,
                                    Integer displacementCc, int powerKw, Integer torqueNm,
                                    Integer cylinderCount, String configuration,
                                    Integer systemPowerKw, BigDecimal compressionRatio,
                                    boolean turbo) {
    var e = new Engine();
    e.id = id;
    e.code = code;
    e.name = name;
    e.fuelType = fuelType;
    e.displacementCc = displacementCc;
    e.powerKw = powerKw;
    e.torqueNm = torqueNm;
    e.cylinderCount = cylinderCount;
    e.configuration = configuration;
    e.systemPowerKw = systemPowerKw;
    e.compressionRatio = compressionRatio;
    e.turbo = turbo;
    return e;
  }

  public void rename(String newName) {
    this.name = requireNonBlank(newName, "name");
  }

  public void setCylinderCount(int cylinderCount) {
    if (cylinderCount < 1) throw new IllegalArgumentException("cylinderCount must be > 0");
    this.cylinderCount = cylinderCount;
  }

  public void setConfiguration(String configuration) {
    this.configuration = requireNonBlank(configuration, "configuration");
  }

  public void setSystemPowerKw(int systemPowerKw) {
    if (systemPowerKw < 1) throw new IllegalArgumentException("systemPowerKw must be > 0");
    this.systemPowerKw = systemPowerKw;
  }

  public void setCompressionRatio(BigDecimal compressionRatio) {
    this.compressionRatio = compressionRatio;
  }

  public void setTurbo(boolean turbo) {
    this.turbo = turbo;
  }

  public int powerHp() {
    return (int) Math.round(powerKw * 1.35962);
  }

  public EngineId id() {
    return id;
  }

  public String code() {
    return code;
  }

  public String name() {
    return name;
  }

  public FuelType fuelType() {
    return fuelType;
  }

  public Integer displacementCc() {
    return displacementCc;
  }

  public int powerKw() {
    return powerKw;
  }

  public Integer torqueNm() {
    return torqueNm;
  }

  public Integer cylinderCount() {
    return cylinderCount;
  }

  public String configuration() {
    return configuration;
  }

  public Integer systemPowerKw() {
    return systemPowerKw;
  }

  public BigDecimal compressionRatio() {
    return compressionRatio;
  }

  public boolean turbo() {
    return turbo;
  }

  private static void validate(Integer cc, int kw, Integer nm) {
    if (cc != null && cc <= 0) throw new IllegalArgumentException("displacement must be > 0");
    if (kw <= 0) throw new IllegalArgumentException("power must be > 0");
    if (nm != null && nm <= 0) throw new IllegalArgumentException("torque must be > 0");
  }

  private static String requireNonBlank(String v, String field) {
    if (v == null || v.isBlank())
      throw new IllegalArgumentException(field + " must not be blank");
    return v.strip();
  }
}
