package live.yurii.autocatalog.domain.body;

import java.util.Objects;
import java.util.UUID;

public record BodyId(UUID value) {
  public BodyId {
    Objects.requireNonNull(value, "BodyId value must not be null");
  }

  public static BodyId generate() {
    return new BodyId(UUID.randomUUID());
  }

  public static BodyId of(String value) {
    return new BodyId(UUID.fromString(value));
  }
}
