package live.yurii.autocatalog.domain.variant;

import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VariantTest {

  private final BodyId bodyId = BodyId.generate();
  private final TransmissionId transmissionId = TransmissionId.generate();

  @Test
  void create_assigns_id_and_populates_required_fields() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.FWD, 1350);

    assertThat(variant.id()).isNotNull();
    assertThat(variant.bodyId()).isEqualTo(bodyId);
    assertThat(variant.transmissionId()).isEqualTo(transmissionId);
    assertThat(variant.drivetrain()).isEqualTo(Drivetrain.FWD);
    assertThat(variant.curbWeightKg()).isEqualTo(1350);
    assertThat(variant.engineIds()).isEmpty();
    assertThat(variant.markets()).isEmpty();
    assertThat(variant.groundClearanceMm()).isNull();
    assertThat(variant.systemPowerKw()).isNull();
  }

  @Test
  void create_rejects_null_body_id() {
    assertThatThrownBy(() -> Variant.create(null, transmissionId, Drivetrain.FWD, 1350))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_null_transmission_id() {
    assertThatThrownBy(() -> Variant.create(bodyId, null, Drivetrain.FWD, 1350))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_null_drivetrain() {
    assertThatThrownBy(() -> Variant.create(bodyId, transmissionId, null, 1350))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_non_positive_curb_weight() {
    assertThatThrownBy(() -> Variant.create(bodyId, transmissionId, Drivetrain.FWD, 0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("curbWeightKg");
  }

  @Test
  void addEngine_deduplicates_same_id() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.RWD, 1600);
    var engineId = EngineId.generate();

    variant.addEngine(engineId);
    variant.addEngine(engineId);

    assertThat(variant.engineIds()).containsExactly(engineId);
  }

  @Test
  void addEngine_rejects_null() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.FWD, 1350);

    assertThatThrownBy(() -> variant.addEngine(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void addMarket_normalizes_to_uppercase() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.FWD, 1350);

    variant.addMarket("de");

    assertThat(variant.markets()).containsExactly("DE");
  }

  @Test
  void addMarket_deduplicates() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.FWD, 1350);

    variant.addMarket("UA");
    variant.addMarket("UA");

    assertThat(variant.markets()).containsExactly("UA");
  }

  @Test
  void addMarket_rejects_blank() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.FWD, 1350);

    assertThatThrownBy(() -> variant.addMarket("  "))
      .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> variant.addMarket(null))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void setGroundClearanceMm_accepts_positive_value() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.AWD, 1800);

    variant.setGroundClearanceMm(200);

    assertThat(variant.groundClearanceMm()).isEqualTo(200);
  }

  @Test
  void setGroundClearanceMm_rejects_non_positive() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.FWD, 1350);

    assertThatThrownBy(() -> variant.setGroundClearanceMm(0))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void setSystemPowerKw_accepts_positive_value() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.FWD, 1350);

    variant.setSystemPowerKw(134);

    assertThat(variant.systemPowerKw()).isEqualTo(134);
  }

  @Test
  void setSystemPowerKw_rejects_non_positive() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.FWD, 1350);

    assertThatThrownBy(() -> variant.setSystemPowerKw(0))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void engineIds_and_markets_are_unmodifiable() {
    var variant = Variant.create(bodyId, transmissionId, Drivetrain.FWD, 1350);

    assertThatThrownBy(() -> variant.engineIds().add(EngineId.generate()))
      .isInstanceOf(UnsupportedOperationException.class);
    assertThatThrownBy(() -> variant.markets().add("US"))
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void reconstitute_preserves_all_fields_without_validation() {
    var id = VariantId.generate();
    var engineId = EngineId.generate();

    var variant = Variant.reconstitute(id, bodyId, Set.of(engineId), transmissionId,
      Drivetrain.AWD, 200, 1800, 134, Set.of("UA", "EU"));

    assertThat(variant.id()).isEqualTo(id);
    assertThat(variant.drivetrain()).isEqualTo(Drivetrain.AWD);
    assertThat(variant.engineIds()).containsExactly(engineId);
    assertThat(variant.groundClearanceMm()).isEqualTo(200);
    assertThat(variant.systemPowerKw()).isEqualTo(134);
    assertThat(variant.markets()).containsExactlyInAnyOrder("UA", "EU");
  }
}
