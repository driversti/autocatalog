package live.yurii.autocatalog.application.image;

import live.yurii.autocatalog.api.image.ImageSourceRequest;
import live.yurii.autocatalog.api.image.ImageSourceResponse;
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

  public ImageSourceResponse getByImageUrl(String imageUrl) {
    return repository.findByImageUrl(imageUrl)
      .map(ImageSourceResponse::from)
      .orElseThrow(() -> new EntityNotFoundException("Image source not found: " + imageUrl));
  }

  public ImageSourceResponse save(ImageSourceRequest request) {
    var entity = new ImageSourceJpaEntity(
      request.imageUrl(), request.sourceUrl(), request.author(),
      request.license(), request.licenseUrl(), request.copyright(),
      request.usageTerms()
    );
    return ImageSourceResponse.from(repository.save(entity));
  }

  public Page<ImageSourceResponse> findAll(Pageable pageable) {
    return repository.findAll(pageable).map(ImageSourceResponse::from);
  }

  public ImageSourceResponse getById(Long id) {
    return repository.findById(id)
      .map(ImageSourceResponse::from)
      .orElseThrow(() -> new EntityNotFoundException("ImageSource not found: " + id));
  }

  public void deleteById(Long id) {
    if (!repository.existsById(id))
      throw new EntityNotFoundException("ImageSource not found: " + id);
    repository.deleteById(id);
  }

  public ImageSourceResponse update(Long id, ImageSourceRequest request) {
    var entity = repository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("ImageSource not found: " + id));
    entity.updateMetadata(request.author(), request.license(), request.licenseUrl(),
      request.copyright(), request.usageTerms());
    repository.save(entity);
    return ImageSourceResponse.from(entity);
  }
}
