package live.yurii.autocatalog.infrastructure.persistence.transmission;

import live.yurii.autocatalog.domain.transmission.TransmissionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface TransmissionJpaRepository extends JpaRepository<TransmissionJpaEntity, UUID> {
  List<TransmissionJpaEntity> findByType(TransmissionType type);

  boolean existsByTypeAndGearCount(TransmissionType type, int gearCount);
}
