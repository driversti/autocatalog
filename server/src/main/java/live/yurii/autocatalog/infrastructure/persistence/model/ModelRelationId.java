package live.yurii.autocatalog.infrastructure.persistence.model;

import live.yurii.autocatalog.domain.model.ModelRelation;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

class ModelRelationId implements Serializable {

  private CarModelJpaEntity fromModel;
  private UUID toModelId;
  private ModelRelation.Type type;

  protected ModelRelationId() {
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromModel, toModelId, type);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ModelRelationId other)) return false;
    return Objects.equals(fromModel, other.fromModel)
      && Objects.equals(toModelId, other.toModelId)
      && type == other.type;
  }
}
