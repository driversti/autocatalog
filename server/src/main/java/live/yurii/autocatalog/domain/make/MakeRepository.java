package live.yurii.autocatalog.domain.make;

import java.util.List;
import java.util.Optional;

public interface MakeRepository {
  Make save(Make make);

  Optional<Make> findById(MakeId id);

  Optional<Make> findBySlug(String slug);

  List<Make> findAll();

  boolean existsByName(String name);

  void deleteById(MakeId id);
}
