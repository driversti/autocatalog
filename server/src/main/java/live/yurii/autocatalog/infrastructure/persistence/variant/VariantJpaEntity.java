package live.yurii.autocatalog.infrastructure.persistence.variant;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import live.yurii.autocatalog.domain.variant.Drivetrain;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "variants")
class VariantJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "body_id", nullable = false, updatable = false)
  private UUID bodyId;

  @ElementCollection
  @CollectionTable(name = "variant_engines", joinColumns = @JoinColumn(name = "variant_id"))
  @Column(name = "engine_id")
  private Set<UUID> engineIds = new LinkedHashSet<>();

  @Column(name = "transmission_id", nullable = false)
  private UUID transmissionId;

  @Enumerated(EnumType.STRING)
  @Column(name = "drivetrain", nullable = false, length = 10)
  private Drivetrain drivetrain;

  @Column(name = "ground_clearance_mm")
  private Integer groundClearanceMm;

  @Column(name = "curb_weight_kg", nullable = false)
  private int curbWeightKg;

  @Column(name = "system_power_kw")
  private Integer systemPowerKw;

  @ElementCollection
  @CollectionTable(name = "variant_markets", joinColumns = @JoinColumn(name = "variant_id"))
  @Column(name = "market", length = 2)
  private Set<String> markets = new LinkedHashSet<>();

  protected VariantJpaEntity() {
  }

  VariantJpaEntity(UUID id, UUID bodyId, UUID transmissionId, Drivetrain drivetrain,
                   Integer groundClearanceMm, int curbWeightKg, Integer systemPowerKw) {
    this.id = id;
    this.bodyId = bodyId;
    this.transmissionId = transmissionId;
    this.drivetrain = drivetrain;
    this.groundClearanceMm = groundClearanceMm;
    this.curbWeightKg = curbWeightKg;
    this.systemPowerKw = systemPowerKw;
  }

  UUID id() {
    return id;
  }

  UUID bodyId() {
    return bodyId;
  }

  Set<UUID> engineIds() {
    return engineIds;
  }

  UUID transmissionId() {
    return transmissionId;
  }

  Drivetrain drivetrain() {
    return drivetrain;
  }

  Integer groundClearanceMm() {
    return groundClearanceMm;
  }

  int curbWeightKg() {
    return curbWeightKg;
  }

  Integer systemPowerKw() {
    return systemPowerKw;
  }

  Set<String> markets() {
    return markets;
  }
}
