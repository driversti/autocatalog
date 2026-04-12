package live.yurii.autocatalog.infrastructure.persistence.transmission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import live.yurii.autocatalog.domain.transmission.TransmissionType;

import java.util.UUID;

@Entity
@Table(name = "transmissions")
class TransmissionJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TransmissionType type;

  @Column(name = "gear_count", nullable = false)
  private int gearCount;

  protected TransmissionJpaEntity() {
  }

  TransmissionJpaEntity(UUID id, TransmissionType type, int gearCount) {
    this.id = id;
    this.type = type;
    this.gearCount = gearCount;
  }

  UUID id() {
    return id;
  }

  TransmissionType type() {
    return type;
  }

  int gearCount() {
    return gearCount;
  }
}
