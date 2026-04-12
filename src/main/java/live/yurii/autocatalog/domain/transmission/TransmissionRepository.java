package live.yurii.autocatalog.domain.transmission;

import java.util.List;
import java.util.Optional;

public interface TransmissionRepository {
  Transmission save(Transmission transmission);

  Optional<Transmission> findById(TransmissionId id);

  List<Transmission> findAll();

  List<Transmission> findByType(TransmissionType type);

  boolean existsByTypeAndGearCount(TransmissionType type, int gearCount);

  void deleteById(TransmissionId id);
}
