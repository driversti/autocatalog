package live.yurii.autocatalog.infrastructure.persistence.engine;

import live.yurii.autocatalog.domain.engine.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface EngineJpaRepository extends JpaRepository<EngineJpaEntity, UUID> {
  Optional<EngineJpaEntity> findByCode(String code);

  List<EngineJpaEntity> findByFuelType(FuelType fuelType);

  boolean existsByCode(String code);
}
