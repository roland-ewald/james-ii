/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities;

/**
 * Attributes of MLSpace entities that must be defined at species level, i.e.
 * that cannot differ from one entity of the same species to another. (Their
 * values will usually determine the entity class to be created. Hence, the
 * attribute ids may not be present in the entities' attribute map at all and
 * thus cannot be changed and are only relevant to the model reader.)
 *
 * @author Arne Bittig
 * @date 28.06.2012
 */
public enum SpeciesLevelAttribute {
  /** Type of boundary (determines "Compartment or Region") */
  BOUNDARIES("boundary",
      model.mlspace.entities.SpeciesLevelAttribute.HARD_BOUNDS,
      model.mlspace.entities.SpeciesLevelAttribute.SOFT_BOUNDS);

  /** Keyword for hard boundaries */
  public static final String HARD_BOUNDS = "hard";

  /** Keyword for soft boundaries */
  public static final String SOFT_BOUNDS = "soft";

  /** Default boundaries hard? */
  public static final boolean HARD_BOUNDS_DEFAULT = true;

  /**
   * string representation of attribute in model description
   */
  private final String attrName;

  private final String[] validValues;

  /**
   * @param attrName
   *          Name/keyword string for the respective attribute
   */
  private SpeciesLevelAttribute(String attrName, String... values) {
    this.attrName = attrName;
    this.validValues = values;
  }

  /**
   * Check whether given value is valid for this attribute
   * 
   * @param val
   *          value
   * @return true iff value is valid
   */
  public boolean isValidValue(String val) {
    for (String v : validValues) {
      if (v.equalsIgnoreCase(val)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return Name of the attribute
   */
  @Override
  public final String toString() {
    return attrName;
  }

  private static final SpeciesLevelAttribute[] COPY_OF_VALUES = values();

  /**
   * Find special attribute matching given attribute name (ignores case). Use
   * this instead of {@link #valueOf(String)} (which can not be overridden in
   * case one wants to switch to shorted identifying strings but keep the actual
   * Enum values).
   * 
   * @param name
   *          name of some attibute
   * @return matching attribute or null, if none
   */
  public static SpeciesLevelAttribute forName(String name) {
    for (SpeciesLevelAttribute spA : COPY_OF_VALUES) {
      if (spA.attrName.equalsIgnoreCase(name)) {
        return spA;
      }
    }
    return null;
  }
}