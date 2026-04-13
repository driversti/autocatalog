package live.yurii.autocatalog.infrastructure.persistence.powertrain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface PowertrainJpaRepository extends JpaRepository<PowertrainJpaEntity, UUID> {
  Optional<PowertrainJpaEntity> findByName(String name);

  boolean existsByName(String name);

  /**
   * Returns true if any powertrain_unit row references the given power_unit_id.
   * Used as delete guard for Engine/ElectricMotor/FuelCellStack.
   */
  boolean existsByUnits_PowerUnit_Id(Long powerUnitId);
}
