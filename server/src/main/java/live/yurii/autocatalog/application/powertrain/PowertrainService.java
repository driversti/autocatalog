package live.yurii.autocatalog.application.powertrain;

import live.yurii.autocatalog.domain.powertrain.DrivetrainType;
import live.yurii.autocatalog.domain.powertrain.Powertrain;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.powertrain.PowertrainRepository;
import live.yurii.autocatalog.domain.powertrain.PowertrainUnit;
import live.yurii.autocatalog.domain.powertrain.UnitRole;
import live.yurii.autocatalog.domain.powerunit.PowerUnitId;
import live.yurii.autocatalog.domain.powerunit.PowerUnitRepository;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.variant.VariantRepository;

import java.math.BigDecimal;
import java.util.List;

public class PowertrainService {

  private final PowertrainRepository powertrainRepository;
  private final PowerUnitRepository powerUnitRepository;
  private final VariantRepository variantRepository;

  public PowertrainService(PowertrainRepository powertrainRepository,
                           PowerUnitRepository powerUnitRepository,
                           VariantRepository variantRepository) {
    this.powertrainRepository = powertrainRepository;
    this.powerUnitRepository = powerUnitRepository;
    this.variantRepository = variantRepository;
  }

  /**
   * Creates a new Powertrain after validating all referenced power units exist
   * and that the name is not already taken.
   *
   * @param name               human-readable label
   * @param drivetrainType     overall propulsion strategy
   * @param combinedPowerHp    combined system power in HP; optional
   * @param combinedTorqueNm   combined system torque in Nm; optional but must be > 0 when present
   * @param batteryCapacityKwh battery capacity; must be paired with electricRangeKm
   * @param electricRangeKm    electric range; must be paired with batteryCapacityKwh
   * @param unitEntries        at least one required; exactly one must be PRIMARY
   */
  public Powertrain create(String name, DrivetrainType drivetrainType,
                           Integer combinedPowerHp, Integer combinedTorqueNm,
                           BigDecimal batteryCapacityKwh, Integer electricRangeKm,
                           List<UnitEntry> unitEntries) {
    if (powertrainRepository.existsByName(name))
      throw new IllegalStateException("Powertrain already exists with name: " + name);

    for (var entry : unitEntries) {
      if (!powerUnitRepository.existsById(entry.powerUnitId()))
        throw new EntityNotFoundException("PowerUnit not found: " + entry.powerUnitId().value());
    }

    var units = unitEntries.stream()
      .map(e -> new PowertrainUnit(e.powerUnitId(), e.role()))
      .toList();

    var powertrain = Powertrain.create(name, drivetrainType, combinedPowerHp, combinedTorqueNm,
      batteryCapacityKwh, electricRangeKm, units);
    return powertrainRepository.save(powertrain);
  }

  public Powertrain getById(PowertrainId id) {
    return powertrainRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Powertrain not found: " + id.value()));
  }

  public List<Powertrain> getAll() {
    return powertrainRepository.findAll();
  }

  public void deleteById(PowertrainId id) {
    getById(id);
    if (variantRepository.existsByPowertrainId(id))
      throw new IllegalStateException("Cannot delete powertrain: referenced by variant(s)");
    powertrainRepository.deleteById(id);
  }

  public Powertrain rename(PowertrainId id, String newName) {
    var powertrain = getById(id);
    if (powertrainRepository.existsByName(newName))
      throw new IllegalStateException("Powertrain already exists with name: " + newName);
    powertrain.rename(newName);
    return powertrainRepository.save(powertrain);
  }

  // --- nested command type ---

  public record UnitEntry(PowerUnitId powerUnitId, UnitRole role) {
  }
}
