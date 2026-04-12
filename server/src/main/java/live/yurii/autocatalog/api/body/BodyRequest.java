package live.yurii.autocatalog.api.body;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import live.yurii.autocatalog.domain.body.BodyStyle;

import java.util.UUID;

public record BodyRequest(
  @NotNull UUID generationId,
  @NotNull BodyStyle bodyStyle,
  @Min(1) int lengthMm,
  @Min(1) int widthMm,
  @Min(1) int heightMm,
  @Min(1) int wheelbaseMm,
  @Min(1) Integer trunkVolumeLitres,
  @Min(1) Integer groundClearanceMm
) {
}
