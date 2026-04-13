package live.yurii.autocatalog.infrastructure.persistence.engine;

import live.yurii.autocatalog.domain.engine.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface EngineJpaRepository extends JpaRepository<EngineJpaEntity, Long> {
  Optional<EngineJpaEntity> findByCode(String code);

  List<EngineJpaEntity> findByFuelType(FuelType fuelType);

  boolean existsByCode(String code);
}
