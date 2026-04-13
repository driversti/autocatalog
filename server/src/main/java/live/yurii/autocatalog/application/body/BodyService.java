package live.yurii.autocatalog.application.body;

import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.body.Body;
import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.body.BodyRepository;
import live.yurii.autocatalog.domain.body.BodyStyle;
import live.yurii.autocatalog.domain.generation.GenerationId;
import live.yurii.autocatalog.domain.generation.GenerationRepository;
import live.yurii.autocatalog.domain.variant.VariantRepository;

import java.util.List;

public class BodyService {

  private final BodyRepository bodyRepository;
  private final GenerationRepository generationRepository;
  private final VariantRepository variantRepository;

  public BodyService(BodyRepository bodyRepository, GenerationRepository generationRepository,
                     VariantRepository variantRepository) {
    this.bodyRepository = bodyRepository;
    this.generationRepository = generationRepository;
    this.variantRepository = variantRepository;
  }

  public Body create(GenerationId generationId, BodyStyle bodyStyle,
                     int lengthMm, int widthMm, int heightMm, Integer wheelbaseMm,
                     Integer trunkVolumeLitres, Integer groundClearanceMm) {
    if (generationRepository.findById(generationId).isEmpty())
      throw new EntityNotFoundException("Generation not found: " + generationId.value());
    if (bodyRepository.existsByGenerationIdAndBodyStyle(generationId, bodyStyle))
      throw new IllegalStateException(
        "Body already exists for generation " + generationId.value() + " with style " + bodyStyle);
    var body = Body.create(generationId, bodyStyle, lengthMm, widthMm, heightMm, wheelbaseMm);
    if (trunkVolumeLitres != null) body.setTrunkVolumeLitres(trunkVolumeLitres);
    if (groundClearanceMm != null) body.setGroundClearanceMm(groundClearanceMm);
    return bodyRepository.save(body);
  }

  public Body getById(BodyId id) {
    return bodyRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Body not found: " + id.value()));
  }

  public List<Body> getByGeneration(GenerationId generationId) {
    return bodyRepository.findByGenerationId(generationId);
  }

  public void deleteById(BodyId id) {
    getById(id);
    if (variantRepository.existsByBodyId(id))
      throw new IllegalStateException("Cannot delete body: dependent variants exist");
    bodyRepository.deleteById(id);
  }

  public Body updateDimensions(BodyId id, int lengthMm, int widthMm, int heightMm, Integer wheelbaseMm,
                               Integer trunkVolumeLitres, Integer groundClearanceMm) {
    var body = getById(id);
    body.updateDimensions(lengthMm, widthMm, heightMm, wheelbaseMm, trunkVolumeLitres, groundClearanceMm);
    return bodyRepository.save(body);
  }
}
