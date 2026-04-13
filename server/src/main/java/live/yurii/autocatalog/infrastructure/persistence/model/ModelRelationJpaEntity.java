package live.yurii.autocatalog.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import live.yurii.autocatalog.domain.model.ModelRelation;

import java.util.UUID;

@Entity
@Table(name = "car_model_relations")
@IdClass(ModelRelationId.class)
class ModelRelationJpaEntity {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_model_id")
  private CarModelJpaEntity fromModel;

  @Id
  @Column(name = "to_model_id")
  private UUID toModelId;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  private ModelRelation.Type type;

  @Column(columnDefinition = "TEXT")
  private String note;

  protected ModelRelationJpaEntity() {
  }

  ModelRelationJpaEntity(CarModelJpaEntity fromModel, UUID toModelId,
                         ModelRelation.Type type, String note) {
    this.fromModel = fromModel;
    this.toModelId = toModelId;
    this.type = type;
    this.note = note;
  }

  CarModelJpaEntity fromModel() {
    return fromModel;
  }

  UUID toModelId() {
    return toModelId;
  }

  ModelRelation.Type type() {
    return type;
  }

  String note() {
    return note;
  }
}
