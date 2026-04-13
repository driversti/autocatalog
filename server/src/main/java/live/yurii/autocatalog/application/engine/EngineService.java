package live.yurii.autocatalog.application.engine;

import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.engine.Engine;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.engine.EngineRepository;
import live.yurii.autocatalog.domain.engine.FuelType;
import live.yurii.autocatalog.domain.powertrain.PowertrainRepository;
import live.yurii.autocatalog.domain.powerunit.PowerUnitId;

import java.util.List;

public class EngineService {

  private final EngineRepository engineRepository;
  private final PowertrainRepository powertrainRepository;

  public EngineService(EngineRepository engineRepository, PowertrainRepository powertrainRepository) {
    this.engineRepository = engineRepository;
    this.powertrainRepository = powertrainRepository;
  }

  public Engine create(String code, String name, FuelType fuelType,
                       Integer displacementCc, int powerKw, Integer torqueNm,
                       Integer cylinderCount, String configuration,
                       Integer systemPowerKw) {
    if (engineRepository.existsByCode(code))
      throw new IllegalStateException("Engine already exists: " + code);
    var engine = Engine.create(code, name, fuelType, displacementCc, powerKw, torqueNm);
    if (cylinderCount != null) engine.setCylinderCount(cylinderCount);
    if (configuration != null) engine.setConfiguration(configuration);
    if (systemPowerKw != null) engine.setSystemPowerKw(systemPowerKw);
    return engineRepository.save(engine);
  }

  public Engine getById(EngineId id) {
    return engineRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Engine not found: " + id.value()));
  }

  public List<Engine> getAll() {
    return engineRepository.findAll();
  }

  public List<Engine> getByFuelType(FuelType fuelType) {
    return engineRepository.findByFuelType(fuelType);
  }

  public void deleteById(EngineId id) {
    Engine engine = getById(id);
    // Guard: check via powertrain_unit table (the engine's power_unit_id)
    if (powertrainRepository.existsByPowerUnitId(new PowerUnitId(engine.id().value())))
      throw new IllegalStateException("Cannot delete engine: referenced by powertrain(s)");
    engineRepository.deleteById(id);
    // ON DELETE CASCADE on power_unit FK removes the power_unit row automatically
  }

  public Engine rename(EngineId id, String newName) {
    var engine = getById(id);
    engine.rename(newName);
    return engineRepository.save(engine);
  }
}
