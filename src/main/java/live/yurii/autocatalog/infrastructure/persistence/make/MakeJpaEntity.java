package live.yurii.autocatalog.infrastructure.persistence.make;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "makes")
class MakeJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @Column(nullable = false, length = 100)
  private String country;

  @Column(nullable = false, unique = true, length = 100)
  private String slug;

  protected MakeJpaEntity() {
  }

  MakeJpaEntity(UUID id, String name, String country, String slug) {
    this.id = id;
    this.name = name;
    this.country = country;
    this.slug = slug;
  }

  UUID id() {
    return id;
  }

  String name() {
    return name;
  }

  String country() {
    return country;
  }

  String slug() {
    return slug;
  }
}
