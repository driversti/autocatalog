package live.yurii.autocatalog.domain.body;

import live.yurii.autocatalog.domain.generation.GenerationId;

import java.util.Objects;

public class Body {

  private BodyId id;
  private GenerationId generationId;
  private BodyStyle bodyStyle;
  private int lengthMm;
  private int widthMm;
  private int heightMm;
  private Integer wheelbaseMm;
  private Integer trunkVolumeLitres;
  private Integer groundClearanceMm;

  private Body() {
  }

  public static Body create(GenerationId generationId, BodyStyle bodyStyle,
                            int lengthMm, int widthMm, int heightMm, Integer wheelbaseMm) {
    validateDimensions(lengthMm, widthMm, heightMm, wheelbaseMm);
    var b = new Body();
    b.id = BodyId.generate();
    b.generationId = Objects.requireNonNull(generationId, "generationId must not be null");
    b.bodyStyle = Objects.requireNonNull(bodyStyle, "bodyStyle must not be null");
    b.lengthMm = lengthMm;
    b.widthMm = widthMm;
    b.heightMm = heightMm;
    b.wheelbaseMm = wheelbaseMm;
    return b;
  }

  public static Body reconstitute(BodyId id, GenerationId generationId, BodyStyle bodyStyle,
                                  int lengthMm, int widthMm, int heightMm, Integer wheelbaseMm,
                                  Integer trunkVolumeLitres, Integer groundClearanceMm) {
    var b = new Body();
    b.id = id;
    b.generationId = generationId;
    b.bodyStyle = bodyStyle;
    b.lengthMm = lengthMm;
    b.widthMm = widthMm;
    b.heightMm = heightMm;
    b.wheelbaseMm = wheelbaseMm;
    b.trunkVolumeLitres = trunkVolumeLitres;
    b.groundClearanceMm = groundClearanceMm;
    return b;
  }

  public void updateDimensions(int lengthMm, int widthMm, int heightMm, Integer wheelbaseMm,
                               Integer trunkVolumeLitres, Integer groundClearanceMm) {
    validateDimensions(lengthMm, widthMm, heightMm, wheelbaseMm);
    this.lengthMm = lengthMm;
    this.widthMm = widthMm;
    this.heightMm = heightMm;
    this.wheelbaseMm = wheelbaseMm;
    if (trunkVolumeLitres != null) {
      setTrunkVolumeLitres(trunkVolumeLitres);
    } else {
      this.trunkVolumeLitres = null;
    }
    if (groundClearanceMm != null) {
      setGroundClearanceMm(groundClearanceMm);
    } else {
      this.groundClearanceMm = null;
    }
  }

  public void setTrunkVolumeLitres(int trunkVolumeLitres) {
    if (trunkVolumeLitres <= 0)
      throw new IllegalArgumentException("trunkVolumeLitres must be > 0");
    this.trunkVolumeLitres = trunkVolumeLitres;
  }

  public void setGroundClearanceMm(int groundClearanceMm) {
    if (groundClearanceMm <= 0)
      throw new IllegalArgumentException("groundClearanceMm must be > 0");
    this.groundClearanceMm = groundClearanceMm;
  }

  public BodyId id() {
    return id;
  }

  public GenerationId generationId() {
    return generationId;
  }

  public BodyStyle bodyStyle() {
    return bodyStyle;
  }

  public int lengthMm() {
    return lengthMm;
  }

  public int widthMm() {
    return widthMm;
  }

  public int heightMm() {
    return heightMm;
  }

  public Integer wheelbaseMm() {
    return wheelbaseMm;
  }

  public Integer trunkVolumeLitres() {
    return trunkVolumeLitres;
  }

  public Integer groundClearanceMm() {
    return groundClearanceMm;
  }

  private static void validateDimensions(int length, int width, int height, Integer wheelbase) {
    if (length <= 0) throw new IllegalArgumentException("lengthMm must be > 0");
    if (width <= 0) throw new IllegalArgumentException("widthMm must be > 0");
    if (height <= 0) throw new IllegalArgumentException("heightMm must be > 0");
    if (wheelbase != null && wheelbase <= 0) throw new IllegalArgumentException("wheelbaseMm must be > 0");
  }
}
