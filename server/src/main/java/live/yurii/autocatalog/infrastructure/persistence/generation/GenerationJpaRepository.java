package live.yurii.autocatalog.infrastructure.persistence.generation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface GenerationJpaRepository extends JpaRepository<GenerationJpaEntity, UUID> {
  List<GenerationJpaEntity> findByModelId(UUID modelId);

  boolean existsByModelIdAndName(UUID modelId, String name);

  boolean existsByModelId(UUID modelId);
}
