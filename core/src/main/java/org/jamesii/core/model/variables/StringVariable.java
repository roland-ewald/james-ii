/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

/**
 * Variable of type String See ancestor (BaseVariable) for further details.
 * NOTE: Before using you should consider using a qualitative variable instead!
 * 
 * @author Jan Himmelspach
 */
public class StringVariable extends BaseVariable<String> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 3832380554005276512L;

  /**
   * Instantiates a new string variable.
   */
  public StringVariable() {
    super();
  }

  /**
   * Instantiates a new string variable.
   * 
   * @param varName
   *          the var name
   */
  public StringVariable(String varName) {
    super(varName);
  }

}
