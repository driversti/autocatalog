package live.yurii.autocatalog.infrastructure.persistence.body;

import live.yurii.autocatalog.domain.body.Body;
import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.body.BodyRepository;
import live.yurii.autocatalog.domain.body.BodyStyle;
import live.yurii.autocatalog.domain.generation.GenerationId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BodyRepositoryAdapter implements BodyRepository {

  private final BodyJpaRepository jpa;

  public BodyRepositoryAdapter(BodyJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Body save(Body body) {
    jpa.save(toEntity(body));
    return body;
  }

  @Override
  public Optional<Body> findById(BodyId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public List<Body> findByGenerationId(GenerationId generationId) {
    return jpa.findByGenerationId(generationId.value()).stream().map(this::toDomain).toList();
  }

  @Override
  public boolean existsByGenerationIdAndBodyStyle(GenerationId generationId, BodyStyle bodyStyle) {
    return jpa.existsByGenerationIdAndBodyStyle(generationId.value(), bodyStyle);
  }

  @Override
  public void deleteById(BodyId id) {
    jpa.deleteById(id.value());
  }

  private BodyJpaEntity toEntity(Body b) {
    return new BodyJpaEntity(
      b.id().value(), b.generationId().value(), b.bodyStyle(),
      b.lengthMm(), b.widthMm(), b.heightMm(), b.wheelbaseMm(),
      b.trunkVolumeLitres(), b.groundClearanceMm()
    );
  }

  private Body toDomain(BodyJpaEntity e) {
    return Body.reconstitute(
      new BodyId(e.id()), new GenerationId(e.generationId()), e.bodyStyle(),
      e.lengthMm(), e.widthMm(), e.heightMm(), e.wheelbaseMm(),
      e.trunkVolumeLitres(), e.groundClearanceMm()
    );
  }
}
