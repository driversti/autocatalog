package live.yurii.autocatalog.domain.transmission;

import java.util.Objects;
import java.util.UUID;

public record TransmissionId(UUID value) {
  public TransmissionId {
    Objects.requireNonNull(value, "TransmissionId value must not be null");
  }

  public static TransmissionId generate() {
    return new TransmissionId(UUID.randomUUID());
  }

  public static TransmissionId of(String value) {
    return new TransmissionId(UUID.fromString(value));
  }
}
