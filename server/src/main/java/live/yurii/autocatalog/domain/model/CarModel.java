package live.yurii.autocatalog.domain.model;

import live.yurii.autocatalog.domain.make.MakeId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CarModel {

  private final List<ModelRelation> relations = new ArrayList<>();
  private ModelId id;
  private MakeId makeId;
  private String name;
  private String slug;

  private CarModel() {
  }

  public static CarModel create(MakeId makeId, String name) {
    var m = new CarModel();
    m.id = ModelId.generate();
    m.makeId = Objects.requireNonNull(makeId);
    m.name = requireNonBlank(name, "name");
    m.slug = slugify(name);
    return m;
  }

  public static CarModel reconstitute(ModelId id, MakeId makeId, String name, String slug) {
    var m = new CarModel();
    m.id = id;
    m.makeId = makeId;
    m.name = name;
    m.slug = slug;
    return m;
  }

  public void rename(String newName) {
    this.name = requireNonBlank(newName, "name");
    this.slug = slugify(newName);
  }

  public void addRelation(ModelId targetId, ModelRelation.Type type, String note) {
    if (targetId.equals(this.id))
      throw new IllegalArgumentException("Model cannot relate to itself");
    boolean duplicate = relations.stream()
      .anyMatch(r -> r.targetModelId().equals(targetId) && r.type() == type);
    if (duplicate)
      throw new IllegalStateException("Relation already exists");
    relations.add(new ModelRelation(targetId, type, note));
  }

  public ModelId id() {
    return id;
  }

  public MakeId makeId() {
    return makeId;
  }

  public String name() {
    return name;
  }

  public String slug() {
    return slug;
  }

  public List<ModelRelation> relations() {
    return Collections.unmodifiableList(relations);
  }

  private static String requireNonBlank(String v, String field) {
    if (v == null || v.isBlank())
      throw new IllegalArgumentException(field + " must not be blank");
    return v.strip();
  }

  private static String slugify(String name) {
    return name.strip().toLowerCase().replaceAll("[^a-z0-9]+", "-");
  }
}
