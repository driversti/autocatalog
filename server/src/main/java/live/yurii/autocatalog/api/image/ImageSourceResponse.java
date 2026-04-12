package live.yurii.autocatalog.api.image;

import live.yurii.autocatalog.infrastructure.persistence.image.ImageSourceJpaEntity;

import java.time.LocalDateTime;

public record ImageSourceResponse(
  Long id,
  String imageUrl,
  String sourceUrl,
  String author,
  String license,
  String licenseUrl,
  String copyright,
  String usageTerms,
  LocalDateTime obtainedAt
) {
  public static ImageSourceResponse from(ImageSourceJpaEntity entity) {
    return new ImageSourceResponse(
      entity.id(), entity.imageUrl(), entity.sourceUrl(), entity.author(),
      entity.license(), entity.licenseUrl(), entity.copyright(),
      entity.usageTerms(), entity.obtainedAt()
    );
  }
}
