package live.yurii.autocatalog.infrastructure.persistence.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "car_models")
class CarModelJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "make_id", nullable = false, updatable = false)
  private UUID makeId;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, unique = true, length = 100)
  private String slug;

  @OneToMany(
    mappedBy = "fromModel",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.EAGER
  )
  private List<ModelRelationJpaEntity> relations = new ArrayList<>();

  protected CarModelJpaEntity() {
  }

  CarModelJpaEntity(UUID id, UUID makeId, String name, String slug) {
    this.id = id;
    this.makeId = makeId;
    this.name = name;
    this.slug = slug;
  }

  UUID id() {
    return id;
  }

  UUID makeId() {
    return makeId;
  }

  String name() {
    return name;
  }

  String slug() {
    return slug;
  }

  List<ModelRelationJpaEntity> relations() {
    return relations;
  }

  void setRelations(List<ModelRelationJpaEntity> relations) {
    this.relations.clear();
    this.relations.addAll(relations);
  }
}
