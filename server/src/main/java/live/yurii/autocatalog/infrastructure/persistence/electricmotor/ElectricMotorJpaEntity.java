package live.yurii.autocatalog.infrastructure.persistence.electricmotor;

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
import live.yurii.autocatalog.domain.electricmotor.MotorType;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaEntity;

@Entity
@Table(name = "electric_motor")
class ElectricMotorJpaEntity {

  @Id
  private Long id;   // shared PK via @MapsId

  @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
  @MapsId
  @JoinColumn(name = "id")
  private PowerUnitJpaEntity unit;

  @Column(nullable = false, length = 100)
  private String label;

  @Column(name = "power_kw")
  private Integer powerKw;

  @Column(name = "torque_nm")
  private Integer torqueNm;

  @Enumerated(EnumType.STRING)
  @Column(name = "motor_type", length = 30)
  private MotorType motorType;

  protected ElectricMotorJpaEntity() {
  }

  ElectricMotorJpaEntity(PowerUnitJpaEntity unit, String label,
                         Integer powerKw, Integer torqueNm, MotorType motorType) {
    this.unit = unit;
    this.label = label;
    this.powerKw = powerKw;
    this.torqueNm = torqueNm;
    this.motorType = motorType;
  }

  Long id() {
    return id;
  }

  String label() {
    return label;
  }

  Integer powerKw() {
    return powerKw;
  }

  Integer torqueNm() {
    return torqueNm;
  }

  MotorType motorType() {
    return motorType;
  }
}
