package live.yurii.autocatalog.infrastructure.persistence.body;

import live.yurii.autocatalog.domain.body.BodyStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface BodyJpaRepository extends JpaRepository<BodyJpaEntity, UUID> {
  List<BodyJpaEntity> findByGenerationId(UUID generationId);

  boolean existsByGenerationIdAndBodyStyle(UUID generationId, BodyStyle bodyStyle);
}
