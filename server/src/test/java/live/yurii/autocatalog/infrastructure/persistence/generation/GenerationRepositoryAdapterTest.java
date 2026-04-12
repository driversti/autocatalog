package live.yurii.autocatalog.infrastructure.persistence.generation;

import live.yurii.autocatalog.domain.generation.Generation;
import live.yurii.autocatalog.domain.generation.GenerationId;
import live.yurii.autocatalog.domain.model.ModelId;
import live.yurii.autocatalog.domain.shared.YearRange;
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
class GenerationRepositoryAdapterTest {

  @Mock
  private GenerationJpaRepository jpa;

  private GenerationRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new GenerationRepositoryAdapter(jpa);
  }

  @Test
  void save_persists_basic_fields_and_open_year_range() {
    var modelId = ModelId.generate();
    var generation = Generation.create(modelId, "F30", new YearRange(2012, null));

    var result = adapter.save(generation);

    var captor = ArgumentCaptor.forClass(GenerationJpaEntity.class);
    verify(jpa).save(captor.capture());
    var entity = captor.getValue();
    assertThat(entity.id()).isEqualTo(generation.id().value());
    assertThat(entity.modelId()).isEqualTo(modelId.value());
    assertThat(entity.name()).isEqualTo("F30");
    assertThat(entity.yearFrom()).isEqualTo(2012);
    assertThat(entity.yearTo()).isNull();
    assertThat(result).isSameAs(generation);
  }

  @Test
  void save_persists_closed_year_range() {
    var generation = Generation.create(ModelId.generate(), "F30", new YearRange(2012, 2019));

    adapter.save(generation);

    var captor = ArgumentCaptor.forClass(GenerationJpaEntity.class);
    verify(jpa).save(captor.capture());
    assertThat(captor.getValue().yearTo()).isEqualTo(2019);
  }

  @Test
  void findById_returns_mapped_domain() {
    var id = UUID.randomUUID();
    var modelId = UUID.randomUUID();
    var entity = new GenerationJpaEntity(id, modelId, "F30", 2012, 2019);
    when(jpa.findById(id)).thenReturn(Optional.of(entity));

    var result = adapter.findById(new GenerationId(id));

    assertThat(result).isPresent();
    var gen = result.get();
    assertThat(gen.id().value()).isEqualTo(id);
    assertThat(gen.modelId().value()).isEqualTo(modelId);
    assertThat(gen.name()).isEqualTo("F30");
    assertThat(gen.years().from()).isEqualTo(2012);
    assertThat(gen.years().to()).isEqualTo(2019);
  }

  @Test
  void findById_maps_open_ended_year_range() {
    var id = UUID.randomUUID();
    var entity = new GenerationJpaEntity(id, UUID.randomUUID(), "Current", 2020, null);
    when(jpa.findById(id)).thenReturn(Optional.of(entity));

    var result = adapter.findById(new GenerationId(id));

    assertThat(result).isPresent();
    assertThat(result.get().years().isCurrent()).isTrue();
    assertThat(result.get().years().to()).isNull();
  }

  @Test
  void findById_returns_empty_when_absent() {
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.empty());

    assertThat(adapter.findById(new GenerationId(id))).isEmpty();
  }

  @Test
  void findByModelId_delegates_and_maps() {
    var modelId = UUID.randomUUID();
    when(jpa.findByModelId(modelId)).thenReturn(List.of(
      new GenerationJpaEntity(UUID.randomUUID(), modelId, "E90", 2005, 2012),
      new GenerationJpaEntity(UUID.randomUUID(), modelId, "F30", 2012, 2019)
    ));

    var result = adapter.findByModelId(new ModelId(modelId));

    assertThat(result).extracting(Generation::name).containsExactly("E90", "F30");
  }

  @Test
  void findByModelId_returns_empty_when_no_generations() {
    var modelId = UUID.randomUUID();
    when(jpa.findByModelId(modelId)).thenReturn(List.of());

    assertThat(adapter.findByModelId(new ModelId(modelId))).isEmpty();
  }

  @Test
  void deleteById_unwraps_id() {
    var id = UUID.randomUUID();

    adapter.deleteById(new GenerationId(id));

    verify(jpa).deleteById(id);
  }
}
