package live.yurii.autocatalog.infrastructure.persistence.variant;

import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.variant.Drivetrain;
import live.yurii.autocatalog.domain.variant.Variant;
import live.yurii.autocatalog.domain.variant.VariantId;
import live.yurii.autocatalog.domain.variant.VariantRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VariantRepositoryAdapter implements VariantRepository {

  private final VariantJpaRepository jpa;

  public VariantRepositoryAdapter(VariantJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Variant save(Variant variant) {
    var entity = toEntity(variant);
    entity.markets().addAll(variant.markets());
    jpa.save(entity);
    return variant;
  }

  @Override
  public Optional<Variant> findById(VariantId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public List<Variant> findByBodyId(BodyId bodyId) {
    return jpa.findByBodyId(bodyId.value()).stream().map(this::toDomain).toList();
  }

  @Override
  public boolean existsByBodyIdAndPowertrainIdAndTransmissionIdAndDrivetrain(
    BodyId bodyId, PowertrainId powertrainId,
    TransmissionId transmissionId, Drivetrain drivetrain) {
    return jpa.existsByBodyIdAndPowertrainIdAndTransmissionIdAndDrivetrain(
      bodyId.value(), powertrainId.value(), transmissionId.value(), drivetrain);
  }

  @Override
  public boolean existsByBodyId(BodyId bodyId) {
    return jpa.existsByBodyId(bodyId.value());
  }

  @Override
  public boolean existsByPowertrainId(PowertrainId powertrainId) {
    return jpa.existsByPowertrainId(powertrainId.value());
  }

  @Override
  public boolean existsByTransmissionId(TransmissionId transmissionId) {
    return jpa.existsByTransmissionId(transmissionId.value());
  }

  @Override
  public void deleteById(VariantId id) {
    jpa.deleteById(id.value());
  }

  private VariantJpaEntity toEntity(Variant v) {
    return new VariantJpaEntity(
      v.id().value(), v.bodyId().value(), v.powertrainId().value(),
      v.transmissionId().value(), v.drivetrain(), v.groundClearanceMm(), v.curbWeightKg()
    );
  }

  private Variant toDomain(VariantJpaEntity e) {
    return Variant.reconstitute(
      new VariantId(e.id()),
      new BodyId(e.bodyId()),
      new PowertrainId(e.powertrainId()),
      new TransmissionId(e.transmissionId()),
      e.drivetrain(),
      e.groundClearanceMm(),
      e.curbWeightKg(),
      e.markets()
    );
  }
}
