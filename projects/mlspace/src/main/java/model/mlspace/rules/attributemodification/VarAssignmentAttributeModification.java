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
 * Attribute modification: assign variable value directly
 * 
 * @author Arne Bittig
 * @date 07.04.2014
 */
public class VarAssignmentAttributeModification implements
    IAttributeModification {

  private static final long serialVersionUID = 94810429528813590L;

  private final String attName;

  private final String varName;

  public VarAssignmentAttributeModification(String attName, String varName) {
    this.attName = attName;
    this.varName = varName;
  }

  @Override
  public String getAttribute() {
    return attName;
  }

  @Override
  public Object modifyAttVal(AbstractModelEntity ent, Map<String, Object> env) {
    Object prevVal = ent.getAttribute(attName);
    ent.setAttribute(attName, env.get(varName));
    // CHECK: type safety?
    return prevVal;
  }

}
