package live.yurii.autocatalog.infrastructure.persistence.powertrain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import live.yurii.autocatalog.domain.powertrain.DrivetrainType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "powertrains")
class PowertrainJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(nullable = false, unique = true, length = 200)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "drivetrain_type", nullable = false, length = 20)
  private DrivetrainType drivetrainType;

  @Column(name = "combined_power_hp")
  private Integer combinedPowerHp;

  @Column(name = "combined_torque_nm")
  private Integer combinedTorqueNm;

  @Column(name = "battery_capacity_kwh", precision = 5, scale = 1)
  private BigDecimal batteryCapacityKwh;

  @Column(name = "electric_range_km")
  private Integer electricRangeKm;

  @OneToMany(mappedBy = "powertrain", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PowertrainUnitJpaEntity> units = new ArrayList<>();

  protected PowertrainJpaEntity() {
  }

  PowertrainJpaEntity(UUID id, String name, DrivetrainType drivetrainType,
                      Integer combinedPowerHp, Integer combinedTorqueNm,
                      BigDecimal batteryCapacityKwh, Integer electricRangeKm) {
    this.id = id;
    this.name = name;
    this.drivetrainType = drivetrainType;
    this.combinedPowerHp = combinedPowerHp;
    this.combinedTorqueNm = combinedTorqueNm;
    this.batteryCapacityKwh = batteryCapacityKwh;
    this.electricRangeKm = electricRangeKm;
  }

  UUID id() {
    return id;
  }

  String name() {
    return name;
  }

  DrivetrainType drivetrainType() {
    return drivetrainType;
  }

  Integer combinedPowerHp() {
    return combinedPowerHp;
  }

  Integer combinedTorqueNm() {
    return combinedTorqueNm;
  }

  BigDecimal batteryCapacityKwh() {
    return batteryCapacityKwh;
  }

  Integer electricRangeKm() {
    return electricRangeKm;
  }

  List<PowertrainUnitJpaEntity> units() {
    return units;
  }
}
