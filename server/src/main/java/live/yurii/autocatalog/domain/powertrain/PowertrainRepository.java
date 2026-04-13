package live.yurii.autocatalog.domain.powertrain;

import live.yurii.autocatalog.domain.powerunit.PowerUnitId;

import java.util.List;
import java.util.Optional;

public interface PowertrainRepository {
  Powertrain save(Powertrain powertrain);

  Optional<Powertrain> findById(PowertrainId id);

  Optional<Powertrain> findByName(String name);

  List<Powertrain> findAll();

  boolean existsByName(String name);

  /**
   * Returns true if any powertrain references the given power unit via powertrain_unit.
   * Used as a delete guard before removing an Engine, ElectricMotor, or FuelCellStack.
   */
  boolean existsByPowerUnitId(PowerUnitId powerUnitId);

  void deleteById(PowertrainId id);
}
