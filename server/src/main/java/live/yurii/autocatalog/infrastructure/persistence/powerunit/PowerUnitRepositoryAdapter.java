package live.yurii.autocatalog.infrastructure.persistence.powerunit;

import live.yurii.autocatalog.domain.powerunit.PowerUnit;
import live.yurii.autocatalog.domain.powerunit.PowerUnitId;
import live.yurii.autocatalog.domain.powerunit.PowerUnitRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PowerUnitRepositoryAdapter implements PowerUnitRepository {

  private final PowerUnitJpaRepository jpa;

  public PowerUnitRepositoryAdapter(PowerUnitJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Optional<PowerUnit> findById(PowerUnitId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public boolean existsById(PowerUnitId id) {
    return jpa.existsById(id.value());
  }

  private PowerUnit toDomain(PowerUnitJpaEntity e) {
    return PowerUnit.reconstitute(new PowerUnitId(e.id()), e.unitType());
  }
}
