package live.yurii.autocatalog.application.model;

import live.yurii.autocatalog.application.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.make.MakeId;
import live.yurii.autocatalog.domain.make.MakeRepository;
import live.yurii.autocatalog.domain.model.CarModel;
import live.yurii.autocatalog.domain.model.CarModelRepository;
import live.yurii.autocatalog.domain.model.ModelId;
import live.yurii.autocatalog.domain.model.ModelRelation;

import java.util.List;

public class CarModelService {

  private final CarModelRepository modelRepository;
  private final MakeRepository makeRepository;

  public CarModelService(CarModelRepository modelRepository, MakeRepository makeRepository) {
    this.modelRepository = modelRepository;
    this.makeRepository = makeRepository;
  }

  public CarModel create(MakeId makeId, String name) {
    if (makeRepository.findById(makeId).isEmpty())
      throw new EntityNotFoundException("Make not found: " + makeId.value());
    if (modelRepository.existsByMakeIdAndName(makeId, name))
      throw new IllegalStateException("Model already exists for make " + makeId.value() + ": " + name);
    return modelRepository.save(CarModel.create(makeId, name));
  }

  public CarModel getById(ModelId id) {
    return modelRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Model not found: " + id.value()));
  }

  public List<CarModel> getByMake(MakeId makeId) {
    return modelRepository.findByMakeId(makeId);
  }

  public CarModel linkRelation(ModelId fromId, ModelId toId,
                               ModelRelation.Type type, String note) {
    var from = getById(fromId);
    getById(toId);
    from.addRelation(toId, type, note);
    return modelRepository.save(from);
  }
}
