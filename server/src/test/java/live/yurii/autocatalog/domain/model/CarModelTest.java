package live.yurii.autocatalog.domain.model;

import live.yurii.autocatalog.domain.make.MakeId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarModelTest {

  private final MakeId makeId = MakeId.generate();

  @Test
  void create_assigns_id_and_slugifies_name() {
    var model = CarModel.create(makeId, "3 Series");

    assertThat(model.id()).isNotNull();
    assertThat(model.makeId()).isEqualTo(makeId);
    assertThat(model.name()).isEqualTo("3 Series");
    assertThat(model.slug()).isEqualTo("3-series");
    assertThat(model.relations()).isEmpty();
  }

  @Test
  void create_rejects_null_make_id() {
    assertThatThrownBy(() -> CarModel.create(null, "A4"))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void create_rejects_blank_name() {
    assertThatThrownBy(() -> CarModel.create(makeId, "   "))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("name");
  }

  @Test
  void reconstitute_preserves_fields() {
    var id = ModelId.generate();

    var model = CarModel.reconstitute(id, makeId, "Legacy", "legacy-slug");

    assertThat(model.id()).isEqualTo(id);
    assertThat(model.makeId()).isEqualTo(makeId);
    assertThat(model.name()).isEqualTo("Legacy");
    assertThat(model.slug()).isEqualTo("legacy-slug");
  }

  @Test
  void addRelation_appends_to_relations_list() {
    var model = CarModel.create(makeId, "E30");
    var target = ModelId.generate();

    model.addRelation(target, ModelRelation.Type.SUCCESSOR, "replaced by E36");

    assertThat(model.relations()).hasSize(1);
    var relation = model.relations().getFirst();
    assertThat(relation.targetModelId()).isEqualTo(target);
    assertThat(relation.type()).isEqualTo(ModelRelation.Type.SUCCESSOR);
    assertThat(relation.note()).isEqualTo("replaced by E36");
  }

  @Test
  void addRelation_rejects_self_reference() {
    var model = CarModel.create(makeId, "Golf");

    assertThatThrownBy(() ->
      model.addRelation(model.id(), ModelRelation.Type.REBADGE, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("itself");
  }

  @Test
  void addRelation_rejects_duplicate_target_with_same_type() {
    var model = CarModel.create(makeId, "Passat");
    var target = ModelId.generate();
    model.addRelation(target, ModelRelation.Type.PLATFORM_SIBLING, "B8");

    assertThatThrownBy(() ->
      model.addRelation(target, ModelRelation.Type.PLATFORM_SIBLING, "different note"))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("already exists");
  }

  @Test
  void addRelation_allows_same_target_with_different_type() {
    var model = CarModel.create(makeId, "Passat");
    var target = ModelId.generate();
    model.addRelation(target, ModelRelation.Type.PLATFORM_SIBLING, null);

    model.addRelation(target, ModelRelation.Type.REBADGE, null);

    assertThat(model.relations()).hasSize(2);
  }

  @Test
  void rename_updates_name_and_slug() {
    var model = CarModel.create(makeId, "Corolla");

    model.rename("Camry");

    assertThat(model.name()).isEqualTo("Camry");
    assertThat(model.slug()).isEqualTo("camry");
  }

  @Test
  void rename_rejects_blank() {
    var model = CarModel.create(makeId, "Corolla");

    assertThatThrownBy(() -> model.rename("   "))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("name");
  }

  @Test
  void relations_list_is_unmodifiable() {
    var model = CarModel.create(makeId, "A4");

    assertThatThrownBy(() ->
      model.relations().add(new ModelRelation(ModelId.generate(), ModelRelation.Type.SUCCESSOR, null)))
      .isInstanceOf(UnsupportedOperationException.class);
  }
}
