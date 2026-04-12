package live.yurii.autocatalog.infrastructure.persistence.transmission;

import live.yurii.autocatalog.domain.transmission.Transmission;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.transmission.TransmissionType;
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
class TransmissionRepositoryAdapterTest {

  @Mock
  private TransmissionJpaRepository jpa;

  private TransmissionRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new TransmissionRepositoryAdapter(jpa);
  }

  @Test
  void save_persists_entity_with_id_type_and_gear_count() {
    var transmission = Transmission.create(TransmissionType.MANUAL, 6);

    var result = adapter.save(transmission);

    var captor = ArgumentCaptor.forClass(TransmissionJpaEntity.class);
    verify(jpa).save(captor.capture());
    var entity = captor.getValue();
    assertThat(entity.id()).isEqualTo(transmission.id().value());
    assertThat(entity.type()).isEqualTo(TransmissionType.MANUAL);
    assertThat(entity.gearCount()).isEqualTo(6);
    assertThat(result).isSameAs(transmission);
  }

  @Test
  void findById_returns_mapped_domain_when_present() {
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.of(new TransmissionJpaEntity(id, TransmissionType.DCT, 7)));

    var result = adapter.findById(new TransmissionId(id));

    assertThat(result).isPresent();
    assertThat(result.get().id().value()).isEqualTo(id);
    assertThat(result.get().type()).isEqualTo(TransmissionType.DCT);
    assertThat(result.get().gearCount()).isEqualTo(7);
  }

  @Test
  void findById_returns_empty_when_absent() {
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.empty());

    assertThat(adapter.findById(new TransmissionId(id))).isEmpty();
  }

  @Test
  void findAll_maps_each_entity_to_domain() {
    when(jpa.findAll()).thenReturn(List.of(
      new TransmissionJpaEntity(UUID.randomUUID(), TransmissionType.MANUAL, 5),
      new TransmissionJpaEntity(UUID.randomUUID(), TransmissionType.AUTOMATIC, 8)
    ));

    var all = adapter.findAll();

    assertThat(all).hasSize(2);
    assertThat(all).extracting(Transmission::type)
      .containsExactly(TransmissionType.MANUAL, TransmissionType.AUTOMATIC);
    assertThat(all).extracting(Transmission::gearCount).containsExactly(5, 8);
  }

  @Test
  void findByType_delegates_and_maps() {
    when(jpa.findByType(TransmissionType.CVT)).thenReturn(List.of(
      new TransmissionJpaEntity(UUID.randomUUID(), TransmissionType.CVT, 1)
    ));

    var result = adapter.findByType(TransmissionType.CVT);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().type()).isEqualTo(TransmissionType.CVT);
  }

  @Test
  void findByType_returns_empty_list_for_no_matches() {
    when(jpa.findByType(TransmissionType.SINGLE_SPEED)).thenReturn(List.of());

    assertThat(adapter.findByType(TransmissionType.SINGLE_SPEED)).isEmpty();
  }

  @Test
  void deleteById_unwraps_id() {
    var id = UUID.randomUUID();

    adapter.deleteById(new TransmissionId(id));

    verify(jpa).deleteById(id);
  }
}
