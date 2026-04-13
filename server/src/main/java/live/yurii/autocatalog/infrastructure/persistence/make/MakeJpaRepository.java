package live.yurii.autocatalog.infrastructure.persistence.make;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface MakeJpaRepository extends JpaRepository<MakeJpaEntity, UUID> {
  Optional<MakeJpaEntity> findBySlug(String slug);

  boolean existsByName(String name);
}
