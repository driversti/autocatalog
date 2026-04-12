package live.yurii.autocatalog.domain.variant;

import java.util.Objects;
import java.util.UUID;

public record VariantId(UUID value) {
  public VariantId {
    Objects.requireNonNull(value, "VariantId value must not be null");
  }

  public static VariantId generate() {
    return new VariantId(UUID.randomUUID());
  }

  public static VariantId of(String value) {
    return new VariantId(UUID.fromString(value));
  }
}
