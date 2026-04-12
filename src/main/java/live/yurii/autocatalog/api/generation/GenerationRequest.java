package live.yurii.autocatalog.api.generation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record GenerationRequest(
  @NotNull UUID modelId,
  @NotBlank @Size(max = 50) String name,
  @Min(1886) @Max(2100) int yearFrom,
  Integer yearTo
) {
}
