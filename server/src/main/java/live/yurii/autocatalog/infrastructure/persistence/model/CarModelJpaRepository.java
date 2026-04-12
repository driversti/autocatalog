package live.yurii.autocatalog.infrastructure.persistence.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface CarModelJpaRepository extends JpaRepository<CarModelJpaEntity, UUID> {
  Optional<CarModelJpaEntity> findBySlug(String slug);

  List<CarModelJpaEntity> findByMakeId(UUID makeId);

  boolean existsByMakeIdAndName(UUID makeId, String name);
}
