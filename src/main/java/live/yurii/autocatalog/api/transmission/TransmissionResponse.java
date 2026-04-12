package live.yurii.autocatalog.api.transmission;

import live.yurii.autocatalog.domain.transmission.Transmission;

import java.util.UUID;

public record TransmissionResponse(UUID id, String type, int gearCount) {

  public static TransmissionResponse from(Transmission t) {
    return new TransmissionResponse(t.id().value(), t.type().name(), t.gearCount());
  }
}
