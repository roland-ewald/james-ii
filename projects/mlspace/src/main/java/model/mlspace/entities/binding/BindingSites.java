/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.binding;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Arne Bittig
 * @date 18.07.2012
 */
public class BindingSites implements Serializable {

  private static final long serialVersionUID = 8405883277112901025L;

  private final Map<String, Double> relAngles;

  /**
   * @param relAngles
   */
  BindingSites(Map<String, Double> relAngles) {
    this.relAngles = relAngles;
  }

  /**
   * Check whether binding site of given name is defined
   * 
   * @param siteName
   *          Binding site name
   * @return true if there is a site of this name
   */
  public boolean contains(String siteName) {
    return relAngles.containsKey(siteName);
  }

  public boolean isFixedAngleBindingSite(String name) {
    if (!relAngles.containsKey(name)) {
      throw new IllegalArgumentException(
          name + " is not a valid binding site name in " + this);
    }
    return relAngles.get(name) != null;
  }

  /**
   * Angle between two sites (order dependent!)
   * 
   * @param site1
   *          Name of first binding site
   * @param site2
   *          Name of second binding site
   * @return Angle between the two sites
   * @throws NullPointerException
   *           if either site is not defined
   */
  public double getRelativeAngle(String site1, String site2) {
    return relAngles.get(site2) - relAngles.get(site1);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('<');
    boolean first = true;
    for (Map.Entry<String, Double> e : relAngles.entrySet()) {
      if (!first) {
        sb.append(',');
      } else {
        first = false;
      }
      sb.append(e.getKey());
      sb.append(':');
      sb.append(Math.toDegrees(e.getValue()));
      sb.append('\u00B0');
    }
    sb.append('>');
    return sb.toString();
  }

  /**
   * Builder class for {@link BindingSites} in lieu of a public constructor
   * 
   * @author Arne Bittig
   * @date 19.07.2012
   */
  public static class Builder {

    private final Map<String, Double> relAngles = new LinkedHashMap<>();

    /**
     * Add binding site
     * 
     * @param name
     *          Name of site
     * @param relAngle
     *          Angle relative to entity's default orientation (and thus,
     *          relative to previously defined binding sites)
     * @return the builder object itself
     */
    public Builder addSite(String name, Double relAngle) {
      relAngles.put(name, relAngle);
      return this;
    }

    /**
     * Finish building
     * 
     * @return Binding sites definition
     */
    public BindingSites build() {
      return new BindingSites(relAngles);
    }

  }

  // @Override
  // public Iterator<String> iterator() {
  // return relAngles.keySet().iterator();
  // }
}
