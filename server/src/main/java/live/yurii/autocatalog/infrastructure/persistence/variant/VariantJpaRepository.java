package live.yurii.autocatalog.infrastructure.persistence.variant;

import live.yurii.autocatalog.domain.variant.Drivetrain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface VariantJpaRepository extends JpaRepository<VariantJpaEntity, UUID> {
  List<VariantJpaEntity> findByBodyId(UUID bodyId);

  boolean existsByBodyIdAndTransmissionIdAndDrivetrain(UUID bodyId, UUID transmissionId,
                                                       Drivetrain drivetrain);
}
