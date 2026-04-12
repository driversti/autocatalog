package live.yurii.autocatalog.infrastructure.persistence.variant;

import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.variant.Drivetrain;
import live.yurii.autocatalog.domain.variant.Variant;
import live.yurii.autocatalog.domain.variant.VariantId;
import live.yurii.autocatalog.domain.variant.VariantRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class VariantRepositoryAdapter implements VariantRepository {

  private final VariantJpaRepository jpa;

  public VariantRepositoryAdapter(VariantJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Variant save(Variant variant) {
    var entity = toEntity(variant);
    entity.engineIds().addAll(variant.engineIds().stream().map(EngineId::value).toList());
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
  public boolean existsByBodyIdAndTransmissionIdAndDrivetrain(BodyId bodyId,
                                                              TransmissionId transmissionId,
                                                              Drivetrain drivetrain) {
    return jpa.existsByBodyIdAndTransmissionIdAndDrivetrain(
      bodyId.value(), transmissionId.value(), drivetrain);
  }

  @Override
  public void deleteById(VariantId id) {
    jpa.deleteById(id.value());
  }

  private VariantJpaEntity toEntity(Variant v) {
    return new VariantJpaEntity(
      v.id().value(), v.bodyId().value(), v.transmissionId().value(),
      v.drivetrain(), v.groundClearanceMm(), v.curbWeightKg(), v.systemPowerKw()
    );
  }

  private Variant toDomain(VariantJpaEntity e) {
    Set<EngineId> engineIds = e.engineIds().stream()
      .map(EngineId::new)
      .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    return Variant.reconstitute(
      new VariantId(e.id()),
      new BodyId(e.bodyId()),
      engineIds,
      new TransmissionId(e.transmissionId()),
      e.drivetrain(),
      e.groundClearanceMm(),
      e.curbWeightKg(),
      e.systemPowerKw(),
      e.markets()
    );
  }
}
