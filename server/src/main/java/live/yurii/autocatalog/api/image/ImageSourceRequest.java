package live.yurii.autocatalog.api.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ImageSourceRequest(
  @NotBlank String imageUrl,
  String sourceUrl,
  @Size(max = 255) String author,
  @NotBlank @Size(max = 100) String license,
  String licenseUrl,
  @Size(max = 500) String copyright,
  String usageTerms
) {
}
