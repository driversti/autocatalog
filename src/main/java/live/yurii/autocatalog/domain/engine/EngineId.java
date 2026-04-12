package live.yurii.autocatalog.domain.engine;

import java.util.Objects;
import java.util.UUID;

public record EngineId(UUID value) {
  public EngineId {
    Objects.requireNonNull(value, "EngineId value must not be null");
  }

  public static EngineId generate() {
    return new EngineId(UUID.randomUUID());
  }

  public static EngineId of(String value) {
    return new EngineId(UUID.fromString(value));
  }
}
