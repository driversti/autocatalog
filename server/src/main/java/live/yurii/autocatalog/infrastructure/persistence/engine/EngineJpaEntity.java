package live.yurii.autocatalog.infrastructure.persistence.engine;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import live.yurii.autocatalog.domain.engine.FuelType;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "engines")
class EngineJpaEntity {

  @Id
  private Long id;   // shared PK — no @GeneratedValue here; derived from power_unit via @MapsId

  @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
  @MapsId
  @JoinColumn(name = "id")
  private PowerUnitJpaEntity unit;

  @Column(nullable = false, unique = true, length = 50)
  private String code;

  @Column(length = 150)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "fuel_type", nullable = false, length = 30)
  private FuelType fuelType;

  @Column(name = "displacement_cc")
  private Integer displacementCc;

  @Column(name = "power_kw", nullable = false)
  private int powerKw;

  @Column(name = "torque_nm")
  private Integer torqueNm;

  @Column(name = "cylinder_count")
  private Integer cylinderCount;

  @Column(length = 20)
  private String configuration;

  @Column(name = "system_power_kw")
  private Integer systemPowerKw;

  @Column(name = "compression_ratio", precision = 4, scale = 1)
  private BigDecimal compressionRatio;

  @Column(name = "turbo", nullable = false)
  private boolean turbo;

  protected EngineJpaEntity() {
  }

  EngineJpaEntity(PowerUnitJpaEntity unit, String code, String name, FuelType fuelType,
                  Integer displacementCc, int powerKw, Integer torqueNm,
                  Integer cylinderCount, String configuration,
                  Integer systemPowerKw, BigDecimal compressionRatio, boolean turbo) {
    this.unit = unit;
    this.code = code;
    this.name = name;
    this.fuelType = fuelType;
    this.displacementCc = displacementCc;
    this.powerKw = powerKw;
    this.torqueNm = torqueNm;
    this.cylinderCount = cylinderCount;
    this.configuration = configuration;
    this.systemPowerKw = systemPowerKw;
    this.compressionRatio = compressionRatio;
    this.turbo = turbo;
  }

  /** Constructor for unit tests: allows explicit id assignment (simulates post-persist state). */
  EngineJpaEntity(Long id, PowerUnitJpaEntity unit, String code, String name, FuelType fuelType,
                  Integer displacementCc, int powerKw, Integer torqueNm,
                  Integer cylinderCount, String configuration,
                  Integer systemPowerKw, BigDecimal compressionRatio, boolean turbo) {
    this(unit, code, name, fuelType, displacementCc, powerKw, torqueNm,
      cylinderCount, configuration, systemPowerKw, compressionRatio, turbo);
    this.id = id;
  }

  Long id() {
    return id;
  }

  PowerUnitJpaEntity unit() {
    return unit;
  }

  String code() {
    return code;
  }

  String name() {
    return name;
  }

  FuelType fuelType() {
    return fuelType;
  }

  Integer displacementCc() {
    return displacementCc;
  }

  int powerKw() {
    return powerKw;
  }

  Integer torqueNm() {
    return torqueNm;
  }

  Integer cylinderCount() {
    return cylinderCount;
  }

  String configuration() {
    return configuration;
  }

  Integer systemPowerKw() {
    return systemPowerKw;
  }

  BigDecimal compressionRatio() {
    return compressionRatio;
  }

  boolean turbo() {
    return turbo;
  }
}
