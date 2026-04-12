package live.yurii.autocatalog.application.engine;

import live.yurii.autocatalog.application.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.engine.Engine;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.engine.EngineRepository;
import live.yurii.autocatalog.domain.engine.FuelType;

import java.util.List;

public class EngineService {

  private final EngineRepository engineRepository;

  public EngineService(EngineRepository engineRepository) {
    this.engineRepository = engineRepository;
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
}
