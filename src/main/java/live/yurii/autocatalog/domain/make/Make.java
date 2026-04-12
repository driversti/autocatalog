package live.yurii.autocatalog.domain.make;

public class Make {

  private MakeId id;
  private String name;
  private String country;
  private String slug;

  private Make() {
  }

  public static Make create(String name, String country) {
    var make = new Make();
    make.id = MakeId.generate();
    make.name = requireNonBlank(name, "name");
    make.country = requireNonBlank(country, "country");
    make.slug = slugify(name);
    return make;
  }

  public static Make reconstitute(MakeId id, String name, String country, String slug) {
    var make = new Make();
    make.id = id;
    make.name = name;
    make.country = country;
    make.slug = slug;
    return make;
  }

  public void rename(String newName) {
    this.name = requireNonBlank(newName, "name");
    this.slug = slugify(newName);
  }

  public MakeId id() {
    return id;
  }

  public String name() {
    return name;
  }

  public String country() {
    return country;
  }

  public String slug() {
    return slug;
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
