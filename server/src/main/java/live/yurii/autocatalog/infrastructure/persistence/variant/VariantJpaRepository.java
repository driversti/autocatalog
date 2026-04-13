package live.yurii.autocatalog.infrastructure.persistence.variant;

import live.yurii.autocatalog.domain.variant.Drivetrain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface VariantJpaRepository extends JpaRepository<VariantJpaEntity, UUID> {
  List<VariantJpaEntity> findByBodyId(UUID bodyId);

  boolean existsByBodyIdAndPowertrainIdAndTransmissionIdAndDrivetrain(
    UUID bodyId, UUID powertrainId, UUID transmissionId, Drivetrain drivetrain);

  boolean existsByBodyId(UUID bodyId);

  boolean existsByPowertrainId(UUID powertrainId);

  boolean existsByTransmissionId(UUID transmissionId);
}
