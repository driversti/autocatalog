package live.yurii.autocatalog.application.electricmotor;

import live.yurii.autocatalog.domain.electricmotor.ElectricMotor;
import live.yurii.autocatalog.domain.electricmotor.ElectricMotorId;
import live.yurii.autocatalog.domain.electricmotor.ElectricMotorRepository;
import live.yurii.autocatalog.domain.electricmotor.MotorType;
import live.yurii.autocatalog.domain.powertrain.PowertrainRepository;
import live.yurii.autocatalog.domain.powerunit.PowerUnitId;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;

import java.util.List;

public class ElectricMotorService {

  private final ElectricMotorRepository motorRepository;
  private final PowertrainRepository powertrainRepository;

  public ElectricMotorService(ElectricMotorRepository motorRepository,
                              PowertrainRepository powertrainRepository) {
    this.motorRepository = motorRepository;
    this.powertrainRepository = powertrainRepository;
  }

  public ElectricMotor create(String label, Integer powerKw, Integer torqueNm, MotorType motorType) {
    var motor = ElectricMotor.create(label, powerKw, torqueNm, motorType);
    return motorRepository.save(motor);
  }

  public ElectricMotor getById(ElectricMotorId id) {
    return motorRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("ElectricMotor not found: " + id.value()));
  }

  public List<ElectricMotor> getAll() {
    return motorRepository.findAll();
  }

  public ElectricMotor updateLabel(ElectricMotorId id, String newLabel) {
    var motor = getById(id);
    motor.updateLabel(newLabel);
    return motorRepository.save(motor);
  }

  public ElectricMotor updateSpecs(ElectricMotorId id, Integer powerKw, Integer torqueNm,
                                   MotorType motorType) {
    var motor = getById(id);
    motor.updateSpecs(powerKw, torqueNm, motorType);
    return motorRepository.save(motor);
  }

  public void deleteById(ElectricMotorId id) {
    ElectricMotor motor = getById(id);
    if (powertrainRepository.existsByPowerUnitId(new PowerUnitId(motor.id().value())))
      throw new IllegalStateException("Cannot delete electric motor: referenced by powertrain(s)");
    motorRepository.deleteById(id);
  }
}
