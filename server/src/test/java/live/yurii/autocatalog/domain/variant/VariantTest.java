package live.yurii.autocatalog.domain.variant;

import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VariantTest {

  private final BodyId bodyId = BodyId.generate();
  private final PowertrainId powertrainId = PowertrainId.generate();
  private final TransmissionId transmissionId = TransmissionId.generate();

  @Test
  void create_assigns_id_and_populates_required_fields() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);

    assertThat(variant.id()).isNotNull();
    assertThat(variant.bodyId()).isEqualTo(bodyId);
    assertThat(variant.powertrainId()).isEqualTo(powertrainId);
    assertThat(variant.transmissionId()).isEqualTo(transmissionId);
    assertThat(variant.drivetrain()).isEqualTo(Drivetrain.FWD);
    assertThat(variant.curbWeightKg()).isEqualTo(1350);
    assertThat(variant.markets()).isEmpty();
    assertThat(variant.groundClearanceMm()).isNull();
  }

  @Test
  void create_rejects_null_body_id() {
    assertThatThrownBy(() -> Variant.create(null, powertrainId, transmissionId, Drivetrain.FWD, 1350))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_null_powertrain_id() {
    assertThatThrownBy(() -> Variant.create(bodyId, null, transmissionId, Drivetrain.FWD, 1350))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_null_transmission_id() {
    assertThatThrownBy(() -> Variant.create(bodyId, powertrainId, null, Drivetrain.FWD, 1350))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_null_drivetrain() {
    assertThatThrownBy(() -> Variant.create(bodyId, powertrainId, transmissionId, null, 1350))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_non_positive_curb_weight() {
    assertThatThrownBy(() -> Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("curbWeightKg");
  }

  @Test
  void addMarket_normalizes_to_uppercase() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);

    variant.addMarket("de");

    assertThat(variant.markets()).containsExactly("DE");
  }

  @Test
  void addMarket_deduplicates() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);

    variant.addMarket("UA");
    variant.addMarket("UA");

    assertThat(variant.markets()).containsExactly("UA");
  }

  @Test
  void addMarket_rejects_blank() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);

    assertThatThrownBy(() -> variant.addMarket("  "))
      .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> variant.addMarket(null))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void setGroundClearanceMm_accepts_positive_value() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.AWD, 1800);

    variant.setGroundClearanceMm(200);

    assertThat(variant.groundClearanceMm()).isEqualTo(200);
  }

  @Test
  void setGroundClearanceMm_rejects_non_positive() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);

    assertThatThrownBy(() -> variant.setGroundClearanceMm(0))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void markets_are_unmodifiable() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);

    assertThatThrownBy(() -> variant.markets().add("US"))
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void removeMarket_removes_existing_market() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);
    variant.addMarket("DE");
    variant.addMarket("FR");

    variant.removeMarket("DE");

    assertThat(variant.markets()).containsExactly("FR");
  }

  @Test
  void removeMarket_normalizes_to_uppercase() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);
    variant.addMarket("DE");

    variant.removeMarket("de");

    assertThat(variant.markets()).isEmpty();
  }

  @Test
  void removeMarket_rejects_blank() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);

    assertThatThrownBy(() -> variant.removeMarket("  "))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("market");
  }

  @Test
  void updateCurbWeightKg_sets_new_weight() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1200);

    variant.updateCurbWeightKg(1350);

    assertThat(variant.curbWeightKg()).isEqualTo(1350);
  }

  @Test
  void updateCurbWeightKg_rejects_zero() {
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1200);

    assertThatThrownBy(() -> variant.updateCurbWeightKg(0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("curbWeightKg");
  }

  @Test
  void reconstitute_preserves_all_fields_without_validation() {
    var id = VariantId.generate();

    var variant = Variant.reconstitute(id, bodyId, powertrainId, transmissionId,
      Drivetrain.AWD, 200, 1800, Set.of("UA", "EU"));

    assertThat(variant.id()).isEqualTo(id);
    assertThat(variant.powertrainId()).isEqualTo(powertrainId);
    assertThat(variant.drivetrain()).isEqualTo(Drivetrain.AWD);
    assertThat(variant.groundClearanceMm()).isEqualTo(200);
    assertThat(variant.markets()).containsExactlyInAnyOrder("UA", "EU");
  }
}
