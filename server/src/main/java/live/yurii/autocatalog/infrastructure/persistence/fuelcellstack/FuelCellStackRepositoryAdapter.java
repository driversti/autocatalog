package live.yurii.autocatalog.infrastructure.persistence.fuelcellstack;

import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStack;
import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStackId;
import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStackRepository;
import live.yurii.autocatalog.domain.powerunit.PowerUnitType;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FuelCellStackRepositoryAdapter implements FuelCellStackRepository {

  private final FuelCellStackJpaRepository jpa;

  public FuelCellStackRepositoryAdapter(FuelCellStackJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public FuelCellStack save(FuelCellStack stack) {
    FuelCellStackJpaEntity saved = jpa.save(toEntity(stack));
    return toDomain(saved);
  }

  @Override
  public Optional<FuelCellStack> findById(FuelCellStackId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public List<FuelCellStack> findAll() {
    return jpa.findAll().stream().map(this::toDomain).toList();
  }

  @Override
  public void deleteById(FuelCellStackId id) {
    jpa.deleteById(id.value());
  }

  private FuelCellStackJpaEntity toEntity(FuelCellStack s) {
    var unit = new PowerUnitJpaEntity(PowerUnitType.FUEL_CELL);
    return new FuelCellStackJpaEntity(unit, s.label(), s.peakPowerKw(), s.hydrogenTankKg());
  }

  private FuelCellStack toDomain(FuelCellStackJpaEntity e) {
    return FuelCellStack.reconstitute(
      new FuelCellStackId(e.id()), e.label(), e.peakPowerKw(), e.hydrogenTankKg()
    );
  }
}
