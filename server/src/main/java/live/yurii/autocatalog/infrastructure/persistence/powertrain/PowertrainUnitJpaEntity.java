package live.yurii.autocatalog.infrastructure.persistence.powertrain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import live.yurii.autocatalog.domain.powertrain.UnitRole;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaEntity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Join entity representing one power unit's participation in a powertrain.
 * Stored in the {@code powertrain_unit} table.
 */
@Entity
@Table(name = "powertrain_unit")
class PowertrainUnitJpaEntity {

  @EmbeddedId
  private PowertrainUnitId id = new PowertrainUnitId();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("powertrainId")
  @JoinColumn(name = "powertrain_id")
  private PowertrainJpaEntity powertrain;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("powerUnitId")
  @JoinColumn(name = "power_unit_id")
  private PowerUnitJpaEntity powerUnit;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UnitRole role;

  protected PowertrainUnitJpaEntity() {
  }

  PowertrainUnitJpaEntity(PowertrainJpaEntity powertrain, PowerUnitJpaEntity powerUnit, UnitRole role) {
    this.powertrain = powertrain;
    this.powerUnit = powerUnit;
    this.role = role;
    this.id.powertrainId = powertrain.id();
    this.id.powerUnitId = powerUnit.id();
  }

  PowerUnitJpaEntity powerUnit() {
    return powerUnit;
  }

  UnitRole role() {
    return role;
  }

  @Embeddable
  static class PowertrainUnitId implements Serializable {
    private UUID powertrainId;
    private Long powerUnitId;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof PowertrainUnitId that)) return false;
      return Objects.equals(powertrainId, that.powertrainId)
        && Objects.equals(powerUnitId, that.powerUnitId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(powertrainId, powerUnitId);
    }
  }
}
