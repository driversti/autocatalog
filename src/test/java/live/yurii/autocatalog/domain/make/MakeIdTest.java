package live.yurii.autocatalog.domain.make;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MakeIdTest {

  @Test
  void generate_produces_unique_non_null_ids() {
    var a = MakeId.generate();
    var b = MakeId.generate();

    assertThat(a.value()).isNotNull();
    assertThat(a).isNotEqualTo(b);
  }

  @Test
  void of_parses_valid_uuid_string() {
    var uuid = UUID.randomUUID();

    var id = MakeId.of(uuid.toString());

    assertThat(id.value()).isEqualTo(uuid);
  }

  @Test
  void of_rejects_invalid_uuid_string() {
    assertThatThrownBy(() -> MakeId.of("not-a-uuid"))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_rejects_null_value() {
    assertThatThrownBy(() -> new MakeId(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessageContaining("MakeId value must not be null");
  }
}
