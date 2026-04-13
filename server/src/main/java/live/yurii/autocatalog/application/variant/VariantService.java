package live.yurii.autocatalog.application.variant;

import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.body.BodyRepository;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.powertrain.PowertrainRepository;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
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
  private final PowertrainRepository powertrainRepository;
  private final TransmissionRepository transmissionRepository;

  public VariantService(VariantRepository variantRepository,
                        BodyRepository bodyRepository,
                        PowertrainRepository powertrainRepository,
                        TransmissionRepository transmissionRepository) {
    this.variantRepository = variantRepository;
    this.bodyRepository = bodyRepository;
    this.powertrainRepository = powertrainRepository;
    this.transmissionRepository = transmissionRepository;
  }

  public Variant create(BodyId bodyId, PowertrainId powertrainId,
                        TransmissionId transmissionId,
                        Drivetrain drivetrain, int curbWeightKg,
                        Integer groundClearanceMm) {
    if (bodyRepository.findById(bodyId).isEmpty())
      throw new EntityNotFoundException("Body not found: " + bodyId.value());
    if (powertrainRepository.findById(powertrainId).isEmpty())
      throw new EntityNotFoundException("Powertrain not found: " + powertrainId.value());
    if (transmissionRepository.findById(transmissionId).isEmpty())
      throw new EntityNotFoundException("Transmission not found: " + transmissionId.value());
    if (variantRepository.existsByBodyIdAndPowertrainIdAndTransmissionIdAndDrivetrain(
      bodyId, powertrainId, transmissionId, drivetrain))
      throw new IllegalStateException(
        "Variant already exists for body " + bodyId.value()
          + " with powertrain " + powertrainId.value()
          + ", transmission " + transmissionId.value()
          + " and drivetrain " + drivetrain);

    var variant = Variant.create(bodyId, powertrainId, transmissionId, drivetrain, curbWeightKg);
    if (groundClearanceMm != null) variant.setGroundClearanceMm(groundClearanceMm);
    return variantRepository.save(variant);
  }

  public Variant getById(VariantId id) {
    return variantRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Variant not found: " + id.value()));
  }

  public List<Variant> getByBody(BodyId bodyId) {
    return variantRepository.findByBodyId(bodyId);
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

  public Variant removeMarket(VariantId id, String market) {
    var variant = getById(id);
    variant.removeMarket(market);
    return variantRepository.save(variant);
  }

  public Variant updateCurbWeightKg(VariantId id, int curbWeightKg) {
    var variant = getById(id);
    variant.updateCurbWeightKg(curbWeightKg);
    return variantRepository.save(variant);
  }

  public Variant updateGroundClearanceMm(VariantId id, int groundClearanceMm) {
    var variant = getById(id);
    variant.setGroundClearanceMm(groundClearanceMm);
    return variantRepository.save(variant);
  }
}
