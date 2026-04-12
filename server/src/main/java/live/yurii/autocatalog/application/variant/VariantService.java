package live.yurii.autocatalog.application.variant;

import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.body.BodyRepository;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.engine.EngineRepository;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.transmission.TransmissionRepository;
import live.yurii.autocatalog.domain.variant.Drivetrain;
import live.yurii.autocatalog.domain.variant.Variant;
import live.yurii.autocatalog.domain.variant.VariantId;
import live.yurii.autocatalog.domain.variant.VariantRepository;

import java.util.List;

public class VariantService {

  private final VariantRepository variantRepository;
  private final BodyRepository bodyRepository;
  private final EngineRepository engineRepository;
  private final TransmissionRepository transmissionRepository;

  public VariantService(VariantRepository variantRepository,
                        BodyRepository bodyRepository,
                        EngineRepository engineRepository,
                        TransmissionRepository transmissionRepository) {
    this.variantRepository = variantRepository;
    this.bodyRepository = bodyRepository;
    this.engineRepository = engineRepository;
    this.transmissionRepository = transmissionRepository;
  }

  public Variant create(BodyId bodyId, TransmissionId transmissionId,
                        Drivetrain drivetrain, int curbWeightKg,
                        Integer groundClearanceMm, Integer systemPowerKw) {
    if (bodyRepository.findById(bodyId).isEmpty())
      throw new EntityNotFoundException("Body not found: " + bodyId.value());
    if (transmissionRepository.findById(transmissionId).isEmpty())
      throw new EntityNotFoundException("Transmission not found: " + transmissionId.value());
    if (variantRepository.existsByBodyIdAndTransmissionIdAndDrivetrain(bodyId, transmissionId, drivetrain))
      throw new IllegalStateException(
        "Variant already exists for body " + bodyId.value()
          + " with transmission " + transmissionId.value()
          + " and drivetrain " + drivetrain);
    var variant = Variant.create(bodyId, transmissionId, drivetrain, curbWeightKg);
    if (groundClearanceMm != null) variant.setGroundClearanceMm(groundClearanceMm);
    if (systemPowerKw != null) variant.setSystemPowerKw(systemPowerKw);
    return variantRepository.save(variant);
  }

  public Variant getById(VariantId id) {
    return variantRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Variant not found: " + id.value()));
  }

  public List<Variant> getByBody(BodyId bodyId) {
    return variantRepository.findByBodyId(bodyId);
  }

  public Variant addEngine(VariantId variantId, EngineId engineId) {
    var variant = getById(variantId);
    if (engineRepository.findById(engineId).isEmpty())
      throw new EntityNotFoundException("Engine not found: " + engineId.value());
    variant.addEngine(engineId);
    return variantRepository.save(variant);
  }

  public Variant addMarket(VariantId variantId, String market) {
    var variant = getById(variantId);
    variant.addMarket(market);
    return variantRepository.save(variant);
  }

  public void deleteById(VariantId id) {
    getById(id);
    variantRepository.deleteById(id);
  }
}
