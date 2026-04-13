package live.yurii.autocatalog.application.generation;

import live.yurii.autocatalog.domain.body.BodyRepository;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.generation.Generation;
import live.yurii.autocatalog.domain.generation.GenerationId;
import live.yurii.autocatalog.domain.generation.GenerationRepository;
import live.yurii.autocatalog.domain.model.CarModelRepository;
import live.yurii.autocatalog.domain.model.ModelId;
import live.yurii.autocatalog.domain.shared.YearRange;

import java.util.List;

public class GenerationService {

  private final GenerationRepository generationRepository;
  private final CarModelRepository modelRepository;
  private final BodyRepository bodyRepository;

  public GenerationService(GenerationRepository generationRepository,
                           CarModelRepository modelRepository,
                           BodyRepository bodyRepository) {
    this.generationRepository = generationRepository;
    this.modelRepository = modelRepository;
    this.bodyRepository = bodyRepository;
  }

  public Generation create(ModelId modelId, String name, YearRange years) {
    if (modelRepository.findById(modelId).isEmpty())
      throw new EntityNotFoundException("Model not found: " + modelId.value());
    if (generationRepository.existsByModelIdAndName(modelId, name))
      throw new IllegalStateException("Generation already exists for model " + modelId.value() + ": " + name);
    return generationRepository.save(Generation.create(modelId, name, years));
  }

  public Generation getById(GenerationId id) {
    return generationRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Generation not found: " + id.value()));
  }

  public List<Generation> getByModel(ModelId modelId) {
    return generationRepository.findByModelId(modelId);
  }

  public Generation close(GenerationId generationId, int year) {
    var generation = getById(generationId);
    generation.close(year);
    return generationRepository.save(generation);
  }

  public void deleteById(GenerationId id) {
    getById(id);
    if (bodyRepository.existsByGenerationId(id))
      throw new IllegalStateException("Cannot delete generation: dependent bodies exist");
    generationRepository.deleteById(id);
  }

  public Generation rename(GenerationId id, String newName) {
    var generation = getById(id);
    if (generationRepository.existsByModelIdAndName(generation.modelId(), newName))
      throw new IllegalStateException("Generation already exists: " + newName);
    generation.rename(newName);
    return generationRepository.save(generation);
  }
}
