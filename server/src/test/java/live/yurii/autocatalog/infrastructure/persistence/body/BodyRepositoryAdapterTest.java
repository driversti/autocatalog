package live.yurii.autocatalog.infrastructure.persistence.body;

import live.yurii.autocatalog.domain.body.Body;
import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.body.BodyStyle;
import live.yurii.autocatalog.domain.generation.GenerationId;
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
class BodyRepositoryAdapterTest {

  @Mock
  private BodyJpaRepository jpa;

  private BodyRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new BodyRepositoryAdapter(jpa);
  }

  @Test
  void save_persists_entity_with_all_fields() {
    var generationId = GenerationId.generate();
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);
    body.setTrunkVolumeLitres(480);

    var result = adapter.save(body);

    var captor = ArgumentCaptor.forClass(BodyJpaEntity.class);
    verify(jpa).save(captor.capture());
    var entity = captor.getValue();
    assertThat(entity.id()).isEqualTo(body.id().value());
    assertThat(entity.generationId()).isEqualTo(generationId.value());
    assertThat(entity.bodyStyle()).isEqualTo(BodyStyle.SEDAN);
    assertThat(entity.lengthMm()).isEqualTo(4650);
    assertThat(entity.widthMm()).isEqualTo(1810);
    assertThat(entity.heightMm()).isEqualTo(1440);
    assertThat(entity.wheelbaseMm()).isEqualTo(2800);
    assertThat(entity.trunkVolumeLitres()).isEqualTo(480);
    assertThat(result).isSameAs(body);
  }

  @Test
  void save_allows_null_trunk_volume() {
    var body = Body.create(GenerationId.generate(), BodyStyle.COUPE, 4400, 1800, 1380, 2700);

    adapter.save(body);

    var captor = ArgumentCaptor.forClass(BodyJpaEntity.class);
    verify(jpa).save(captor.capture());
    assertThat(captor.getValue().trunkVolumeLitres()).isNull();
  }

  @Test
  void findById_returns_mapped_domain() {
    var id = UUID.randomUUID();
    var generationId = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.of(new BodyJpaEntity(
      id, generationId, BodyStyle.WAGON, 4700, 1810, 1460, 2800, 600, null
    )));

    var result = adapter.findById(new BodyId(id));

    assertThat(result).isPresent();
    var body = result.get();
    assertThat(body.id().value()).isEqualTo(id);
    assertThat(body.generationId().value()).isEqualTo(generationId);
    assertThat(body.bodyStyle()).isEqualTo(BodyStyle.WAGON);
    assertThat(body.lengthMm()).isEqualTo(4700);
    assertThat(body.trunkVolumeLitres()).isEqualTo(600);
  }

  @Test
  void findById_returns_empty_when_missing() {
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.empty());

    assertThat(adapter.findById(new BodyId(id))).isEmpty();
  }

  @Test
  void findByGenerationId_delegates_and_maps() {
    var generationId = UUID.randomUUID();
    when(jpa.findByGenerationId(generationId)).thenReturn(List.of(
      new BodyJpaEntity(UUID.randomUUID(), generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800, null, null),
      new BodyJpaEntity(UUID.randomUUID(), generationId, BodyStyle.WAGON, 4700, 1810, 1460, 2800, 600, null)
    ));

    var result = adapter.findByGenerationId(new GenerationId(generationId));

    assertThat(result).extracting(Body::bodyStyle)
      .containsExactly(BodyStyle.SEDAN, BodyStyle.WAGON);
  }

  @Test
  void existsByGenerationIdAndBodyStyle_delegates() {
    var generationId = UUID.randomUUID();
    when(jpa.existsByGenerationIdAndBodyStyle(generationId, BodyStyle.SEDAN)).thenReturn(true);

    assertThat(adapter.existsByGenerationIdAndBodyStyle(
      new GenerationId(generationId), BodyStyle.SEDAN)).isTrue();
  }

  @Test
  void deleteById_unwraps_id() {
    var id = UUID.randomUUID();

    adapter.deleteById(new BodyId(id));

    verify(jpa).deleteById(id);
  }
}
