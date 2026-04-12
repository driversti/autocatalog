package live.yurii.autocatalog.infrastructure.persistence.model;

import live.yurii.autocatalog.domain.make.MakeId;
import live.yurii.autocatalog.domain.model.CarModel;
import live.yurii.autocatalog.domain.model.ModelId;
import live.yurii.autocatalog.domain.model.ModelRelation;
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
class CarModelRepositoryAdapterTest {

  @Mock
  private CarModelJpaRepository jpa;

  private CarModelRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new CarModelRepositoryAdapter(jpa);
  }

  @Test
  void save_persists_entity_with_make_name_and_slug() {
    var makeId = MakeId.generate();
    var model = CarModel.create(makeId, "3 Series");

    var result = adapter.save(model);

    var captor = ArgumentCaptor.forClass(CarModelJpaEntity.class);
    verify(jpa).save(captor.capture());
    var entity = captor.getValue();
    assertThat(entity.id()).isEqualTo(model.id().value());
    assertThat(entity.makeId()).isEqualTo(makeId.value());
    assertThat(entity.name()).isEqualTo("3 Series");
    assertThat(entity.slug()).isEqualTo("3-series");
    assertThat(entity.relations()).isEmpty();
    assertThat(result).isSameAs(model);
  }

  @Test
  void save_propagates_model_relations_to_jpa_entity() {
    var makeId = MakeId.generate();
    var model = CarModel.create(makeId, "E90");
    var targetId = ModelId.generate();
    model.addRelation(targetId, ModelRelation.Type.SUCCESSOR, "replaced by F30");

    adapter.save(model);

    var captor = ArgumentCaptor.forClass(CarModelJpaEntity.class);
    verify(jpa).save(captor.capture());
    var relations = captor.getValue().relations();
    assertThat(relations).hasSize(1);
    var relation = relations.getFirst();
    assertThat(relation.toModelId()).isEqualTo(targetId.value());
    assertThat(relation.type()).isEqualTo(ModelRelation.Type.SUCCESSOR);
    assertThat(relation.note()).isEqualTo("replaced by F30");
  }

  @Test
  void findById_returns_mapped_domain_with_empty_relations() {
    var id = UUID.randomUUID();
    var makeId = UUID.randomUUID();
    var entity = new CarModelJpaEntity(id, makeId, "A4", "a4");
    when(jpa.findById(id)).thenReturn(Optional.of(entity));

    var result = adapter.findById(new ModelId(id));

    assertThat(result).isPresent();
    assertThat(result.get().id().value()).isEqualTo(id);
    assertThat(result.get().makeId().value()).isEqualTo(makeId);
    assertThat(result.get().name()).isEqualTo("A4");
    assertThat(result.get().slug()).isEqualTo("a4");
    assertThat(result.get().relations()).isEmpty();
  }

  @Test
  void findById_maps_relations_back_into_domain() {
    var id = UUID.randomUUID();
    var makeId = UUID.randomUUID();
    var targetId = UUID.randomUUID();
    var entity = new CarModelJpaEntity(id, makeId, "E90", "e90");
    entity.setRelations(List.of(
      new ModelRelationJpaEntity(entity, targetId, ModelRelation.Type.SUCCESSOR, "went to F30")
    ));
    when(jpa.findById(id)).thenReturn(Optional.of(entity));

    var result = adapter.findById(new ModelId(id));

    assertThat(result).isPresent();
    assertThat(result.get().relations()).hasSize(1);
    var relation = result.get().relations().getFirst();
    assertThat(relation.targetModelId().value()).isEqualTo(targetId);
    assertThat(relation.type()).isEqualTo(ModelRelation.Type.SUCCESSOR);
    assertThat(relation.note()).isEqualTo("went to F30");
  }

  @Test
  void findById_returns_empty_when_absent() {
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.empty());

    assertThat(adapter.findById(new ModelId(id))).isEmpty();
  }

  @Test
  void findBySlug_returns_mapped_domain() {
    var id = UUID.randomUUID();
    var makeId = UUID.randomUUID();
    when(jpa.findBySlug("a4")).thenReturn(Optional.of(new CarModelJpaEntity(id, makeId, "A4", "a4")));

    var result = adapter.findBySlug("a4");

    assertThat(result).isPresent();
    assertThat(result.get().slug()).isEqualTo("a4");
  }

  @Test
  void findByMakeId_delegates_and_maps() {
    var makeId = UUID.randomUUID();
    when(jpa.findByMakeId(makeId)).thenReturn(List.of(
      new CarModelJpaEntity(UUID.randomUUID(), makeId, "3 Series", "3-series"),
      new CarModelJpaEntity(UUID.randomUUID(), makeId, "5 Series", "5-series")
    ));

    var result = adapter.findByMakeId(new MakeId(makeId));

    assertThat(result).extracting(CarModel::name).containsExactly("3 Series", "5 Series");
    assertThat(result).allMatch(m -> m.makeId().value().equals(makeId));
  }

  @Test
  void findByMakeId_returns_empty_when_no_models() {
    var makeId = UUID.randomUUID();
    when(jpa.findByMakeId(makeId)).thenReturn(List.of());

    assertThat(adapter.findByMakeId(new MakeId(makeId))).isEmpty();
  }

  @Test
  void deleteById_unwraps_id() {
    var id = UUID.randomUUID();

    adapter.deleteById(new ModelId(id));

    verify(jpa).deleteById(id);
  }
}
