package live.yurii.autocatalog.api.make;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MakeRequest(
  @NotBlank @Size(max = 100) String name,
  @NotBlank @Size(max = 100) String country
) {
}
