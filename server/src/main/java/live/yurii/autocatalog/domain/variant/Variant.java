package live.yurii.autocatalog.domain.variant;

import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Variant {

  private VariantId id;
  private BodyId bodyId;
  private PowertrainId powertrainId;
  private TransmissionId transmissionId;
  private Drivetrain drivetrain;
  private Integer groundClearanceMm;
  private int curbWeightKg;
  private Set<String> markets = new LinkedHashSet<>();

  private Variant() {
  }

  public static Variant create(BodyId bodyId, PowertrainId powertrainId,
                               TransmissionId transmissionId,
                               Drivetrain drivetrain, int curbWeightKg) {
    if (curbWeightKg <= 0) throw new IllegalArgumentException("curbWeightKg must be > 0");
    var v = new Variant();
    v.id = VariantId.generate();
    v.bodyId = Objects.requireNonNull(bodyId, "bodyId must not be null");
    v.powertrainId = Objects.requireNonNull(powertrainId, "powertrainId must not be null");
    v.transmissionId = Objects.requireNonNull(transmissionId, "transmissionId must not be null");
    v.drivetrain = Objects.requireNonNull(drivetrain, "drivetrain must not be null");
    v.curbWeightKg = curbWeightKg;
    return v;
  }

  public static Variant reconstitute(VariantId id, BodyId bodyId,
                                     PowertrainId powertrainId,
                                     TransmissionId transmissionId,
                                     Drivetrain drivetrain,
                                     Integer groundClearanceMm,
                                     int curbWeightKg,
                                     Set<String> markets) {
    var v = new Variant();
    v.id = id;
    v.bodyId = bodyId;
    v.powertrainId = powertrainId;
    v.transmissionId = transmissionId;
    v.drivetrain = drivetrain;
    v.groundClearanceMm = groundClearanceMm;
    v.curbWeightKg = curbWeightKg;
    v.markets = new LinkedHashSet<>(markets);
    return v;
  }

  public void addMarket(String market) {
    if (market == null || market.isBlank())
      throw new IllegalArgumentException("market must not be blank");
    markets.add(market.toUpperCase().strip());
  }

  public void removeMarket(String market) {
    if (market == null || market.isBlank())
      throw new IllegalArgumentException("market must not be blank");
    this.markets.remove(market.strip().toUpperCase());
  }

  public void updateCurbWeightKg(int curbWeightKg) {
    if (curbWeightKg <= 0) throw new IllegalArgumentException("curbWeightKg must be > 0");
    this.curbWeightKg = curbWeightKg;
  }

  public void setGroundClearanceMm(int groundClearanceMm) {
    if (groundClearanceMm <= 0)
      throw new IllegalArgumentException("groundClearanceMm must be > 0");
    this.groundClearanceMm = groundClearanceMm;
  }

  public VariantId id() {
    return id;
  }

  public BodyId bodyId() {
    return bodyId;
  }

  public PowertrainId powertrainId() {
    return powertrainId;
  }

  public TransmissionId transmissionId() {
    return transmissionId;
  }

  public Drivetrain drivetrain() {
    return drivetrain;
  }

  public Integer groundClearanceMm() {
    return groundClearanceMm;
  }

  public int curbWeightKg() {
    return curbWeightKg;
  }

  public Set<String> markets() {
    return Collections.unmodifiableSet(markets);
  }
}
