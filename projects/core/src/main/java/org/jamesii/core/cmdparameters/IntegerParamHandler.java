/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import org.jamesii.SimSystem;

/**
 * The Class IntegerParamHandler.
 * 
 * @author Jan Himmelspach
 */
public class IntegerParamHandler extends ParamHandler {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5902715541498023784L;

  /** The field name. */

  private String fieldName;

  /**
   * Instantiates a new integer param handler.
   * 
   * @param fieldName
   *          the field name
   */
  public IntegerParamHandler(String fieldName) {
    super();
    this.fieldName = fieldName;
  }

  @Override
  public void handleParamValue(String value, AbstractParameters parameters) {

    int i = -1;
    try {
      i = Integer.valueOf(value).intValue();
    } catch (NumberFormatException e) {
      SimSystem.report(e);
      throw new InvalidParameterException(
          "Wrong parameter value! Please use a valid integer value!!", e);
    }

    setSimple(parameters, fieldName, i);
  }
}