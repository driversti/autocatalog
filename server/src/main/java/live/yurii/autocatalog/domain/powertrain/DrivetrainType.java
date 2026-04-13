package live.yurii.autocatalog.domain.powertrain;

/**
 * Describes the overall propulsion strategy of a powertrain.
 *
 * <p>ICE — internal combustion only.
 * MHEV — mild hybrid (48V belt-starter, no EV-only drive mode).
 * HEV — full self-charging hybrid.
 * PHEV — plug-in hybrid.
 * BEV — battery-electric only.
 * FCEV — fuel cell electric (hydrogen).
 */
public enum DrivetrainType {
  ICE,
  MHEV,
  HEV,
  PHEV,
  BEV,
  FCEV
}
