package live.yurii.autocatalog.domain.powertrain;

import live.yurii.autocatalog.domain.powerunit.PowerUnitId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PowertrainTest {

  private final List<PowertrainUnit> singleIceUnit = List.of(
    new PowertrainUnit(new PowerUnitId(1L), UnitRole.PRIMARY)
  );

  // -------------------------------------------------------------------------
  // create() happy paths
  // -------------------------------------------------------------------------

  @Test
  void create_assigns_id_and_populates_fields() {
    var pt = Powertrain.create("2.0 TDI 150", DrivetrainType.ICE,
      150, 340, null, null, singleIceUnit);

    assertThat(pt.id()).isNotNull();
    assertThat(pt.name()).isEqualTo("2.0 TDI 150");
    assertThat(pt.drivetrainType()).isEqualTo(DrivetrainType.ICE);
    assertThat(pt.combinedPowerHp()).isEqualTo(150);
    assertThat(pt.combinedTorqueNm()).isEqualTo(340);
    assertThat(pt.batteryCapacityKwh()).isNull();
    assertThat(pt.electricRangeKm()).isNull();
    assertThat(pt.units()).hasSize(1);
    assertThat(pt.units().getFirst().role()).isEqualTo(UnitRole.PRIMARY);
  }

  @Test
  void create_strips_name_whitespace() {
    var pt = Powertrain.create("  2.0 TDI  ", DrivetrainType.ICE,
      110, null, null, null, singleIceUnit);

    assertThat(pt.name()).isEqualTo("2.0 TDI");
  }

  @Test
  void create_allows_null_torque_and_power() {
    var pt = Powertrain.create("EV Motor", DrivetrainType.BEV,
      null, null, null, null, singleIceUnit);

    assertThat(pt.combinedPowerHp()).isNull();
    assertThat(pt.combinedTorqueNm()).isNull();
  }

  @Test
  void create_phev_with_battery_and_range() {
    var phevUnits = List.of(
      new PowertrainUnit(new PowerUnitId(1L), UnitRole.PRIMARY),
      new PowertrainUnit(new PowerUnitId(2L), UnitRole.SECONDARY)
    );
    var pt = Powertrain.create("A250e PHEV", DrivetrainType.PHEV,
      218, 450, new BigDecimal("15.6"), 80, phevUnits);

    assertThat(pt.drivetrainType()).isEqualTo(DrivetrainType.PHEV);
    assertThat(pt.batteryCapacityKwh()).isEqualByComparingTo("15.6");
    assertThat(pt.electricRangeKm()).isEqualTo(80);
    assertThat(pt.units()).hasSize(2);
    assertThat(pt.units()).extracting(PowertrainUnit::role)
      .containsExactly(UnitRole.PRIMARY, UnitRole.SECONDARY);
  }

  @Test
  void create_bev_with_only_electric_primary() {
    var bevUnit = List.of(new PowertrainUnit(new PowerUnitId(10L), UnitRole.PRIMARY));
    var pt = Powertrain.create("EQA 250", DrivetrainType.BEV,
      140, 375, new BigDecimal("66.5"), 426, bevUnit);

    assertThat(pt.drivetrainType()).isEqualTo(DrivetrainType.BEV);
    assertThat(pt.units()).hasSize(1);
    assertThat(pt.units().getFirst().role()).isEqualTo(UnitRole.PRIMARY);
  }

  @Test
  void create_fcev_with_generator_and_primary_motor() {
    var fcevUnits = List.of(
      new PowertrainUnit(new PowerUnitId(3L), UnitRole.GENERATOR),
      new PowertrainUnit(new PowerUnitId(4L), UnitRole.PRIMARY)
    );
    var pt = Powertrain.create("Mirai FCEV", DrivetrainType.FCEV,
      128, 300, new BigDecimal("5.6"), 650, fcevUnits);

    assertThat(pt.drivetrainType()).isEqualTo(DrivetrainType.FCEV);
    assertThat(pt.units()).hasSize(2);
    assertThat(pt.units()).extracting(PowertrainUnit::role)
      .containsExactlyInAnyOrder(UnitRole.GENERATOR, UnitRole.PRIMARY);
  }

  // -------------------------------------------------------------------------
  // create() validations
  // -------------------------------------------------------------------------

  @Test
  void create_rejects_blank_name() {
    assertThatThrownBy(() -> Powertrain.create("  ", DrivetrainType.ICE,
      110, null, null, null, singleIceUnit))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("name");
  }

  @Test
  void create_rejects_null_drivetrain_type() {
    assertThatThrownBy(() -> Powertrain.create("2.0 TDI", null,
      110, null, null, null, singleIceUnit))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_non_positive_torque_when_present() {
    assertThatThrownBy(() -> Powertrain.create("2.0 TDI", DrivetrainType.ICE,
      110, 0, null, null, singleIceUnit))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("combinedTorqueNm");
  }

  @Test
  void create_rejects_battery_without_range() {
    assertThatThrownBy(() -> Powertrain.create("PHEV", DrivetrainType.PHEV,
      218, null, new BigDecimal("15.6"), null, singleIceUnit))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("batteryCapacityKwh");
  }

  @Test
  void create_rejects_range_without_battery() {
    assertThatThrownBy(() -> Powertrain.create("PHEV", DrivetrainType.PHEV,
      218, null, null, 80, singleIceUnit))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("batteryCapacityKwh");
  }

  @Test
  void create_rejects_empty_unit_list() {
    assertThatThrownBy(() -> Powertrain.create("2.0 TDI", DrivetrainType.ICE,
      110, null, null, null, List.of()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("unit");
  }

  @Test
  void create_rejects_null_unit_list() {
    assertThatThrownBy(() -> Powertrain.create("2.0 TDI", DrivetrainType.ICE,
      110, null, null, null, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("unit");
  }

  @Test
  void create_rejects_zero_primary_units() {
    var noPrimary = List.of(new PowertrainUnit(new PowerUnitId(1L), UnitRole.SECONDARY));
    assertThatThrownBy(() -> Powertrain.create("Bad", DrivetrainType.ICE,
      110, null, null, null, noPrimary))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("PRIMARY");
  }

  @Test
  void create_rejects_two_primary_units() {
    var twoPrimary = List.of(
      new PowertrainUnit(new PowerUnitId(1L), UnitRole.PRIMARY),
      new PowertrainUnit(new PowerUnitId(2L), UnitRole.PRIMARY)
    );
    assertThatThrownBy(() -> Powertrain.create("Bad", DrivetrainType.ICE,
      110, null, null, null, twoPrimary))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("PRIMARY");
  }

  @Test
  void units_list_is_unmodifiable() {
    var pt = Powertrain.create("2.0 TDI", DrivetrainType.ICE,
      110, null, null, null, singleIceUnit);

    assertThatThrownBy(() -> pt.units().add(
      new PowertrainUnit(new PowerUnitId(99L), UnitRole.SECONDARY)))
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void reconstitute_preserves_all_fields_without_validation() {
    var id = PowertrainId.generate();
    var units = List.of(new PowertrainUnit(new PowerUnitId(5L), UnitRole.PRIMARY));

    var pt = Powertrain.reconstitute(id, "2.0 TDI", DrivetrainType.ICE,
      150, 340, null, null, units);

    assertThat(pt.id()).isEqualTo(id);
    assertThat(pt.name()).isEqualTo("2.0 TDI");
    assertThat(pt.drivetrainType()).isEqualTo(DrivetrainType.ICE);
    assertThat(pt.combinedPowerHp()).isEqualTo(150);
    assertThat(pt.combinedTorqueNm()).isEqualTo(340);
    assertThat(pt.units()).hasSize(1);
    assertThat(pt.units().getFirst().powerUnitId()).isEqualTo(new PowerUnitId(5L));
  }

  @Test
  void rename_updates_name() {
    var pt = Powertrain.create("2.0 TDI 150", DrivetrainType.ICE,
      110, 340, null, null, singleIceUnit);

    pt.rename("2.0 TDI 160");

    assertThat(pt.name()).isEqualTo("2.0 TDI 160");
  }

  @Test
  void rename_rejects_blank() {
    var pt = Powertrain.create("2.0 TDI 150", DrivetrainType.ICE,
      110, 340, null, null, singleIceUnit);

    assertThatThrownBy(() -> pt.rename("   "))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("name");
  }
}
