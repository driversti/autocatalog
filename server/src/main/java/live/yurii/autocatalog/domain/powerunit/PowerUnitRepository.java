package live.yurii.autocatalog.domain.powerunit;

import java.util.Optional;

public interface PowerUnitRepository {
  Optional<PowerUnit> findById(PowerUnitId id);

  boolean existsById(PowerUnitId id);
}
