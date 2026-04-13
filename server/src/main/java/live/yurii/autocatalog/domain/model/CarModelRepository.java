package live.yurii.autocatalog.domain.model;

import live.yurii.autocatalog.domain.make.MakeId;

import java.util.List;
import java.util.Optional;

public interface CarModelRepository {
  CarModel save(CarModel model);

  Optional<CarModel> findById(ModelId id);

  Optional<CarModel> findBySlug(String slug);

  List<CarModel> findByMakeId(MakeId makeId);

  boolean existsByMakeIdAndName(MakeId makeId, String name);

  boolean existsByMakeId(MakeId makeId);

  void deleteById(ModelId id);
}
