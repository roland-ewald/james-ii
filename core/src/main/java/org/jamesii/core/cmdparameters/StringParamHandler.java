/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import java.util.List;

/**
 * The Class StringParamHandler.
 * 
 * @author Jan Himmelspach
 */
public class StringParamHandler extends ParamHandler {

  /** Serialization ID. */
  private static final long serialVersionUID = 1745195868842883303L;

  /** The field name. */
  private String fieldName;

  /** The valid values. */
  private List<String> validValues;

  /**
   * The Constructor.
   * 
   * @param fieldName
   *          the field name
   * @param validValues
   *          maybe null => all values are "valid"
   */
  public StringParamHandler(String fieldName, List<String> validValues) {
    super();
    this.fieldName = fieldName;
    this.validValues = validValues;
  }

  @Override
  public void handleParamValue(String value, AbstractParameters parameters) {
    if (validValues != null && !validValues.contains(value)) {
      throw new InvalidParameterException("The value " + value
          + " is not a value for the parameter!! \n\n Valid values are : \n"
          + validValues);
    }

    setSimple(parameters, fieldName, value);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(super.toString());
    if ((validValues != null) && (!validValues.isEmpty())) {
      result.append(" Valid values:");
      for (String s : validValues) {
        result.append("\n  ");
        result.append(s);
      }
    } // else result += "No valid values given!";
    result.append("\n");
    return result.toString();
  }
}