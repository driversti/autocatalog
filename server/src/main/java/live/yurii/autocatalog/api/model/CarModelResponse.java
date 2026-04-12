package live.yurii.autocatalog.api.model;

import live.yurii.autocatalog.domain.model.CarModel;

import java.util.List;
import java.util.UUID;

public record CarModelResponse(
  UUID id,
  UUID makeId,
  String name,
  String slug,
  List<RelationResponse> relations
) {
  public static CarModelResponse from(CarModel model) {
    return new CarModelResponse(
      model.id().value(),
      model.makeId().value(),
      model.name(),
      model.slug(),
      model.relations().stream()
        .map(r -> new RelationResponse(r.targetModelId().value(), r.type().name(), r.note()))
        .toList()
    );
  }

  public record RelationResponse(UUID targetModelId, String type, String note) {
  }
}
