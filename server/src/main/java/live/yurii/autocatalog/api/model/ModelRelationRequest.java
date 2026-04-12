package live.yurii.autocatalog.api.model;

import jakarta.validation.constraints.NotNull;
import live.yurii.autocatalog.domain.model.ModelRelation;

import java.util.UUID;

public record ModelRelationRequest(
  @NotNull UUID targetModelId,
  @NotNull ModelRelation.Type type,
  String note
) {
}
