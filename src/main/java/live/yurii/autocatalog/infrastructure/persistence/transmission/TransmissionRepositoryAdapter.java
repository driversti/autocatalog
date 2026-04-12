package live.yurii.autocatalog.infrastructure.persistence.transmission;

import live.yurii.autocatalog.domain.transmission.Transmission;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.transmission.TransmissionRepository;
import live.yurii.autocatalog.domain.transmission.TransmissionType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransmissionRepositoryAdapter implements TransmissionRepository {

  private final TransmissionJpaRepository jpa;

  public TransmissionRepositoryAdapter(TransmissionJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Transmission save(Transmission transmission) {
    jpa.save(toEntity(transmission));
    return transmission;
  }

  @Override
  public Optional<Transmission> findById(TransmissionId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public List<Transmission> findAll() {
    return jpa.findAll().stream().map(this::toDomain).toList();
  }

  @Override
  public List<Transmission> findByType(TransmissionType type) {
    return jpa.findByType(type).stream().map(this::toDomain).toList();
  }

  @Override
  public boolean existsByTypeAndGearCount(TransmissionType type, int gearCount) {
    return jpa.existsByTypeAndGearCount(type, gearCount);
  }

  @Override
  public void deleteById(TransmissionId id) {
    jpa.deleteById(id.value());
  }

  private TransmissionJpaEntity toEntity(Transmission t) {
    return new TransmissionJpaEntity(t.id().value(), t.type(), t.gearCount());
  }

  private Transmission toDomain(TransmissionJpaEntity e) {
    return Transmission.reconstitute(new TransmissionId(e.id()), e.type(), e.gearCount());
  }
}
