package live.yurii.autocatalog.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CarModelRequest(
  @NotNull UUID makeId,
  @NotBlank @Size(max = 100) String name
) {
}
