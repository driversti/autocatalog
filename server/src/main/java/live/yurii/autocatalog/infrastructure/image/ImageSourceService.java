package live.yurii.autocatalog.infrastructure.image;

import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.infrastructure.persistence.image.ImageSourceJpaEntity;
import live.yurii.autocatalog.infrastructure.persistence.image.ImageSourceJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ImageSourceService {

  private final ImageSourceJpaRepository repository;

  public ImageSourceService(ImageSourceJpaRepository repository) {
    this.repository = repository;
  }

  public ImageSourceJpaEntity getByImageUrl(String imageUrl) {
    return repository.findByImageUrl(imageUrl)
      .orElseThrow(() -> new EntityNotFoundException("Image source not found: " + imageUrl));
  }

  public ImageSourceJpaEntity save(ImageSourceJpaEntity entity) {
    return repository.save(entity);
  }

  public Page<ImageSourceJpaEntity> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }
}
