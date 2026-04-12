package live.yurii.autocatalog.infrastructure.persistence.make;

import live.yurii.autocatalog.domain.make.Make;
import live.yurii.autocatalog.domain.make.MakeId;
import live.yurii.autocatalog.domain.make.MakeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MakeRepositoryAdapter implements MakeRepository {

  private final MakeJpaRepository jpa;

  public MakeRepositoryAdapter(MakeJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Make save(Make make) {
    jpa.save(toEntity(make));
    return make;
  }

  @Override
  public Optional<Make> findById(MakeId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public Optional<Make> findBySlug(String slug) {
    return jpa.findBySlug(slug).map(this::toDomain);
  }

  @Override
  public List<Make> findAll() {
    return jpa.findAll().stream().map(this::toDomain).toList();
  }

  @Override
  public boolean existsByName(String name) {
    return jpa.existsByName(name);
  }

  @Override
  public void deleteById(MakeId id) {
    jpa.deleteById(id.value());
  }

  private MakeJpaEntity toEntity(Make make) {
    return new MakeJpaEntity(
      make.id().value(), make.name(), make.country(), make.slug()
    );
  }

  private Make toDomain(MakeJpaEntity e) {
    return Make.reconstitute(new MakeId(e.id()), e.name(), e.country(), e.slug());
  }
}
