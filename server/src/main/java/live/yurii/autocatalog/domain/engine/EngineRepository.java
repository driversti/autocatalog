package live.yurii.autocatalog.domain.engine;

import java.util.List;
import java.util.Optional;

public interface EngineRepository {
  Engine save(Engine engine);

  Optional<Engine> findById(EngineId id);

  Optional<Engine> findByCode(String code);

  List<Engine> findAll();

  List<Engine> findByFuelType(FuelType fuelType);

  boolean existsByCode(String code);

  void deleteById(EngineId id);
}
