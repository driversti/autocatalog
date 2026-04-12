package live.yurii.autocatalog.infrastructure.persistence.model;

import live.yurii.autocatalog.domain.make.MakeId;
import live.yurii.autocatalog.domain.model.CarModel;
import live.yurii.autocatalog.domain.model.CarModelRepository;
import live.yurii.autocatalog.domain.model.ModelId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CarModelRepositoryAdapter implements CarModelRepository {

  private final CarModelJpaRepository jpa;

  public CarModelRepositoryAdapter(CarModelJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public CarModel save(CarModel model) {
    var entity = toEntity(model);
    jpa.save(entity);
    return model;
  }

  @Override
  public Optional<CarModel> findById(ModelId id) {
    return jpa.findById(id.value()).map(this::toDomain);
  }

  @Override
  public Optional<CarModel> findBySlug(String slug) {
    return jpa.findBySlug(slug).map(this::toDomain);
  }

  @Override
  public List<CarModel> findByMakeId(MakeId makeId) {
    return jpa.findByMakeId(makeId.value()).stream().map(this::toDomain).toList();
  }

  @Override
  public boolean existsByMakeIdAndName(MakeId makeId, String name) {
    return jpa.existsByMakeIdAndName(makeId.value(), name);
  }

  @Override
  public void deleteById(ModelId id) {
    jpa.deleteById(id.value());
  }

  private CarModelJpaEntity toEntity(CarModel model) {
    var entity = new CarModelJpaEntity(
      model.id().value(), model.makeId().value(), model.name(), model.slug()
    );
    var relations = model.relations().stream()
      .map(r -> new ModelRelationJpaEntity(entity, r.targetModelId().value(), r.type(), r.note()))
      .toList();
    entity.setRelations(relations);
    return entity;
  }

  private CarModel toDomain(CarModelJpaEntity e) {
    var model = CarModel.reconstitute(
      new ModelId(e.id()), new MakeId(e.makeId()), e.name(), e.slug()
    );
    e.relations().forEach(r -> model.addRelation(new ModelId(r.toModelId()), r.type(), r.note()));
    return model;
  }
}
