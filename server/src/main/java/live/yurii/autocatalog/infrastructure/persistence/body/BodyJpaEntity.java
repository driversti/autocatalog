package live.yurii.autocatalog.infrastructure.persistence.body;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import live.yurii.autocatalog.domain.body.BodyStyle;

import java.util.UUID;

@Entity
@Table(name = "bodies")
class BodyJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "generation_id", nullable = false, updatable = false)
  private UUID generationId;

  @Enumerated(EnumType.STRING)
  @Column(name = "body_style", nullable = false, length = 20)
  private BodyStyle bodyStyle;

  @Column(name = "length_mm", nullable = false)
  private int lengthMm;

  @Column(name = "width_mm", nullable = false)
  private int widthMm;

  @Column(name = "height_mm", nullable = false)
  private int heightMm;

  @Column(name = "wheelbase_mm", nullable = false)
  private int wheelbaseMm;

  @Column(name = "trunk_volume_litres")
  private Integer trunkVolumeLitres;

  @Column(name = "ground_clearance_mm")
  private Integer groundClearanceMm;

  protected BodyJpaEntity() {
  }

  BodyJpaEntity(UUID id, UUID generationId, BodyStyle bodyStyle,
                int lengthMm, int widthMm, int heightMm, int wheelbaseMm,
                Integer trunkVolumeLitres, Integer groundClearanceMm) {
    this.id = id;
    this.generationId = generationId;
    this.bodyStyle = bodyStyle;
    this.lengthMm = lengthMm;
    this.widthMm = widthMm;
    this.heightMm = heightMm;
    this.wheelbaseMm = wheelbaseMm;
    this.trunkVolumeLitres = trunkVolumeLitres;
    this.groundClearanceMm = groundClearanceMm;
  }

  UUID id() {
    return id;
  }

  UUID generationId() {
    return generationId;
  }

  BodyStyle bodyStyle() {
    return bodyStyle;
  }

  int lengthMm() {
    return lengthMm;
  }

  int widthMm() {
    return widthMm;
  }

  int heightMm() {
    return heightMm;
  }

  int wheelbaseMm() {
    return wheelbaseMm;
  }

  Integer trunkVolumeLitres() {
    return trunkVolumeLitres;
  }

  Integer groundClearanceMm() {
    return groundClearanceMm;
  }
}
