package live.yurii.autocatalog.infrastructure.persistence.variant;

import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.variant.Drivetrain;
import live.yurii.autocatalog.domain.variant.Variant;
import live.yurii.autocatalog.domain.variant.VariantId;
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
class VariantRepositoryAdapterTest {

  @Mock
  private VariantJpaRepository jpa;

  private VariantRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new VariantRepositoryAdapter(jpa);
  }

  @Test
  void save_persists_entity_with_basic_fields() {
    var bodyId = BodyId.generate();
    var powertrainId = PowertrainId.generate();
    var transmissionId = TransmissionId.generate();
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);

    var result = adapter.save(variant);

    var captor = ArgumentCaptor.forClass(VariantJpaEntity.class);
    verify(jpa).save(captor.capture());
    var entity = captor.getValue();
    assertThat(entity.id()).isEqualTo(variant.id().value());
    assertThat(entity.bodyId()).isEqualTo(bodyId.value());
    assertThat(entity.powertrainId()).isEqualTo(powertrainId.value());
    assertThat(entity.transmissionId()).isEqualTo(transmissionId.value());
    assertThat(entity.drivetrain()).isEqualTo(Drivetrain.FWD);
    assertThat(entity.curbWeightKg()).isEqualTo(1350);
    assertThat(entity.groundClearanceMm()).isNull();
    assertThat(result).isSameAs(variant);
  }

  @Test
  void save_propagates_markets() {
    var variant = Variant.create(BodyId.generate(), PowertrainId.generate(),
      TransmissionId.generate(), Drivetrain.AWD, 1800);
    variant.addMarket("UA");
    variant.addMarket("EU");

    adapter.save(variant);

    var captor = ArgumentCaptor.forClass(VariantJpaEntity.class);
    verify(jpa).save(captor.capture());
    assertThat(captor.getValue().markets()).containsExactlyInAnyOrder("UA", "EU");
  }

  @Test
  void save_propagates_optional_fields() {
    var variant = Variant.create(BodyId.generate(), PowertrainId.generate(),
      TransmissionId.generate(), Drivetrain.FWD, 1400);
    variant.setGroundClearanceMm(180);

    adapter.save(variant);

    var captor = ArgumentCaptor.forClass(VariantJpaEntity.class);
    verify(jpa).save(captor.capture());
    assertThat(captor.getValue().groundClearanceMm()).isEqualTo(180);
  }

  @Test
  void findById_returns_mapped_domain_with_markets() {
    var id = UUID.randomUUID();
    var bodyId = UUID.randomUUID();
    var powertrainId = UUID.randomUUID();
    var transmissionId = UUID.randomUUID();
    var entity = new VariantJpaEntity(id, bodyId, powertrainId, transmissionId,
      Drivetrain.RWD, 130, 1600);
    entity.markets().add("US");
    when(jpa.findById(id)).thenReturn(Optional.of(entity));

    var result = adapter.findById(new VariantId(id));

    assertThat(result).isPresent();
    var variant = result.get();
    assertThat(variant.id().value()).isEqualTo(id);
    assertThat(variant.bodyId().value()).isEqualTo(bodyId);
    assertThat(variant.powertrainId().value()).isEqualTo(powertrainId);
    assertThat(variant.transmissionId().value()).isEqualTo(transmissionId);
    assertThat(variant.drivetrain()).isEqualTo(Drivetrain.RWD);
    assertThat(variant.groundClearanceMm()).isEqualTo(130);
    assertThat(variant.curbWeightKg()).isEqualTo(1600);
    assertThat(variant.markets()).containsExactly("US");
  }

  @Test
  void findById_returns_empty_when_missing() {
    var id = UUID.randomUUID();
    when(jpa.findById(id)).thenReturn(Optional.empty());

    assertThat(adapter.findById(new VariantId(id))).isEmpty();
  }

  @Test
  void findByBodyId_delegates_and_maps() {
    var bodyId = UUID.randomUUID();
    when(jpa.findByBodyId(bodyId)).thenReturn(List.of(
      new VariantJpaEntity(UUID.randomUUID(), bodyId, UUID.randomUUID(), UUID.randomUUID(),
        Drivetrain.FWD, null, 1350),
      new VariantJpaEntity(UUID.randomUUID(), bodyId, UUID.randomUUID(), UUID.randomUUID(),
        Drivetrain.AWD, null, 1600)
    ));

    var result = adapter.findByBodyId(new BodyId(bodyId));

    assertThat(result).extracting(Variant::drivetrain)
      .containsExactly(Drivetrain.FWD, Drivetrain.AWD);
  }

  @Test
  void existsByBodyIdAndPowertrainIdAndTransmissionIdAndDrivetrain_delegates() {
    var bodyId = UUID.randomUUID();
    var powertrainId = UUID.randomUUID();
    var transmissionId = UUID.randomUUID();
    when(jpa.existsByBodyIdAndPowertrainIdAndTransmissionIdAndDrivetrain(
      bodyId, powertrainId, transmissionId, Drivetrain.AWD)).thenReturn(true);

    assertThat(adapter.existsByBodyIdAndPowertrainIdAndTransmissionIdAndDrivetrain(
      new BodyId(bodyId), new PowertrainId(powertrainId),
      new TransmissionId(transmissionId), Drivetrain.AWD)).isTrue();
  }

  @Test
  void deleteById_unwraps_id() {
    var id = UUID.randomUUID();

    adapter.deleteById(new VariantId(id));

    verify(jpa).deleteById(id);
  }
}
