package live.yurii.autocatalog.api.engine;

import live.yurii.autocatalog.domain.engine.Engine;

import java.util.UUID;

public record EngineResponse(
  UUID id,
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
      engine.id().value(), engine.code(), engine.name(), engine.fuelType().name(),
      engine.displacementCc(), engine.powerKw(), engine.powerHp(),
      engine.torqueNm(), engine.cylinderCount(), engine.configuration(),
      engine.systemPowerKw()
    );
  }
}
