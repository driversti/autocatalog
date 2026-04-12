package live.yurii.autocatalog.infrastructure.persistence.engine;

import live.yurii.autocatalog.domain.engine.Engine;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.engine.EngineRepository;
import live.yurii.autocatalog.domain.engine.FuelType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EngineRepositoryAdapter implements EngineRepository {

  private final EngineJpaRepository jpa;

  public EngineRepositoryAdapter(EngineJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Engine save(Engine engine) {
    jpa.save(toEntity(engine));
    return engine;
  }

  @Override
  public Optional<Engine> findById(EngineId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public Optional<Engine> findByCode(String code) {
    return jpa.findByCode(code).map(this::toDomain);
  }

  @Override
  public List<Engine> findAll() {
    return jpa.findAll().stream().map(this::toDomain).toList();
  }

  @Override
  public List<Engine> findByFuelType(FuelType fuelType) {
    return jpa.findByFuelType(fuelType).stream().map(this::toDomain).toList();
  }

  @Override
  public boolean existsByCode(String code) {
    return jpa.existsByCode(code);
  }

  @Override
  public void deleteById(EngineId id) {
    jpa.deleteById(id.value());
  }

  private EngineJpaEntity toEntity(Engine e) {
    return new EngineJpaEntity(
      e.id().value(), e.code(), e.name(), e.fuelType(),
      e.displacementCc(), e.powerKw(), e.torqueNm(),
      e.cylinderCount(), e.configuration(), e.systemPowerKw()
    );
  }

  private Engine toDomain(EngineJpaEntity e) {
    return Engine.reconstitute(
      new EngineId(e.id()), e.code(), e.name(), e.fuelType(),
      e.displacementCc(), e.powerKw(), e.torqueNm(),
      e.cylinderCount(), e.configuration(), e.systemPowerKw()
    );
  }
}
