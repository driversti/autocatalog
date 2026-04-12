package live.yurii.autocatalog.infrastructure.persistence.engine;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import live.yurii.autocatalog.domain.engine.FuelType;

import java.util.UUID;

@Entity
@Table(name = "engines")
class EngineJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(nullable = false, unique = true, length = 50)
  private String code;

  @Column(nullable = false, length = 150)
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

  protected EngineJpaEntity() {
  }

  EngineJpaEntity(UUID id, String code, String name, FuelType fuelType,
                  Integer displacementCc, int powerKw, Integer torqueNm,
                  Integer cylinderCount, String configuration,
                  Integer systemPowerKw) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.fuelType = fuelType;
    this.displacementCc = displacementCc;
    this.powerKw = powerKw;
    this.torqueNm = torqueNm;
    this.cylinderCount = cylinderCount;
    this.configuration = configuration;
    this.systemPowerKw = systemPowerKw;
  }

  UUID id() {
    return id;
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
}
