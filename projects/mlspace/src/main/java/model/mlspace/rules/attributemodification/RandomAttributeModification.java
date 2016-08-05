/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules.attributemodification;

import java.util.Map;

import org.jamesii.core.math.random.generators.IRandom;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.entities.values.AbstractValueRange;

/**
 * Modification of attribute with value randomly drawn from given range.
 * Contains a reference to a random number generator, which is used on calls to
 * {@link #modifyAttVal(AbstractModelEntity, Map)}.
 * 
 * @author Arne Bittig
 * @date 07.03.2014
 */
public class RandomAttributeModification implements IAttributeModification {

  private static final long serialVersionUID = 7968434615873019753L;

  private final IRandom rand;

  private final String attName;

  private final AbstractValueRange<?> range;

  public RandomAttributeModification(String att,
      AbstractValueRange<?> newValDef, IRandom rand) {
    this.attName = att;
    this.range = newValDef;
    this.rand = rand;
  }

  @Override
  public String getAttribute() {
    return attName;
  }

  @Override
  public Object modifyAttVal(AbstractModelEntity ent, Map<String, Object> env) {
    Object prevVal = ent.getAttribute(attName);
    if (prevVal == null && !attName.equals(
        SpatialAttribute.SPERICAL_COORDINATES_ADDITIONAL_ATT_NAME.toString())) {
      throw new IllegalArgumentException(
          "Entity " + ent + " does not have attribute " + attName);
    }
    ent.setAttribute(attName, range.getRandomValue(rand));
    return prevVal;
  }

  @Override
  public String toString() {
    return attName + ":=rand" + range;
  }

}
