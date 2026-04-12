package live.yurii.autocatalog.api.make;

import live.yurii.autocatalog.domain.make.Make;

import java.util.UUID;

public record MakeResponse(UUID id, String name, String country, String slug) {

  public static MakeResponse from(Make make) {
    return new MakeResponse(make.id().value(), make.name(), make.country(), make.slug());
  }
}
