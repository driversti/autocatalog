package live.yurii.autocatalog.domain.transmission;

import java.util.Objects;

public class Transmission {

  private TransmissionId id;
  private TransmissionType type;
  private int gearCount;

  private Transmission() {
  }

  public static Transmission create(TransmissionType type, int gearCount) {
    if (gearCount < 1 || gearCount > 12)
      throw new IllegalArgumentException("Unexpected gear count: " + gearCount);
    var t = new Transmission();
    t.id = TransmissionId.generate();
    t.type = Objects.requireNonNull(type);
    t.gearCount = gearCount;
    return t;
  }

  public static Transmission reconstitute(TransmissionId id, TransmissionType type, int gearCount) {
    var t = new Transmission();
    t.id = id;
    t.type = type;
    t.gearCount = gearCount;
    return t;
  }

  public TransmissionId id() {
    return id;
  }

  public TransmissionType type() {
    return type;
  }

  public int gearCount() {
    return gearCount;
  }
}
