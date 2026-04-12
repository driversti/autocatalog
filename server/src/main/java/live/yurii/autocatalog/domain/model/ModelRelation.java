package live.yurii.autocatalog.domain.model;

import java.util.Objects;

public class ModelRelation {

  private final ModelId targetModelId;
  private final Type type;
  private final String note;

  public ModelRelation(ModelId targetModelId, Type type, String note) {
    this.targetModelId = Objects.requireNonNull(targetModelId);
    this.type = Objects.requireNonNull(type);
    this.note = note;
  }

  public ModelId targetModelId() {
    return targetModelId;
  }

  public Type type() {
    return type;
  }

  public String note() {
    return note;
  }

  public enum Type {SUCCESSOR, PREDECESSOR, REBADGE, PLATFORM_SIBLING}
}
