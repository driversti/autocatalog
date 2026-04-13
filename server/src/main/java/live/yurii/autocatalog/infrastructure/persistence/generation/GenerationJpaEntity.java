package live.yurii.autocatalog.infrastructure.persistence.generation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "generations")
class GenerationJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "model_id", nullable = false, updatable = false)
  private UUID modelId;

  @Column(nullable = false, length = 50)
  private String name;

  @Column(name = "year_from", nullable = false)
  private int yearFrom;

  @Column(name = "year_to")
  private Integer yearTo;

  protected GenerationJpaEntity() {
  }

  GenerationJpaEntity(UUID id, UUID modelId, String name, int yearFrom, Integer yearTo) {
    this.id = id;
    this.modelId = modelId;
    this.name = name;
    this.yearFrom = yearFrom;
    this.yearTo = yearTo;
  }

  UUID id() {
    return id;
  }

  UUID modelId() {
    return modelId;
  }

  String name() {
    return name;
  }

  int yearFrom() {
    return yearFrom;
  }

  Integer yearTo() {
    return yearTo;
  }
}
