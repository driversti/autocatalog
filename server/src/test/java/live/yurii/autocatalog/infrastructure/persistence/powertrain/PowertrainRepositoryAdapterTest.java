package live.yurii.autocatalog.infrastructure.persistence.powertrain;

import live.yurii.autocatalog.domain.powertrain.DrivetrainType;
import live.yurii.autocatalog.domain.powertrain.Powertrain;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.powertrain.PowertrainUnit;
import live.yurii.autocatalog.domain.powertrain.UnitRole;
import live.yurii.autocatalog.domain.powerunit.PowerUnitId;
import live.yurii.autocatalog.domain.powerunit.PowerUnitType;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaEntity;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PowertrainRepositoryAdapterTest {

  @Mock
  private PowertrainJpaRepository jpa;

  @Mock
  private PowerUnitJpaRepository powerUnitJpa;

  private PowertrainRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new PowertrainRepositoryAdapter(jpa, powerUnitJpa);
  }

  @Test
  void save_persists_entity() {
    var units = List.of(new PowertrainUnit(new PowerUnitId(1L), UnitRole.PRIMARY));
    var powertrain = Powertrain.create("2.0 TDI 150", DrivetrainType.ICE,
      150, 340, null, null, units);
    // getReferenceById returns a lazy proxy in real JPA; in unit tests, provide a real entity with id
    var powerUnitEntity = new PowerUnitJpaEntity(1L, PowerUnitType.ICE);
    when(powerUnitJpa.getReferenceById(1L)).thenReturn(powerUnitEntity);
    when(jpa.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = adapter.save(powertrain);

    verify(jpa).save(any(PowertrainJpaEntity.class));
    assertThat(result).isSameAs(powertrain);
  }

  @Test
  void findById_returns_mapped_domain() {
    var id = UUID.randomUUID();
    var entity = new PowertrainJpaEntity(id, "2.0 TDI", DrivetrainType.ICE, 150, 340, null, null);
    when(jpa.findById(id)).thenReturn(Optional.of(entity));

    var result = adapter.findById(new PowertrainId(id));

    assertThat(result).isPresent();
    var pt = result.get();
    assertThat(pt.id().value()).isEqualTo(id);
    assertThat(pt.name()).isEqualTo("2.0 TDI");
    assertThat(pt.drivetrainType()).isEqualTo(DrivetrainType.ICE);
    assertThat(pt.combinedPowerHp()).isEqualTo(150);
    assertThat(pt.combinedTorqueNm()).isEqualTo(340);
    assertThat(pt.units()).isEmpty();
  }

  @Test
  void findById_returns_empty_when_missing() {
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.empty());

    assertThat(adapter.findById(new PowertrainId(id))).isEmpty();
  }

  @Test
  void findByName_returns_mapped_domain() {
    var id = UUID.randomUUID();
    var entity = new PowertrainJpaEntity(id, "EV Motor", DrivetrainType.BEV, null, null, null, null);
    when(jpa.findByName("EV Motor")).thenReturn(Optional.of(entity));

    var result = adapter.findByName("EV Motor");

    assertThat(result).isPresent();
    assertThat(result.get().name()).isEqualTo("EV Motor");
    assertThat(result.get().combinedTorqueNm()).isNull();
  }

  @Test
  void findAll_maps_every_entity() {
    when(jpa.findAll()).thenReturn(List.of(
      new PowertrainJpaEntity(UUID.randomUUID(), "A", DrivetrainType.ICE, 100, null, null, null),
      new PowertrainJpaEntity(UUID.randomUUID(), "B", DrivetrainType.BEV, 200, null, null, null)
    ));

    var all = adapter.findAll();

    assertThat(all).extracting(Powertrain::name).containsExactly("A", "B");
  }

  @Test
  void existsByName_delegates_directly() {
    when(jpa.existsByName("2.0 TDI 150")).thenReturn(true);

    assertThat(adapter.existsByName("2.0 TDI 150")).isTrue();
  }

  @Test
  void existsByName_returns_false_when_jpa_says_no() {
    when(jpa.existsByName("unknown")).thenReturn(false);

    assertThat(adapter.existsByName("unknown")).isFalse();
  }

  @Test
  void existsByPowerUnitId_delegates_to_jpa() {
    when(jpa.existsByUnits_PowerUnit_Id(5L)).thenReturn(true);

    assertThat(adapter.existsByPowerUnitId(new PowerUnitId(5L))).isTrue();
  }
}
