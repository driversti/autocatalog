package live.yurii.autocatalog.api.body;

import live.yurii.autocatalog.domain.body.Body;

import java.util.UUID;

public record BodyResponse(
  UUID id,
  UUID generationId,
  String bodyStyle,
  int lengthMm,
  int widthMm,
  int heightMm,
  int wheelbaseMm,
  Integer trunkVolumeLitres,
  Integer groundClearanceMm
) {
  public static BodyResponse from(Body b) {
    return new BodyResponse(
      b.id().value(),
      b.generationId().value(),
      b.bodyStyle().name(),
      b.lengthMm(),
      b.widthMm(),
      b.heightMm(),
      b.wheelbaseMm(),
      b.trunkVolumeLitres(),
      b.groundClearanceMm()
    );
  }
}
