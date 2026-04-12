package live.yurii.autocatalog.domain.variant;

import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;

import java.util.List;
import java.util.Optional;

public interface VariantRepository {
  Variant save(Variant variant);

  Optional<Variant> findById(VariantId id);

  List<Variant> findByBodyId(BodyId bodyId);

  boolean existsByBodyIdAndTransmissionIdAndDrivetrain(BodyId bodyId, TransmissionId transmissionId,
                                                       Drivetrain drivetrain);

  void deleteById(VariantId id);
}
