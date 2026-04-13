package live.yurii.autocatalog.infrastructure.persistence.electricmotor;

import live.yurii.autocatalog.domain.electricmotor.ElectricMotor;
import live.yurii.autocatalog.domain.electricmotor.ElectricMotorId;
import live.yurii.autocatalog.domain.electricmotor.ElectricMotorRepository;
import live.yurii.autocatalog.domain.electricmotor.MotorType;
import live.yurii.autocatalog.domain.powerunit.PowerUnitType;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ElectricMotorRepositoryAdapter implements ElectricMotorRepository {

  private final ElectricMotorJpaRepository jpa;

  public ElectricMotorRepositoryAdapter(ElectricMotorJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public ElectricMotor save(ElectricMotor motor) {
    ElectricMotorJpaEntity saved = jpa.save(toEntity(motor));
    return toDomain(saved);
  }

  @Override
  public Optional<ElectricMotor> findById(ElectricMotorId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public List<ElectricMotor> findAll() {
    return jpa.findAll().stream().map(this::toDomain).toList();
  }

  @Override
  public void deleteById(ElectricMotorId id) {
    jpa.deleteById(id.value());
  }

  private ElectricMotorJpaEntity toEntity(ElectricMotor m) {
    var unit = new PowerUnitJpaEntity(PowerUnitType.ELECTRIC);
    return new ElectricMotorJpaEntity(unit, m.label(), m.powerKw(), m.torqueNm(), m.motorType());
  }

  private ElectricMotor toDomain(ElectricMotorJpaEntity e) {
    return ElectricMotor.reconstitute(
      new ElectricMotorId(e.id()), e.label(), e.powerKw(), e.torqueNm(), e.motorType()
    );
  }
}
