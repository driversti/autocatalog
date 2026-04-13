package live.yurii.autocatalog.api.engine;

import live.yurii.autocatalog.domain.engine.Engine;

public record EngineResponse(
  Long id,
  String code,
  String name,
  String fuelType,
  Integer displacementCc,
  int powerKw,
  int powerHp,
  Integer torqueNm,
  Integer cylinderCount,
  String configuration,
  Integer systemPowerKw
) {
  public static EngineResponse from(Engine engine) {
    return new EngineResponse(
      engine.id() == null ? null : engine.id().value(),
      engine.code(), engine.name(), engine.fuelType().name(),
      engine.displacementCc(), engine.powerKw(), engine.powerHp(),
      engine.torqueNm(), engine.cylinderCount(), engine.configuration(),
      engine.systemPowerKw()
    );
  }
}
