package live.yurii.autocatalog.infrastructure.persistence.fuelcellstack;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "fuel_cell_stack")
class FuelCellStackJpaEntity {

  @Id
  private Long id;   // shared PK via @MapsId

  @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
  @MapsId
  @JoinColumn(name = "id")
  private PowerUnitJpaEntity unit;

  @Column(nullable = false, length = 100)
  private String label;

  @Column(name = "peak_power_kw")
  private Integer peakPowerKw;

  @Column(name = "hydrogen_tank_kg", precision = 4, scale = 1)
  private BigDecimal hydrogenTankKg;

  protected FuelCellStackJpaEntity() {
  }

  FuelCellStackJpaEntity(PowerUnitJpaEntity unit, String label,
                         Integer peakPowerKw, BigDecimal hydrogenTankKg) {
    this.unit = unit;
    this.label = label;
    this.peakPowerKw = peakPowerKw;
    this.hydrogenTankKg = hydrogenTankKg;
  }

  Long id() {
    return id;
  }

  String label() {
    return label;
  }

  Integer peakPowerKw() {
    return peakPowerKw;
  }

  BigDecimal hydrogenTankKg() {
    return hydrogenTankKg;
  }
}
