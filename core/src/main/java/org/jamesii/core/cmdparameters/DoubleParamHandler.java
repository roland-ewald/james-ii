/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import org.jamesii.SimSystem;

/**
 * The Class DoubleParamHandler.
 * 
 * @author Jan Himmelspach
 */
public class DoubleParamHandler extends ParamHandler {

  /** Serialization ID. */
  private static final long serialVersionUID = -6305332481541604320L;

  /** The field name. */
  private String fieldName;

  /**
   * Instantiates a new double param handler.
   * 
   * @param fieldName
   *          the field name
   */
  public DoubleParamHandler(String fieldName) {
    super();
    this.fieldName = fieldName;
  }

  @Override
  public void handleParamValue(String value, AbstractParameters parameters) {
    try {
      parameters.getClass().getField(fieldName)
          .set(parameters, Double.valueOf(value).doubleValue());
    } catch (Exception e) {
      SimSystem.report(e);
      if (e instanceof NumberFormatException) {
        throw new InvalidParameterException(
            "Wrong parameter value! Please use a valid floating point (double) value!!",
            e);
      }
      // field not available, store in "simple parameter list"
      parameters.getSimpleValues().put(fieldName, value);

    }
  }

}