package live.yurii.autocatalog.infrastructure.persistence.engine;

import live.yurii.autocatalog.domain.engine.Engine;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.engine.EngineRepository;
import live.yurii.autocatalog.domain.engine.FuelType;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaEntity;
import live.yurii.autocatalog.domain.powerunit.PowerUnitType;
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
    EngineJpaEntity entity = toEntity(engine);
    EngineJpaEntity saved = jpa.save(entity);
    // After save, the DB-generated ID is available via saved.id()
    return toDomain(saved);
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
    // ON DELETE CASCADE on power_unit FK removes the power_unit row automatically
  }

  private EngineJpaEntity toEntity(Engine e) {
    // Always create a fresh PowerUnitJpaEntity for ICE; cascade will persist it.
    // If the engine already has an ID (update scenario), we load the existing unit
    // by constructing an entity with the existing ID set via @MapsId path.
    var unit = new PowerUnitJpaEntity(PowerUnitType.ICE);
    return new EngineJpaEntity(
      unit, e.code(), e.name(), e.fuelType(),
      e.displacementCc(), e.powerKw(), e.torqueNm(),
      e.cylinderCount(), e.configuration(), e.systemPowerKw(),
      e.compressionRatio(), e.turbo()
    );
  }

  private Engine toDomain(EngineJpaEntity e) {
    return Engine.reconstitute(
      new EngineId(e.id()), e.code(), e.name(), e.fuelType(),
      e.displacementCc(), e.powerKw(), e.torqueNm(),
      e.cylinderCount(), e.configuration(), e.systemPowerKw(),
      e.compressionRatio(), e.turbo()
    );
  }
}
