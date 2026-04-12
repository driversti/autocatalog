package live.yurii.autocatalog.application.make;

import live.yurii.autocatalog.application.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.make.Make;
import live.yurii.autocatalog.domain.make.MakeId;
import live.yurii.autocatalog.domain.make.MakeRepository;

import java.util.List;

public class MakeService {

  private final MakeRepository makeRepository;

  public MakeService(MakeRepository makeRepository) {
    this.makeRepository = makeRepository;
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
}
