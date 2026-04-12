package live.yurii.autocatalog.domain.generation;

import live.yurii.autocatalog.domain.model.ModelId;
import live.yurii.autocatalog.domain.shared.YearRange;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerationTest {

  private final ModelId modelId = ModelId.generate();

  @Test
  void create_assigns_id_and_populates_fields() {
    var gen = Generation.create(modelId, "F30", new YearRange(2012, 2019));

    assertThat(gen.id()).isNotNull();
    assertThat(gen.modelId()).isEqualTo(modelId);
    assertThat(gen.name()).isEqualTo("F30");
    assertThat(gen.years()).isEqualTo(new YearRange(2012, 2019));
  }

  @Test
  void create_rejects_null_model_id() {
    assertThatThrownBy(() -> Generation.create(null, "F30", new YearRange(2012, 2019)))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_blank_name() {
    assertThatThrownBy(() -> Generation.create(modelId, "  ", new YearRange(2012, 2019)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("name");
  }

  @Test
  void create_rejects_null_year_range() {
    assertThatThrownBy(() -> Generation.create(modelId, "F30", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void close_sets_end_year_preserving_start() {
    var gen = Generation.create(modelId, "E90", new YearRange(2005, null));

    gen.close(2012);

    assertThat(gen.years()).isEqualTo(new YearRange(2005, 2012));
    assertThat(gen.years().isCurrent()).isFalse();
  }

  @Test
  void close_rejects_year_before_start() {
    var gen = Generation.create(modelId, "E90", new YearRange(2005, null));

    assertThatThrownBy(() -> gen.close(2000))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("before start");
  }

  @Test
  void close_allows_closing_at_start_year() {
    var gen = Generation.create(modelId, "Shortlived", new YearRange(2020, null));

    gen.close(2020);

    assertThat(gen.years()).isEqualTo(new YearRange(2020, 2020));
  }

  @Test
  void reconstitute_preserves_all_fields_without_validation() {
    var id = GenerationId.generate();

    var gen = Generation.reconstitute(id, modelId, "E90", new YearRange(2005, 2012));

    assertThat(gen.id()).isEqualTo(id);
    assertThat(gen.modelId()).isEqualTo(modelId);
    assertThat(gen.name()).isEqualTo("E90");
    assertThat(gen.years()).isEqualTo(new YearRange(2005, 2012));
  }
}
