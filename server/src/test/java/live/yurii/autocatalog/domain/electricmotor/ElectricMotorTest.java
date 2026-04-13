package live.yurii.autocatalog.domain.electricmotor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElectricMotorTest {

  @Test
  void create_assigns_no_id_until_persisted() {
    var motor = ElectricMotor.create("Rear EM 75kW", 75, 250, MotorType.PERMANENT_MAGNET);

    assertThat(motor.id()).isNull();
    assertThat(motor.label()).isEqualTo("Rear EM 75kW");
    assertThat(motor.powerKw()).isEqualTo(75);
    assertThat(motor.torqueNm()).isEqualTo(250);
    assertThat(motor.motorType()).isEqualTo(MotorType.PERMANENT_MAGNET);
  }

  @Test
  void create_strips_label_whitespace() {
    var motor = ElectricMotor.create("  Front EM  ", null, null, null);

    assertThat(motor.label()).isEqualTo("Front EM");
  }

  @Test
  void create_accepts_null_optional_fields() {
    var motor = ElectricMotor.create("EM", null, null, null);

    assertThat(motor.powerKw()).isNull();
    assertThat(motor.torqueNm()).isNull();
    assertThat(motor.motorType()).isNull();
  }

  @Test
  void create_rejects_blank_label() {
    assertThatThrownBy(() -> ElectricMotor.create("", null, null, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("label");
  }

  @Test
  void create_rejects_non_positive_power() {
    assertThatThrownBy(() -> ElectricMotor.create("EM", 0, null, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("powerKw");
  }

  @Test
  void create_rejects_non_positive_torque() {
    assertThatThrownBy(() -> ElectricMotor.create("EM", null, -1, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("torqueNm");
  }

  @Test
  void updateLabel_changes_label() {
    var motor = ElectricMotor.create("Old Label", 75, 250, null);

    motor.updateLabel("New Label");

    assertThat(motor.label()).isEqualTo("New Label");
  }

  @Test
  void updateLabel_rejects_blank() {
    var motor = ElectricMotor.create("Label", 75, 250, null);

    assertThatThrownBy(() -> motor.updateLabel("  "))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void updateSpecs_changes_specs() {
    var motor = ElectricMotor.create("EM", 75, 250, MotorType.INDUCTION);

    motor.updateSpecs(100, 300, MotorType.PERMANENT_MAGNET);

    assertThat(motor.powerKw()).isEqualTo(100);
    assertThat(motor.torqueNm()).isEqualTo(300);
    assertThat(motor.motorType()).isEqualTo(MotorType.PERMANENT_MAGNET);
  }

  @Test
  void reconstitute_preserves_all_fields() {
    var id = new ElectricMotorId(42L);

    var motor = ElectricMotor.reconstitute(id, "Rear EM", 75, 250, MotorType.INDUCTION);

    assertThat(motor.id()).isEqualTo(id);
    assertThat(motor.label()).isEqualTo("Rear EM");
    assertThat(motor.powerKw()).isEqualTo(75);
    assertThat(motor.torqueNm()).isEqualTo(250);
    assertThat(motor.motorType()).isEqualTo(MotorType.INDUCTION);
  }
}
