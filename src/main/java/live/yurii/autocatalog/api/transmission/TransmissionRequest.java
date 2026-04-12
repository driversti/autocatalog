package live.yurii.autocatalog.api.transmission;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import live.yurii.autocatalog.domain.transmission.TransmissionType;

public record TransmissionRequest(
  @NotNull TransmissionType type,
  @Min(1) @Max(12) int gearCount
) {
}
