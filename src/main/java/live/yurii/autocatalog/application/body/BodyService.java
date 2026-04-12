package live.yurii.autocatalog.application.body;

import live.yurii.autocatalog.application.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.body.Body;
import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.body.BodyRepository;
import live.yurii.autocatalog.domain.body.BodyStyle;
import live.yurii.autocatalog.domain.generation.GenerationId;
import live.yurii.autocatalog.domain.generation.GenerationRepository;

import java.util.List;

public class BodyService {

  private final BodyRepository bodyRepository;
  private final GenerationRepository generationRepository;

  public BodyService(BodyRepository bodyRepository, GenerationRepository generationRepository) {
    this.bodyRepository = bodyRepository;
    this.generationRepository = generationRepository;
  }

  public Body create(GenerationId generationId, BodyStyle bodyStyle,
                     int lengthMm, int widthMm, int heightMm, int wheelbaseMm,
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
    bodyRepository.deleteById(id);
  }
}
