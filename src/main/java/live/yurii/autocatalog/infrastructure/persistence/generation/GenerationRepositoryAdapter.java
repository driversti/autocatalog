package live.yurii.autocatalog.infrastructure.persistence.generation;

import live.yurii.autocatalog.domain.generation.Generation;
import live.yurii.autocatalog.domain.generation.GenerationId;
import live.yurii.autocatalog.domain.generation.GenerationRepository;
import live.yurii.autocatalog.domain.model.ModelId;
import live.yurii.autocatalog.domain.shared.YearRange;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GenerationRepositoryAdapter implements GenerationRepository {

  private final GenerationJpaRepository jpa;

  public GenerationRepositoryAdapter(GenerationJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Generation save(Generation generation) {
    jpa.save(toEntity(generation));
    return generation;
  }

  @Override
  public Optional<Generation> findById(GenerationId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public List<Generation> findByModelId(ModelId modelId) {
    return jpa.findByModelId(modelId.value()).stream().map(this::toDomain).toList();
  }

  @Override
  public boolean existsByModelIdAndName(ModelId modelId, String name) {
    return jpa.existsByModelIdAndName(modelId.value(), name);
  }

  @Override
  public void deleteById(GenerationId id) {
    jpa.deleteById(id.value());
  }

  private GenerationJpaEntity toEntity(Generation g) {
    return new GenerationJpaEntity(
      g.id().value(), g.modelId().value(), g.name(), g.years().from(), g.years().to()
    );
  }

  private Generation toDomain(GenerationJpaEntity e) {
    return Generation.reconstitute(
      new GenerationId(e.id()), new ModelId(e.modelId()),
      e.name(), new YearRange(e.yearFrom(), e.yearTo())
    );
  }
}
