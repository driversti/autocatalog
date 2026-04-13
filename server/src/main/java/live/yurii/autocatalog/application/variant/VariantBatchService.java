package live.yurii.autocatalog.application.variant;

import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.variant.Drivetrain;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes a batch of variant creation requests, treating each item independently.
 *
 * <p>A failure in one item never affects the others — partial success is the expected
 * outcome when some inputs are invalid or conflict with existing data.
 */
public class VariantBatchService {

  private final VariantService variantService;

  public VariantBatchService(VariantService variantService) {
    this.variantService = variantService;
  }

  /**
   * Creates variants for all given requests, processing each independently.
   *
   * @param requests list of creation parameters; each entry maps to one variant
   * @return one result per request, in the same order, each marked SUCCESS or FAILURE
   */
  public List<BatchResult> createAll(List<CreateVariantCommand> requests) {
    var results = new ArrayList<BatchResult>(requests.size());
    for (int i = 0; i < requests.size(); i++) {
      var cmd = requests.get(i);
      try {
        var variant = variantService.create(
          cmd.bodyId(),
          cmd.powertrainId(),
          cmd.transmissionId(),
          cmd.drivetrain(),
          cmd.curbWeightKg(),
          cmd.groundClearanceMm()
        );
        results.add(BatchResult.success(i, variant));
      } catch (Exception e) {
        results.add(BatchResult.failure(i, e.getMessage()));
      }
    }
    return results;
  }

  // --- nested types kept package-private to avoid polluting the domain layer ---

  public record CreateVariantCommand(
    BodyId bodyId,
    PowertrainId powertrainId,
    TransmissionId transmissionId,
    Drivetrain drivetrain,
    int curbWeightKg,
    Integer groundClearanceMm
  ) {
  }

  public record BatchResult(
    int index,
    boolean succeeded,
    live.yurii.autocatalog.domain.variant.Variant variant,
    String error
  ) {
    public static BatchResult success(int index, live.yurii.autocatalog.domain.variant.Variant variant) {
      return new BatchResult(index, true, variant, null);
    }

    public static BatchResult failure(int index, String error) {
      return new BatchResult(index, false, null, error);
    }
  }
}
