package live.yurii.autocatalog.domain.fuelcellstack;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FuelCellStackTest {

  @Test
  void create_assigns_no_id_until_persisted() {
    var stack = FuelCellStack.create("Toyota FC Stack", 128, new BigDecimal("5.6"));

    assertThat(stack.id()).isNull();
    assertThat(stack.label()).isEqualTo("Toyota FC Stack");
    assertThat(stack.peakPowerKw()).isEqualTo(128);
    assertThat(stack.hydrogenTankKg()).isEqualByComparingTo("5.6");
  }

  @Test
  void create_strips_label_whitespace() {
    var stack = FuelCellStack.create("  FC Stack  ", null, null);

    assertThat(stack.label()).isEqualTo("FC Stack");
  }

  @Test
  void create_accepts_null_optional_fields() {
    var stack = FuelCellStack.create("FC Stack", null, null);

    assertThat(stack.peakPowerKw()).isNull();
    assertThat(stack.hydrogenTankKg()).isNull();
  }

  @Test
  void create_rejects_blank_label() {
    assertThatThrownBy(() -> FuelCellStack.create("", null, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("label");
  }

  @Test
  void create_rejects_non_positive_peak_power() {
    assertThatThrownBy(() -> FuelCellStack.create("FC Stack", 0, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("peakPowerKw");
  }

  @Test
  void reconstitute_preserves_all_fields() {
    var id = new FuelCellStackId(99L);

    var stack = FuelCellStack.reconstitute(id, "FC Stack", 128, new BigDecimal("5.6"));

    assertThat(stack.id()).isEqualTo(id);
    assertThat(stack.label()).isEqualTo("FC Stack");
    assertThat(stack.peakPowerKw()).isEqualTo(128);
    assertThat(stack.hydrogenTankKg()).isEqualByComparingTo("5.6");
  }
}
