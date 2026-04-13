package live.yurii.autocatalog.application.make;

import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.make.Make;
import live.yurii.autocatalog.domain.make.MakeId;
import live.yurii.autocatalog.domain.make.MakeRepository;
import live.yurii.autocatalog.domain.model.CarModelRepository;

import java.util.List;

public class MakeService {

  private final MakeRepository makeRepository;
  private final CarModelRepository carModelRepository;

  public MakeService(MakeRepository makeRepository, CarModelRepository carModelRepository) {
    this.makeRepository = makeRepository;
    this.carModelRepository = carModelRepository;
  }

  public Make create(String name, String country) {
    if (makeRepository.existsByName(name))
      throw new IllegalStateException("Make already exists: " + name);
    return makeRepository.save(Make.create(name, country));
  }

  public Make getById(MakeId id) {
    return makeRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Make not found: " + id.value()));
  }

  public Make getBySlug(String slug) {
    return makeRepository.findBySlug(slug)
      .orElseThrow(() -> new EntityNotFoundException("Make not found: " + slug));
  }

  public List<Make> getAll() {
    return makeRepository.findAll();
  }

  public Make rename(MakeId id, String newName) {
    var make = getById(id);
    make.rename(newName);
    return makeRepository.save(make);
  }

  public void deleteById(MakeId id) {
    getById(id);
    if (carModelRepository.existsByMakeId(id))
      throw new IllegalStateException("Cannot delete make: dependent models exist");
    makeRepository.deleteById(id);
  }

  public Make updateCountry(MakeId id, String newCountry) {
    var make = getById(id);
    make.updateCountry(newCountry);
    return makeRepository.save(make);
  }
}
