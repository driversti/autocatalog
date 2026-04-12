package live.yurii.autocatalog.domain.body;

import live.yurii.autocatalog.domain.generation.GenerationId;

import java.util.List;
import java.util.Optional;

public interface BodyRepository {
  Body save(Body body);

  Optional<Body> findById(BodyId id);

  List<Body> findByGenerationId(GenerationId generationId);

  boolean existsByGenerationIdAndBodyStyle(GenerationId generationId, BodyStyle bodyStyle);

  void deleteById(BodyId id);
}
