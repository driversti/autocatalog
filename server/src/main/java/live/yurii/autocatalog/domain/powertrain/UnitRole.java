package live.yurii.autocatalog.domain.powertrain;

/**
 * The role a power unit plays within a powertrain configuration.
 *
 * <p>PRIMARY — the main traction motor or ICE engine.
 * SECONDARY — a supplementary motor (e.g. rear axle motor in AWD BEV, or MHEV e-motor).
 * GENERATOR — provides electrical energy but not direct traction (e.g. fuel cell stack in FCEV).
 */
public enum UnitRole {
  PRIMARY,
  SECONDARY,
  GENERATOR
}
