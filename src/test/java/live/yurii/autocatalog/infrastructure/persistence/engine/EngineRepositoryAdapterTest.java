package live.yurii.autocatalog.infrastructure.persistence.engine;

import live.yurii.autocatalog.domain.engine.Engine;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.engine.FuelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

  @Test
  void save_persists_entity_with_all_engine_fields() {
    var engine = Engine.create("B58", "3.0L TwinPower", FuelType.PETROL, 2998, 250, 500);
    engine.setCylinderCount(6);
    engine.setConfiguration("I6");
    engine.setSystemPowerKw(280);

    var result = adapter.save(engine);

    var captor = ArgumentCaptor.forClass(EngineJpaEntity.class);
    verify(jpa).save(captor.capture());
    var entity = captor.getValue();
    assertThat(entity.id()).isEqualTo(engine.id().value());
    assertThat(entity.code()).isEqualTo("B58");
    assertThat(entity.name()).isEqualTo("3.0L TwinPower");
    assertThat(entity.fuelType()).isEqualTo(FuelType.PETROL);
    assertThat(entity.displacementCc()).isEqualTo(2998);
    assertThat(entity.powerKw()).isEqualTo(250);
    assertThat(entity.torqueNm()).isEqualTo(500);
    assertThat(entity.cylinderCount()).isEqualTo(6);
    assertThat(entity.configuration()).isEqualTo("I6");
    assertThat(entity.systemPowerKw()).isEqualTo(280);
    assertThat(result).isSameAs(engine);
  }

  @Test
  void save_allows_null_cylinder_count_and_configuration() {
    var engine = Engine.create("EV1", "Electric", FuelType.ELECTRIC, 1, 200, 400);

    adapter.save(engine);

    var captor = ArgumentCaptor.forClass(EngineJpaEntity.class);
    verify(jpa).save(captor.capture());
    assertThat(captor.getValue().cylinderCount()).isNull();
    assertThat(captor.getValue().configuration()).isNull();
    assertThat(captor.getValue().systemPowerKw()).isNull();
  }

  @Test
  void findById_returns_mapped_domain() {
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.of(new EngineJpaEntity(
      id, "M20", "2.0L M", FuelType.DIESEL, 1998, 140, 320, 4, "I4", null
    )));

    var result = adapter.findById(new EngineId(id));

    assertThat(result).isPresent();
    var engine = result.get();
    assertThat(engine.id().value()).isEqualTo(id);
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
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.empty());

    assertThat(adapter.findById(new EngineId(id))).isEmpty();
  }

  @Test
  void findByCode_returns_mapped_domain() {
    var id = UUID.randomUUID();
    when(jpa.findByCode("M20")).thenReturn(Optional.of(new EngineJpaEntity(
      id, "M20", "2.0L M", FuelType.DIESEL, 1998, 140, 320, null, null, null
    )));

    var result = adapter.findByCode("M20");

    assertThat(result).isPresent();
    assertThat(result.get().code()).isEqualTo("M20");
  }

  @Test
  void findAll_maps_every_entity() {
    when(jpa.findAll()).thenReturn(List.of(
      new EngineJpaEntity(UUID.randomUUID(), "A", "A", FuelType.PETROL, 1000, 50, 100, null, null, null),
      new EngineJpaEntity(UUID.randomUUID(), "B", "B", FuelType.DIESEL, 2000, 100, 200, null, null, null)
    ));

    var all = adapter.findAll();

    assertThat(all).extracting(Engine::code).containsExactly("A", "B");
  }

  @Test
  void findByFuelType_delegates_and_maps() {
    when(jpa.findByFuelType(FuelType.HYBRID)).thenReturn(List.of(
      new EngineJpaEntity(UUID.randomUUID(), "H1", "Hybrid", FuelType.HYBRID, 1500, 80, 150, 4, "I4", 136)
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
    var id = UUID.randomUUID();

    adapter.deleteById(new EngineId(id));

    verify(jpa).deleteById(id);
  }
}
