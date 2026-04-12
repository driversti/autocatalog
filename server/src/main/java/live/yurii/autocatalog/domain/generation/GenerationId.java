package live.yurii.autocatalog.domain.generation;

import java.util.Objects;
import java.util.UUID;

public record GenerationId(UUID value) {
  public GenerationId {
    Objects.requireNonNull(value, "GenerationId value must not be null");
  }

  public static GenerationId generate() {
    return new GenerationId(UUID.randomUUID());
  }

  public static GenerationId of(String value) {
    return new GenerationId(UUID.fromString(value));
  }
}
