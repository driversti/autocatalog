package live.yurii.autocatalog.infrastructure.persistence.powerunit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import live.yurii.autocatalog.domain.powerunit.PowerUnitType;

/**
 * JPA entity for the power_unit table.
 * Made public to allow the engine, electricmotor, fuelcellstack, and powertrain
 * packages to reference it for the shared-PK pattern via @MapsId.
 */
@Entity
@Table(name = "power_unit")
public class PowerUnitJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "unit_type", nullable = false, length = 20)
  private PowerUnitType unitType;

  protected PowerUnitJpaEntity() {
  }

  public PowerUnitJpaEntity(PowerUnitType unitType) {
    this.unitType = unitType;
  }

  /** Constructor for unit tests or reconstitution: allows explicit id. */
  public PowerUnitJpaEntity(Long id, PowerUnitType unitType) {
    this.id = id;
    this.unitType = unitType;
  }

  public Long id() {
    return id;
  }

  public PowerUnitType unitType() {
    return unitType;
  }
}
