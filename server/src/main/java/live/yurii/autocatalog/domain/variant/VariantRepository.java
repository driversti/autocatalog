package live.yurii.autocatalog.domain.variant;

import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;

import java.util.List;
import java.util.Optional;

public interface VariantRepository {
  Variant save(Variant variant);

  Optional<Variant> findById(VariantId id);

  List<Variant> findByBodyId(BodyId bodyId);

  boolean existsByBodyIdAndPowertrainIdAndTransmissionIdAndDrivetrain(
    BodyId bodyId, PowertrainId powertrainId,
    TransmissionId transmissionId, Drivetrain drivetrain);

  boolean existsByBodyId(BodyId bodyId);

  boolean existsByPowertrainId(PowertrainId powertrainId);

  boolean existsByTransmissionId(TransmissionId transmissionId);

  void deleteById(VariantId id);
}
