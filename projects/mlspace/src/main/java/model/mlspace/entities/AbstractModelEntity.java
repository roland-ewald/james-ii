/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import model.mlspace.entities.spatial.SpatialAttribute;

/**
 * General model entities of a certain species ( @see {@link Species}) with
 * (optional) attributes and (optional) contained entities.
 *
 * Creation date 21.02.2011
 *
 * @author Arne Bittig
 */
public abstract class AbstractModelEntity extends AbstractEntity<Object> {

  private static final long serialVersionUID = 7488685565564001140L;

  /**
   * Entity with attributes, without sub-entities
   * 
   * @param spec
   *          Species of this entity
   * @param attributes
   *          Attribute -> values map
   */
  protected AbstractModelEntity(Species spec, Map<String, Object> attributes) {
    super(spec, attributes);
  }

  /**
   * Set/change attribute value
   * 
   * @param name
   *          Attribute name
   * @param value
   *          New attribute value
   * @return Previous attribute value (as in {@link Map#put(Object, Object)})
   */
  public Object setAttribute(String name, Object value) {
    if (value == null) {
      throw new IllegalArgumentException(
          "attribute value must not be null (attribute " + name + ")");
    }
    return getAttributes().put(name, value);
  }

  /**
   * Remove attribute used for temporary information (e.g. to mark incomplete
   * initialization). Mainly for internal logic.
   * 
   * @param name
   *          temporary attribute name
   */
  public final void removeTempAttribute(String name) {
    if (!name.startsWith("_")
        && !name.equals(SpatialAttribute.POSITION.toString())) {
      throw new IllegalArgumentException("No deletion of non-temp attributes: "
          + name);
    }
    getAttributes().remove(name);
  }

  /**
   * @return value of the entity's diffusion attribute
   * @see #getAttribute(String)
   */
  public double getDiffusionConstant() {
    Object diffVal = getAttributes().get(SpatialAttribute.DIFFUSION.toString());
    if (diffVal == null) {
      return 0.;
    }
    return (Double) diffVal;
  }

  /**
   * @return Collection of attribute names
   */
  public Collection<String> getAttributeNames() {
    return Collections.unmodifiableSet(getAttributes().keySet());
  }

  /**
   * @return Hash code value of the attribute map
   */
  public int getAttributesHashCode() {
    return getAttributes().hashCode();
  }

}