package live.yurii.autocatalog.domain.body;

import live.yurii.autocatalog.domain.generation.GenerationId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BodyTest {

  private final GenerationId generationId = GenerationId.generate();

  @Test
  void create_assigns_id_and_populates_required_fields() {
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);

    assertThat(body.id()).isNotNull();
    assertThat(body.generationId()).isEqualTo(generationId);
    assertThat(body.bodyStyle()).isEqualTo(BodyStyle.SEDAN);
    assertThat(body.lengthMm()).isEqualTo(4650);
    assertThat(body.widthMm()).isEqualTo(1810);
    assertThat(body.heightMm()).isEqualTo(1440);
    assertThat(body.wheelbaseMm()).isEqualTo(2800);
    assertThat(body.trunkVolumeLitres()).isNull();
  }

  @Test
  void create_rejects_null_generation_id() {
    assertThatThrownBy(() -> Body.create(null, BodyStyle.SEDAN, 4650, 1810, 1440, 2800))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_null_body_style() {
    assertThatThrownBy(() -> Body.create(generationId, null, 4650, 1810, 1440, 2800))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_non_positive_length() {
    assertThatThrownBy(() -> Body.create(generationId, BodyStyle.SEDAN, 0, 1810, 1440, 2800))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("lengthMm");
  }

  @Test
  void create_rejects_non_positive_width() {
    assertThatThrownBy(() -> Body.create(generationId, BodyStyle.SEDAN, 4650, 0, 1440, 2800))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("widthMm");
  }

  @Test
  void create_rejects_non_positive_height() {
    assertThatThrownBy(() -> Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 0, 2800))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("heightMm");
  }

  @Test
  void setTrunkVolumeLitres_accepts_positive_value() {
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);

    body.setTrunkVolumeLitres(480);

    assertThat(body.trunkVolumeLitres()).isEqualTo(480);
  }

  @Test
  void setTrunkVolumeLitres_rejects_non_positive() {
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);

    assertThatThrownBy(() -> body.setTrunkVolumeLitres(0))
      .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> body.setTrunkVolumeLitres(-100))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void reconstitute_preserves_all_fields_without_validation() {
    var id = BodyId.generate();

    var body = Body.reconstitute(id, generationId, BodyStyle.HATCHBACK,
      4200, 1750, 1450, 2600, 380, 160);

    assertThat(body.id()).isEqualTo(id);
    assertThat(body.bodyStyle()).isEqualTo(BodyStyle.HATCHBACK);
    assertThat(body.lengthMm()).isEqualTo(4200);
    assertThat(body.trunkVolumeLitres()).isEqualTo(380);
    assertThat(body.groundClearanceMm()).isEqualTo(160);
  }

  @Test
  void setGroundClearanceMm_accepts_positive_value() {
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);

    body.setGroundClearanceMm(160);

    assertThat(body.groundClearanceMm()).isEqualTo(160);
  }

  @Test
  void setGroundClearanceMm_rejects_non_positive() {
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);

    assertThatThrownBy(() -> body.setGroundClearanceMm(0))
      .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> body.setGroundClearanceMm(-50))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void updateDimensions_sets_all_fields() {
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);

    body.updateDimensions(4700, 1820, 1450, 2850, 500, 165);

    assertThat(body.lengthMm()).isEqualTo(4700);
    assertThat(body.widthMm()).isEqualTo(1820);
    assertThat(body.heightMm()).isEqualTo(1450);
    assertThat(body.wheelbaseMm()).isEqualTo(2850);
    assertThat(body.trunkVolumeLitres()).isEqualTo(500);
    assertThat(body.groundClearanceMm()).isEqualTo(165);
  }

  @Test
  void updateDimensions_clears_optional_fields_when_null() {
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);
    body.setTrunkVolumeLitres(480);
    body.setGroundClearanceMm(155);

    body.updateDimensions(4700, 1820, 1450, 2850, null, null);

    assertThat(body.trunkVolumeLitres()).isNull();
    assertThat(body.groundClearanceMm()).isNull();
  }

  @Test
  void updateDimensions_rejects_zero_length() {
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);

    assertThatThrownBy(() -> body.updateDimensions(0, 1820, 1450, 2850, null, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("lengthMm");
  }

  @Test
  void create_has_null_ground_clearance_by_default() {
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);

    assertThat(body.groundClearanceMm()).isNull();
  }

  @Test
  void create_allows_null_wheelbase() {
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, null);

    assertThat(body.wheelbaseMm()).isNull();
  }

  @Test
  void create_rejects_non_positive_wheelbase_when_provided() {
    assertThatThrownBy(() -> Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("wheelbaseMm");
  }
}
