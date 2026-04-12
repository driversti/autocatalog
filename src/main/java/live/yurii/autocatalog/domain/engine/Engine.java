package live.yurii.autocatalog.domain.engine;

import java.util.Objects;

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

  private Engine() {
  }

  public static Engine create(String code, String name, FuelType fuelType,
                              Integer displacementCc, int powerKw, Integer torqueNm) {
    validate(displacementCc, powerKw, torqueNm);
    var e = new Engine();
    e.id = EngineId.generate();
    e.code = requireNonBlank(code, "code");
    e.name = requireNonBlank(name, "name");
    e.fuelType = Objects.requireNonNull(fuelType);
    e.displacementCc = displacementCc;
    e.powerKw = powerKw;
    e.torqueNm = torqueNm;
    return e;
  }

  public static Engine reconstitute(EngineId id, String code, String name, FuelType fuelType,
                                    Integer displacementCc, int powerKw, Integer torqueNm,
                                    Integer cylinderCount, String configuration,
                                    Integer systemPowerKw) {
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
    return e;
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
