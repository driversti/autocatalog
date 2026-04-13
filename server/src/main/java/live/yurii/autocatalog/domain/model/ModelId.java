package live.yurii.autocatalog.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ModelId(UUID value) {
  public ModelId {
    Objects.requireNonNull(value, "ModelId value must not be null");
  }

  public static ModelId generate() {
    return new ModelId(UUID.randomUUID());
  }

  public static ModelId of(String value) {
    return new ModelId(UUID.fromString(value));
  }
}
