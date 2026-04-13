package live.yurii.autocatalog.domain.powertrain;

import java.util.Objects;
import java.util.UUID;

public record PowertrainId(UUID value) {
  public PowertrainId {
    Objects.requireNonNull(value, "PowertrainId value must not be null");
  }

  public static PowertrainId generate() {
    return new PowertrainId(UUID.randomUUID());
  }

  public static PowertrainId of(String value) {
    return new PowertrainId(UUID.fromString(value));
  }
}
