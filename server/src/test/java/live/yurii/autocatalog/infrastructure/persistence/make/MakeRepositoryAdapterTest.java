package live.yurii.autocatalog.infrastructure.persistence.make;

import live.yurii.autocatalog.domain.make.Make;
import live.yurii.autocatalog.domain.make.MakeId;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeRepositoryAdapterTest {

  @Mock
  private MakeJpaRepository jpa;

  private MakeRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new MakeRepositoryAdapter(jpa);
  }

  @Test
  void save_persists_entity_with_all_fields_and_returns_same_domain_object() {
    var make = Make.create("BMW", "Germany");

    var result = adapter.save(make);

    var captor = ArgumentCaptor.forClass(MakeJpaEntity.class);
    verify(jpa).save(captor.capture());
    var entity = captor.getValue();
    assertThat(entity.id()).isEqualTo(make.id().value());
    assertThat(entity.name()).isEqualTo("BMW");
    assertThat(entity.country()).isEqualTo("Germany");
    assertThat(entity.slug()).isEqualTo("bmw");
    assertThat(result).isSameAs(make);
  }

  @Test
  void findById_returns_mapped_domain_when_entity_exists() {
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.of(new MakeJpaEntity(id, "Audi", "Germany", "audi")));

    var result = adapter.findById(new MakeId(id));

    assertThat(result).isPresent();
    assertThat(result.get().id().value()).isEqualTo(id);
    assertThat(result.get().name()).isEqualTo("Audi");
    assertThat(result.get().country()).isEqualTo("Germany");
    assertThat(result.get().slug()).isEqualTo("audi");
  }

  @Test
  void findById_returns_empty_when_entity_absent() {
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.empty());

    assertThat(adapter.findById(new MakeId(id))).isEmpty();
  }

  @Test
  void findBySlug_returns_mapped_domain_when_entity_exists() {
    var id = UUID.randomUUID();
    when(jpa.findBySlug("bmw")).thenReturn(Optional.of(new MakeJpaEntity(id, "BMW", "Germany", "bmw")));

    var result = adapter.findBySlug("bmw");

    assertThat(result).isPresent();
    assertThat(result.get().name()).isEqualTo("BMW");
    assertThat(result.get().slug()).isEqualTo("bmw");
  }

  @Test
  void findBySlug_returns_empty_when_not_found() {
    when(jpa.findBySlug("missing")).thenReturn(Optional.empty());

    assertThat(adapter.findBySlug("missing")).isEmpty();
  }

  @Test
  void findAll_maps_every_entity_preserving_order() {
    var id1 = UUID.randomUUID();
    var id2 = UUID.randomUUID();
    when(jpa.findAll()).thenReturn(List.of(
      new MakeJpaEntity(id1, "BMW", "Germany", "bmw"),
      new MakeJpaEntity(id2, "Audi", "Germany", "audi")
    ));

    var all = adapter.findAll();

    assertThat(all).extracting(Make::name).containsExactly("BMW", "Audi");
    assertThat(all).extracting(m -> m.id().value()).containsExactly(id1, id2);
  }

  @Test
  void findAll_returns_empty_list_when_no_entities() {
    when(jpa.findAll()).thenReturn(List.of());

    assertThat(adapter.findAll()).isEmpty();
  }

  @Test
  void existsByName_delegates_directly() {
    when(jpa.existsByName("BMW")).thenReturn(true);

    assertThat(adapter.existsByName("BMW")).isTrue();
  }

  @Test
  void existsByName_returns_false_when_jpa_says_no() {
    when(jpa.existsByName("Unknown")).thenReturn(false);

    assertThat(adapter.existsByName("Unknown")).isFalse();
  }

  @Test
  void deleteById_unwraps_MakeId_to_raw_UUID() {
    var id = UUID.randomUUID();

    adapter.deleteById(new MakeId(id));

    verify(jpa).deleteById(id);
  }

  @Test
  void existsByName_makes_exactly_one_jpa_call() {
    adapter.existsByName("BMW");

    verify(jpa).existsByName("BMW");
    verifyNoMoreInteractions(jpa);
  }
}
