package live.yurii.autocatalog.domain.transmission;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransmissionTest {

  @Test
  void create_assigns_id_and_populates_fields() {
    var t = Transmission.create(TransmissionType.DCT, 7);

    assertThat(t.id()).isNotNull();
    assertThat(t.type()).isEqualTo(TransmissionType.DCT);
    assertThat(t.gearCount()).isEqualTo(7);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 6, 12})
  void create_accepts_gear_counts_at_and_within_bounds(int gearCount) {
    var t = Transmission.create(TransmissionType.MANUAL, gearCount);

    assertThat(t.gearCount()).isEqualTo(gearCount);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, 13, 100})
  void create_rejects_gear_counts_out_of_bounds(int gearCount) {
    assertThatThrownBy(() -> Transmission.create(TransmissionType.MANUAL, gearCount))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("gear count");
  }

  @Test
  void create_rejects_null_type() {
    assertThatThrownBy(() -> Transmission.create(null, 6))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void reconstitute_preserves_fields() {
    var id = TransmissionId.generate();

    var t = Transmission.reconstitute(id, TransmissionType.CVT, 1);

    assertThat(t.id()).isEqualTo(id);
    assertThat(t.type()).isEqualTo(TransmissionType.CVT);
    assertThat(t.gearCount()).isEqualTo(1);
  }
}
