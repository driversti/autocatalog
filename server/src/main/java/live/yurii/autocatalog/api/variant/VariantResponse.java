package live.yurii.autocatalog.api.variant;

import live.yurii.autocatalog.domain.variant.Variant;

import java.util.Set;
import java.util.UUID;

public record VariantResponse(
  UUID id,
  UUID bodyId,
  UUID powertrainId,
  UUID transmissionId,
  String drivetrain,
  Integer groundClearanceMm,
  int curbWeightKg,
  Set<String> markets
) {
  public static VariantResponse from(Variant v) {
    return new VariantResponse(
      v.id().value(),
      v.bodyId().value(),
      v.powertrainId().value(),
      v.transmissionId().value(),
      v.drivetrain().name(),
      v.groundClearanceMm(),
      v.curbWeightKg(),
      v.markets()
    );
  }
}
