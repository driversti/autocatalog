package live.yurii.autocatalog.domain.make;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MakeTest {

  @Test
  void create_assigns_id_and_slugifies_name() {
    var make = Make.create("Alfa Romeo", "Italy");

    assertThat(make.id()).isNotNull();
    assertThat(make.id().value()).isNotNull();
    assertThat(make.name()).isEqualTo("Alfa Romeo");
    assertThat(make.country()).isEqualTo("Italy");
    assertThat(make.slug()).isEqualTo("alfa-romeo");
  }

  @Test
  void create_strips_whitespace_from_name_and_country() {
    var make = Make.create("  BMW  ", "  Germany  ");

    assertThat(make.name()).isEqualTo("BMW");
    assertThat(make.country()).isEqualTo("Germany");
    assertThat(make.slug()).isEqualTo("bmw");
  }

  @Test
  void create_slugifies_punctuation_and_special_chars() {
    var make = Make.create("Rolls-Royce & Bentley!", "UK");

    assertThat(make.slug()).isEqualTo("rolls-royce-bentley-");
  }

  @Test
  void create_rejects_blank_name() {
    assertThatThrownBy(() -> Make.create("   ", "Germany"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("name");
  }

  @Test
  void create_rejects_null_name() {
    assertThatThrownBy(() -> Make.create(null, "Germany"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("name");
  }

  @Test
  void create_rejects_blank_country() {
    assertThatThrownBy(() -> Make.create("BMW", ""))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("country");
  }

  @Test
  void reconstitute_preserves_all_fields_without_validation() {
    var id = MakeId.generate();

    var make = Make.reconstitute(id, "Legacy", "Nowhere", "legacy-slug");

    assertThat(make.id()).isEqualTo(id);
    assertThat(make.name()).isEqualTo("Legacy");
    assertThat(make.country()).isEqualTo("Nowhere");
    assertThat(make.slug()).isEqualTo("legacy-slug");
  }

  @Test
  void rename_updates_name_and_regenerates_slug() {
    var make = Make.create("Old Name", "Germany");

    make.rename("New Brand");

    assertThat(make.name()).isEqualTo("New Brand");
    assertThat(make.slug()).isEqualTo("new-brand");
  }

  @Test
  void rename_rejects_blank_name() {
    var make = Make.create("BMW", "Germany");

    assertThatThrownBy(() -> make.rename(" "))
      .isInstanceOf(IllegalArgumentException.class);
  }
}
