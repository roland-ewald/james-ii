/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Common base class for AbstractModelEntity (used in NSM simulations and
 * underlying every compartment) and Rule- & InitEntities (with attribute ranges
 * that MLSpaceEntities can match or from which the initializer can choose)
 * 
 * @author Arne Bittig
 * 
 * @param <V>
 *          The attribute value type (actual objects or sets,ranges,intervals)
 */
public abstract class AbstractEntity<V> implements java.io.Serializable,
    IEntity<V> {

  private static final long serialVersionUID = 5004047601396689299L;

  /** The species of this entity */
  private final Species species;

  /** Attributes of each entity (may be empty) */
  private final Map<String, V> attributes;

  /**
   * Constructor that takes species and generic attribute map
   * 
   * @param spec
   * @param attributes
   */
  public AbstractEntity(Species spec, Map<String, V> attributes) {
    this.species = spec;
    this.attributes =
        attributes != null ? attributes : new LinkedHashMap<String, V>();
  }

  /**
   * Protected attribute map getter for field encapsulation
   * 
   * @return the attribute map
   */
  protected Map<String, V> getAttributes() {
    return attributes;
  }

  /**
   * Check whether entity has given attribute
   * 
   * @param name
   *          Name of the attribute
   * @return true if attribute map contains name as key
   */
  @Override
  public boolean hasAttribute(String name) {
    return attributes.containsKey(name);
  }

  /**
   * Get attribute value (as Object, will need casting)
   * 
   * @param name
   *          Attribute name
   * @return Attribute value (null if no attribute of this name)
   */
  @Override
  public V getAttribute(String name) {
    return attributes.get(name);
  }

  /**
   * 
   * @return Species this entity belongs to
   */
  @Override
  public final Species getSpecies() {
    return species;
  }

  /**
   * String representation of entity including spatial attributes (the latter
   * are not included in {@link #toString()})
   * 
   * @return String representation like Spec(att1:a1val,att2:a2val,...)
   */
  @Override
  public String toString() {
    return absEntStr().toString();
  }

  /**
   * Abstract entity string representation. Helper method for
   * {@link #toString()}, returning a {@link StringBuilder} for easier
   * manipulation in subclasses.
   * 
   * @return String representation like Spec(att1:a1val,att2:a2val,...)
   */
  protected final StringBuilder absEntStr() {
    StringBuilder str = new StringBuilder(getSpecies().getName());
    str.append("(");
    appendAttStr(str);
    if (str.charAt(str.length() - 1) == ',') {
      str.deleteCharAt(str.length() - 1);
    }
    str.append(")");
    return str;
  }

  protected void appendAttStr(StringBuilder str) {
    for (Map.Entry<String, V> attE : attributes.entrySet()) {
      String attName = attE.getKey();
      str.append(attName);
      str.append(":");
      if (attName.startsWith("_")) {
        str.append('!');
      } else {
        str.append(attE.getValue());
        str.append(",");
      }
    }
  }

}