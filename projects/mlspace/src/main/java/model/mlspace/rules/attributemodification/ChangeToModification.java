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
 * Change to given value (not necessarily numeric)
 *
 * @author Arne Bittig
 */
public class ChangeToModification implements IAttributeModification {

  private static final long serialVersionUID = -8850069607307666246L;

  private final String attribute;

  private final Object newValue;

  /**
   * 
   * @param att
   *          Attribute name
   * @param newValue
   *          New attribute value
   */
  public ChangeToModification(String att, Object newValue) {
    this.attribute = att;
    this.newValue = newValue;
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
    ent.setAttribute(attribute, newValue);
    return prevVal;
  }

  @Override
  public String toString() {
    return attribute + ":=" + newValue;
  }
}