package live.yurii.autocatalog.domain.powerunit;

/**
 * Discriminator for the type of propulsion unit.
 * Determines which concrete table (engine / electric_motor / fuel_cell_stack)
 * holds the type-specific attributes.
 */
public enum PowerUnitType {
  ICE,
  ELECTRIC,
  FUEL_CELL
}
