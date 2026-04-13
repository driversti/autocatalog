package live.yurii.autocatalog.domain.generation;

import live.yurii.autocatalog.domain.model.ModelId;

import java.util.List;
import java.util.Optional;

public interface GenerationRepository {
  Generation save(Generation generation);

  Optional<Generation> findById(GenerationId id);

  List<Generation> findByModelId(ModelId modelId);

  boolean existsByModelIdAndName(ModelId modelId, String name);

  boolean existsByModelId(ModelId modelId);

  void deleteById(GenerationId id);
}
