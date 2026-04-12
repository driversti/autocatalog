package live.yurii.autocatalog.api.variant;

import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.variant.Variant;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record VariantResponse(
  UUID id,
  UUID bodyId,
  Set<UUID> engineIds,
  UUID transmissionId,
  String drivetrain,
  Integer groundClearanceMm,
  int curbWeightKg,
  Integer systemPowerKw,
  Set<String> markets
) {
  public static VariantResponse from(Variant v) {
    return new VariantResponse(
      v.id().value(),
      v.bodyId().value(),
      v.engineIds().stream().map(EngineId::value).collect(Collectors.toSet()),
      v.transmissionId().value(),
      v.drivetrain().name(),
      v.groundClearanceMm(),
      v.curbWeightKg(),
      v.systemPowerKw(),
      v.markets()
    );
  }
}
