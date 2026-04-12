package live.yurii.autocatalog.api.generation;

import live.yurii.autocatalog.domain.generation.Generation;

import java.util.UUID;

public record GenerationResponse(
  UUID id,
  UUID modelId,
  String name,
  int yearFrom,
  Integer yearTo
) {
  public static GenerationResponse from(Generation g) {
    return new GenerationResponse(
      g.id().value(),
      g.modelId().value(),
      g.name(),
      g.years().from(),
      g.years().to()
    );
  }
}
