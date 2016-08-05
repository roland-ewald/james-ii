/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules.attributemodification;

import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;

/**
 * Change of numeric attribute value by addition of predefined addend
 *
 * @author Arne Bittig
 */
public class AdditionModification implements IAttributeModification {

  private static final long serialVersionUID = -2720712356935912936L;

  private final String attribute;

  private final double change;

  /**
   * 
   * @param att
   *          Attribute name
   * @param factor
   *          Factor to multiply value of att by
   */
  public AdditionModification(String att, Number factor) {
    this.attribute = att;
    this.change = factor.doubleValue();
  }

  @Override
  public String getAttribute() {
    return attribute;
  }

  @Override
  public Object modifyAttVal(AbstractModelEntity ent, Map<String, Object> env) {
    Object prevVal = ent.getAttribute(attribute);
    if (prevVal == null) {
      throw new IllegalArgumentException("Entity " + ent
          + " does not have attribute " + attribute);
    }
    double val = ((Number) prevVal).doubleValue();
    val += change;
    ent.setAttribute(attribute, val);
    return prevVal;
  }

  @Override
  public String toString() {
    if (change < 0) {
      return attribute + "-=" + -change;
    }
    return attribute + "+=" + change;
  }
}