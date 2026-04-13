package live.yurii.autocatalog.domain.fuelcellstack;

import java.util.List;
import java.util.Optional;

public interface FuelCellStackRepository {
  FuelCellStack save(FuelCellStack stack);

  Optional<FuelCellStack> findById(FuelCellStackId id);

  List<FuelCellStack> findAll();

  void deleteById(FuelCellStackId id);
}
