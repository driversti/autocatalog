package live.yurii.autocatalog.infrastructure.persistence.engine;

import live.yurii.autocatalog.domain.engine.Engine;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.engine.FuelType;
import live.yurii.autocatalog.domain.powerunit.PowerUnitType;
import live.yurii.autocatalog.infrastructure.persistence.powerunit.PowerUnitJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EngineRepositoryAdapterTest {

  @Mock
  private EngineJpaRepository jpa;

  private EngineRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new EngineRepositoryAdapter(jpa);
  }

  private EngineJpaEntity entityWithId(long id, String code, String name, FuelType fuelType,
                                       Integer displacementCc, int powerKw, Integer torqueNm,
                                       Integer cylinderCount, String configuration,
                                       Integer systemPowerKw) {
    var unit = new PowerUnitJpaEntity(PowerUnitType.ICE);
    var entity = new EngineJpaEntity(unit, code, name, fuelType,
      displacementCc, powerKw, torqueNm, cylinderCount, configuration, systemPowerKw, null, false);
    // Simulate the ID being set after persist by using reconstitute-style entity via JPA stub
    // We just verify the code went through; actual ID assignment is integration-test territory
    return entity;
  }

  @Test
  void save_calls_jpa_save_and_returns_domain() {
    var engine = Engine.create("B58", "3.0L TwinPower", FuelType.PETROL, 2998, 250, 500);
    engine.setCylinderCount(6);
    engine.setConfiguration("I6");
    engine.setSystemPowerKw(280);

    var unit = new PowerUnitJpaEntity(PowerUnitType.ICE);
    // Simulate post-persist: entity has id assigned
    var savedEntity = new EngineJpaEntity(1L, unit, "B58", "3.0L TwinPower", FuelType.PETROL,
      2998, 250, 500, 6, "I6", 280, null, false);
    when(jpa.save(any(EngineJpaEntity.class))).thenReturn(savedEntity);

    var result = adapter.save(engine);

    var captor = ArgumentCaptor.forClass(EngineJpaEntity.class);
    verify(jpa).save(captor.capture());
    var entity = captor.getValue();
    assertThat(entity.code()).isEqualTo("B58");
    assertThat(entity.name()).isEqualTo("3.0L TwinPower");
    assertThat(entity.fuelType()).isEqualTo(FuelType.PETROL);
    assertThat(entity.displacementCc()).isEqualTo(2998);
    assertThat(entity.powerKw()).isEqualTo(250);
    assertThat(entity.torqueNm()).isEqualTo(500);
    assertThat(entity.cylinderCount()).isEqualTo(6);
    assertThat(entity.configuration()).isEqualTo("I6");
    assertThat(entity.systemPowerKw()).isEqualTo(280);
    assertThat(result.code()).isEqualTo("B58");
  }

  @Test
  void findById_returns_mapped_domain() {
    var unit = new PowerUnitJpaEntity(PowerUnitType.ICE);
    var entity = new EngineJpaEntity(1L, unit, "M20", "2.0L M", FuelType.DIESEL,
      1998, 140, 320, 4, "I4", null, null, false);
    when(jpa.findById(1L)).thenReturn(Optional.of(entity));

    var result = adapter.findById(new EngineId(1L));

    assertThat(result).isPresent();
    var engine = result.get();
    assertThat(engine.code()).isEqualTo("M20");
    assertThat(engine.fuelType()).isEqualTo(FuelType.DIESEL);
    assertThat(engine.displacementCc()).isEqualTo(1998);
    assertThat(engine.powerKw()).isEqualTo(140);
    assertThat(engine.torqueNm()).isEqualTo(320);
    assertThat(engine.cylinderCount()).isEqualTo(4);
    assertThat(engine.configuration()).isEqualTo("I4");
    assertThat(engine.systemPowerKw()).isNull();
  }

  @Test
  void findById_returns_empty_when_missing() {
    when(jpa.findById(99L)).thenReturn(Optional.empty());

    assertThat(adapter.findById(new EngineId(99L))).isEmpty();
  }

  @Test
  void findByCode_returns_mapped_domain() {
    var unit = new PowerUnitJpaEntity(PowerUnitType.ICE);
    var entity = new EngineJpaEntity(2L, unit, "M20", "2.0L M", FuelType.DIESEL,
      1998, 140, 320, null, null, null, null, false);
    when(jpa.findByCode("M20")).thenReturn(Optional.of(entity));

    var result = adapter.findByCode("M20");

    assertThat(result).isPresent();
    assertThat(result.get().code()).isEqualTo("M20");
  }

  @Test
  void findAll_maps_every_entity() {
    var unit1 = new PowerUnitJpaEntity(PowerUnitType.ICE);
    var unit2 = new PowerUnitJpaEntity(PowerUnitType.ICE);
    when(jpa.findAll()).thenReturn(List.of(
      new EngineJpaEntity(1L, unit1, "A", "A", FuelType.PETROL, 1000, 50, 100, null, null, null, null, false),
      new EngineJpaEntity(2L, unit2, "B", "B", FuelType.DIESEL, 2000, 100, 200, null, null, null, null, false)
    ));

    var all = adapter.findAll();

    assertThat(all).extracting(Engine::code).containsExactly("A", "B");
  }

  @Test
  void findByFuelType_delegates_and_maps() {
    var unit = new PowerUnitJpaEntity(PowerUnitType.ICE);
    when(jpa.findByFuelType(FuelType.HYBRID)).thenReturn(List.of(
      new EngineJpaEntity(3L, unit, "H1", "Hybrid", FuelType.HYBRID, 1500, 80, 150, 4, "I4", 136, null, false)
    ));

    var result = adapter.findByFuelType(FuelType.HYBRID);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().fuelType()).isEqualTo(FuelType.HYBRID);
    assertThat(result.getFirst().cylinderCount()).isEqualTo(4);
    assertThat(result.getFirst().systemPowerKw()).isEqualTo(136);
  }

  @Test
  void existsByCode_delegates_directly() {
    when(jpa.existsByCode("B58")).thenReturn(true);

    assertThat(adapter.existsByCode("B58")).isTrue();
  }

  @Test
  void existsByCode_returns_false_when_jpa_says_no() {
    when(jpa.existsByCode("ZZZ")).thenReturn(false);

    assertThat(adapter.existsByCode("ZZZ")).isFalse();
  }

  @Test
  void deleteById_unwraps_id() {
    adapter.deleteById(new EngineId(7L));

    verify(jpa).deleteById(7L);
  }
}
