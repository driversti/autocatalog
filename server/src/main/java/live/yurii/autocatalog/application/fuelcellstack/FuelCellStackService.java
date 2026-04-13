package live.yurii.autocatalog.application.fuelcellstack;

import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStack;
import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStackId;
import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStackRepository;
import live.yurii.autocatalog.domain.powertrain.PowertrainRepository;
import live.yurii.autocatalog.domain.powerunit.PowerUnitId;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public class FuelCellStackService {

  private final FuelCellStackRepository stackRepository;
  private final PowertrainRepository powertrainRepository;

  public FuelCellStackService(FuelCellStackRepository stackRepository,
                              PowertrainRepository powertrainRepository) {
    this.stackRepository = stackRepository;
    this.powertrainRepository = powertrainRepository;
  }

  public FuelCellStack create(String label, Integer peakPowerKw, BigDecimal hydrogenTankKg) {
    var stack = FuelCellStack.create(label, peakPowerKw, hydrogenTankKg);
    return stackRepository.save(stack);
  }

  public FuelCellStack getById(FuelCellStackId id) {
    return stackRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("FuelCellStack not found: " + id.value()));
  }

  public List<FuelCellStack> getAll() {
    return stackRepository.findAll();
  }

  public void deleteById(FuelCellStackId id) {
    FuelCellStack stack = getById(id);
    if (powertrainRepository.existsByPowerUnitId(new PowerUnitId(stack.id().value())))
      throw new IllegalStateException("Cannot delete fuel cell stack: referenced by powertrain(s)");
    stackRepository.deleteById(id);
  }
}
