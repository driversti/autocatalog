package live.yurii.autocatalog.domain.engine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EngineTest {

  @Test
  void create_assigns_id_and_populates_required_fields() {
    var engine = Engine.create("B58", "3.0 TwinPower", FuelType.PETROL,
      2998, 285, 500);

    assertThat(engine.id()).isNotNull();
    assertThat(engine.code()).isEqualTo("B58");
    assertThat(engine.name()).isEqualTo("3.0 TwinPower");
    assertThat(engine.fuelType()).isEqualTo(FuelType.PETROL);
    assertThat(engine.displacementCc()).isEqualTo(2998);
    assertThat(engine.powerKw()).isEqualTo(285);
    assertThat(engine.torqueNm()).isEqualTo(500);
    assertThat(engine.cylinderCount()).isNull();
    assertThat(engine.configuration()).isNull();
  }

  @Test
  void create_strips_whitespace_on_code_and_name() {
    var engine = Engine.create("  B58  ", "  Turbo  ", FuelType.PETROL, 2998, 285, 500);

    assertThat(engine.code()).isEqualTo("B58");
    assertThat(engine.name()).isEqualTo("Turbo");
  }

  @Test
  void create_rejects_non_positive_displacement() {
    assertThatThrownBy(() -> Engine.create("X", "Y", FuelType.PETROL, 0, 100, 200))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("displacement");
  }

  @Test
  void create_rejects_non_positive_power() {
    assertThatThrownBy(() -> Engine.create("X", "Y", FuelType.PETROL, 2000, 0, 200))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("power");
  }

  @Test
  void create_rejects_non_positive_torque() {
    assertThatThrownBy(() -> Engine.create("X", "Y", FuelType.PETROL, 2000, 100, -1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("torque");
  }

  @Test
  void create_rejects_blank_code() {
    assertThatThrownBy(() -> Engine.create("", "Y", FuelType.PETROL, 2000, 100, 200))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("code");
  }

  @Test
  void create_rejects_null_fuel_type() {
    assertThatThrownBy(() -> Engine.create("X", "Y", null, 2000, 100, 200))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void powerHp_converts_kw_using_standard_factor() {
    var engine = Engine.create("B58", "Turbo", FuelType.PETROL, 2998, 100, 500);

    // 100 kW * 1.35962 = 135.962 -> rounds to 136
    assertThat(engine.powerHp()).isEqualTo(136);
  }

  @Test
  void powerHp_rounds_a_typical_value() {
    var engine = Engine.create("B58", "Turbo", FuelType.PETROL, 2998, 285, 500);

    // 285 * 1.35962 = 387.4 -> rounds to 387
    assertThat(engine.powerHp()).isEqualTo(387);
  }

  @Test
  void setCylinderCount_accepts_positive_value() {
    var engine = Engine.create("B58", "Turbo", FuelType.PETROL, 2998, 285, 500);

    engine.setCylinderCount(6);

    assertThat(engine.cylinderCount()).isEqualTo(6);
  }

  @Test
  void setCylinderCount_rejects_zero_or_negative() {
    var engine = Engine.create("B58", "Turbo", FuelType.PETROL, 2998, 285, 500);

    assertThatThrownBy(() -> engine.setCylinderCount(0))
      .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> engine.setCylinderCount(-4))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void setConfiguration_strips_whitespace() {
    var engine = Engine.create("B58", "Turbo", FuelType.PETROL, 2998, 285, 500);

    engine.setConfiguration("  inline-6  ");

    assertThat(engine.configuration()).isEqualTo("inline-6");
  }

  @Test
  void setConfiguration_rejects_blank() {
    var engine = Engine.create("B58", "Turbo", FuelType.PETROL, 2998, 285, 500);

    assertThatThrownBy(() -> engine.setConfiguration("   "))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void reconstitute_preserves_all_fields_without_validation() {
    var id = EngineId.generate();

    var engine = Engine.reconstitute(id, "X", "Y", FuelType.DIESEL,
      2000, 100, 300, 4, "inline-4", 136);

    assertThat(engine.id()).isEqualTo(id);
    assertThat(engine.cylinderCount()).isEqualTo(4);
    assertThat(engine.configuration()).isEqualTo("inline-4");
    assertThat(engine.systemPowerKw()).isEqualTo(136);
  }

  @Test
  void create_accepts_null_displacement_for_electric_motor() {
    var engine = Engine.create("EM1", "Electric Motor", FuelType.ELECTRIC, null, 150, null);

    assertThat(engine.displacementCc()).isNull();
    assertThat(engine.torqueNm()).isNull();
  }

  @Test
  void create_rejects_zero_displacement_when_provided() {
    assertThatThrownBy(() -> Engine.create("X", "Y", FuelType.ELECTRIC, 0, 100, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("displacement");
  }

  @Test
  void create_rejects_zero_torque_when_provided() {
    assertThatThrownBy(() -> Engine.create("X", "Y", FuelType.PETROL, 2000, 100, 0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("torque");
  }
}
