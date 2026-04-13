package live.yurii.autocatalog.application.transmission;

import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.transmission.Transmission;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.transmission.TransmissionRepository;
import live.yurii.autocatalog.domain.transmission.TransmissionType;
import live.yurii.autocatalog.domain.variant.VariantRepository;

import java.util.List;

public class TransmissionService {

  private final TransmissionRepository transmissionRepository;
  private final VariantRepository variantRepository;

  public TransmissionService(TransmissionRepository transmissionRepository,
                              VariantRepository variantRepository) {
    this.transmissionRepository = transmissionRepository;
    this.variantRepository = variantRepository;
  }

  public Transmission create(TransmissionType type, int gearCount) {
    if (transmissionRepository.existsByTypeAndGearCount(type, gearCount))
      throw new IllegalStateException("Transmission already exists: " + type + " " + gearCount + "-speed");
    return transmissionRepository.save(Transmission.create(type, gearCount));
  }

  public Transmission getById(TransmissionId id) {
    return transmissionRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Transmission not found: " + id.value()));
  }

  public List<Transmission> getAll() {
    return transmissionRepository.findAll();
  }

  public List<Transmission> getByType(TransmissionType type) {
    return transmissionRepository.findByType(type);
  }

  public void deleteById(TransmissionId id) {
    getById(id);
    if (variantRepository.existsByTransmissionId(id))
      throw new IllegalStateException("Cannot delete transmission: referenced by variant(s)");
    transmissionRepository.deleteById(id);
  }
}
