/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.spatial;

/**
 * Attributes of MLSpace entities that are linked to special treatment. In
 * attribute name->value or name->range maps (i.e. Map<String,?>), use
 * {@link SpatialAttribute}.XYZ.toString() in get and put calls.
 *
 * @author Arne Bittig
 *
 */
public enum SpatialAttribute {
  /** shape attribute */
  SHAPE("shape", false, null),
  /**
   * size attribute (area for 2d, volume for 3d; internally part of shape, but
   * externally accessible independently)
   */
  SIZE("size", true, 0.), /** position/location attribute */
  POSITION("position", false, null), /** diffusion constant attribute */
  DIFFUSION("diffusion", true, 0.0),
  /**
   * movement (speed & direction) attribute, can also be specified in parts, see
   * VELOCITY and DIRECTION
   */
  DRIFT("drift", true, null), /** movement speed attribute */
  VELOCITY("velocity", true, null), /** movement direction attribute */
  DIRECTION("direction", true, null);

  /**
   * string representation of attribute in model description (e.g. to allow
   * shorter IDs for model description, e.g. "diff" instead of "diffusion",
   * without changing the enum value
   */
  private final String attrName;

  /** should this attribute be accessible for change by rules? */
  private final boolean accessible;

  private final Object defaultValue;

  // /** string name -> enum value map for faster lookup */
  // private static final Map<String, SpatialAttribute> nameToValueMap =
  // new HashMap<String, SpatialAttribute>();
  //
  // static {
  // for (SpatialAttribute value : EnumSet
  // .allOf(SpatialAttribute.class)) {
  // nameToValueMap.put(value.name, value);
  // }
  // }
  //
  // public static SpatialAttribute forName(String name) {
  // return nameToValueMap.get(name);
  // }

  /**
   * @param attrName
   *          Name/keyword string for the respective attribute
   */
  private SpatialAttribute(String attrName, boolean accessible,
      Object defaultValue) {
    this.attrName = attrName;
    this.accessible = accessible;
    this.defaultValue = defaultValue;
  }

  /**
   * @return true if attribute should be accessible for change by rules
   */
  public final boolean isAccessible() {
    return accessible;
  }

  /**
   * 
   * @return Default value for the respective attribute (null if none)
   */
  public final Object defaultValue() {
    return defaultValue;
  }

  /**
   * @return Name of the attribute
   */
  @Override
  public final String toString() {
    return attrName;
  }

  private static final SpatialAttribute[] COPY_OF_VALUES = values();

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
  public static SpatialAttribute forName(String name) {
    for (SpatialAttribute spA : COPY_OF_VALUES) {
      if (spA.attrName.equalsIgnoreCase(name)) {
        return spA;
      }
    }
    return null;
  }

  /**
   * Workaround for additional angle needed for spherical coordinates (maybe
   * better solution would be typed angles for DIRECTION, but needs more complex
   * implementation)
   */
  public static final String SPERICAL_COORDINATES_ADDITIONAL_ATT_NAME = "phi";
}