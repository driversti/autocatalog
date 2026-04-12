package live.yurii.autocatalog.domain.generation;

import live.yurii.autocatalog.domain.model.ModelId;
import live.yurii.autocatalog.domain.shared.YearRange;

import java.util.Objects;

public class Generation {

  private GenerationId id;
  private ModelId modelId;
  private String name;
  private YearRange years;

  private Generation() {
  }

  public static Generation create(ModelId modelId, String name, YearRange years) {
    var g = new Generation();
    g.id = GenerationId.generate();
    g.modelId = Objects.requireNonNull(modelId);
    g.name = requireNonBlank(name, "name");
    g.years = Objects.requireNonNull(years);
    return g;
  }

  public static Generation reconstitute(GenerationId id, ModelId modelId,
                                        String name, YearRange years) {
    var g = new Generation();
    g.id = id;
    g.modelId = modelId;
    g.name = name;
    g.years = years;
    return g;
  }

  public void close(int year) {
    if (year < years.from())
      throw new IllegalArgumentException("Close year cannot be before start year");
    this.years = new YearRange(years.from(), year);
  }

  public GenerationId id() {
    return id;
  }

  public ModelId modelId() {
    return modelId;
  }

  public String name() {
    return name;
  }

  public YearRange years() {
    return years;
  }

  private static String requireNonBlank(String v, String field) {
    if (v == null || v.isBlank())
      throw new IllegalArgumentException(field + " must not be blank");
    return v.strip();
  }
}
