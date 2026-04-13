package live.yurii.autocatalog.infrastructure.persistence.powertrain;

import live.yurii.autocatalog.domain.powertrain.DrivetrainType;
import live.yurii.autocatalog.domain.powertrain.Powertrain;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.powertrain.PowertrainRepository;
import live.yurii.autocatalog.domain.powertrain.PowertrainUnit;
import live.yurii.autocatalog.domain.powertrain.UnitRole;
import live.yurii.autocatalog.domain.powerunit.PowerUnitId;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaEntity;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PowertrainRepositoryAdapter implements PowertrainRepository {

  private final PowertrainJpaRepository jpa;
  private final PowerUnitJpaRepository powerUnitJpa;

  public PowertrainRepositoryAdapter(PowertrainJpaRepository jpa,
                                     PowerUnitJpaRepository powerUnitJpa) {
    this.jpa = jpa;
    this.powerUnitJpa = powerUnitJpa;
  }

  @Override
  public Powertrain save(Powertrain powertrain) {
    var entity = toEntity(powertrain);
    // Add unit join rows
    for (var unit : powertrain.units()) {
      PowerUnitJpaEntity powerUnitEntity = powerUnitJpa.getReferenceById(unit.powerUnitId().value());
      entity.units().add(new PowertrainUnitJpaEntity(entity, powerUnitEntity, unit.role()));
    }
    jpa.save(entity);
    return powertrain;
  }

  @Override
  public Optional<Powertrain> findById(PowertrainId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public Optional<Powertrain> findByName(String name) {
    return jpa.findByName(name).map(this::toDomain);
  }

  @Override
  public List<Powertrain> findAll() {
    return jpa.findAll().stream().map(this::toDomain).toList();
  }

  @Override
  public boolean existsByName(String name) {
    return jpa.existsByName(name);
  }

  @Override
  public boolean existsByPowerUnitId(PowerUnitId powerUnitId) {
    return jpa.existsByUnits_PowerUnit_Id(powerUnitId.value());
  }

  @Override
  public void deleteById(PowertrainId id) {
    jpa.deleteById(id.value());
  }

  private PowertrainJpaEntity toEntity(Powertrain p) {
    return new PowertrainJpaEntity(
      p.id().value(), p.name(), p.drivetrainType(),
      p.combinedPowerHp(), p.combinedTorqueNm(),
      p.batteryCapacityKwh(), p.electricRangeKm()
    );
  }

  private Powertrain toDomain(PowertrainJpaEntity e) {
    List<PowertrainUnit> units = e.units().stream()
      .map(pu -> new PowertrainUnit(new PowerUnitId(pu.powerUnit().id()), pu.role()))
      .toList();
    return Powertrain.reconstitute(
      new PowertrainId(e.id()), e.name(), e.drivetrainType(),
      e.combinedPowerHp(), e.combinedTorqueNm(),
      e.batteryCapacityKwh(), e.electricRangeKm(),
      units
    );
  }
}
