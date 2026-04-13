package live.yurii.autocatalog.infrastructure.persistence.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageSourceJpaRepository extends JpaRepository<ImageSourceJpaEntity, Long> {
  Optional<ImageSourceJpaEntity> findByImageUrl(String imageUrl);
}
