package live.yurii.autocatalog.domain.electricmotor;

import java.util.List;
import java.util.Optional;

public interface ElectricMotorRepository {
  ElectricMotor save(ElectricMotor motor);

  Optional<ElectricMotor> findById(ElectricMotorId id);

  List<ElectricMotor> findAll();

  void deleteById(ElectricMotorId id);
}
