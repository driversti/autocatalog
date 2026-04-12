package live.yurii.autocatalog.domain.make;

import java.util.Objects;
import java.util.UUID;

public record MakeId(UUID value) {
  public MakeId {
    Objects.requireNonNull(value, "MakeId value must not be null");
  }

  public static MakeId generate() {
    return new MakeId(UUID.randomUUID());
  }

  public static MakeId of(String value) {
    return new MakeId(UUID.fromString(value));
  }
}
