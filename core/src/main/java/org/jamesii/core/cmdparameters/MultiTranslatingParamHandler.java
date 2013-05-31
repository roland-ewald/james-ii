/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import java.util.Map;

/**
 * Sets a field of the passed Parameters object named by fieldName to one of the
 * valud given in the validValues list or, if this is null to the value as such.
 * The type parameter T can be used for defining the type of the replacement
 * which shall be assigned instead of the parameter string to the specified
 * field.
 * 
 * @param <T>
 *          the type of the replacement
 * @author Jan Himmelspach
 */
public class MultiTranslatingParamHandler<T> extends ParamHandler {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1443686241941366304L;

  /** The field name. */
  private String fieldName;

  /** The valid values. */
  private Map<String, T> validValues;

  /**
   * The Constructor.
   * 
   * @param fieldName
   *          the field name
   * @param theValidValues
   *          the the valid values
   */
  public MultiTranslatingParamHandler(String fieldName,
      Map<String, T> theValidValues) {
    super();
    this.fieldName = fieldName;
    validValues = theValidValues;
  }

  @Override
  public void handleParamValue(String value, AbstractParameters parameters) {
    if (validValues != null && !validValues.containsKey(value)) {
      throw new RuntimeException("The value " + value
          + " is not a value for the parameter!!");
    }

    try {
      if (validValues != null) {
        parameters.getClass().getField(fieldName)
            .set(parameters, validValues.get(value));

      } else {
        parameters.getClass().getField(fieldName).set(parameters, value);
      }
    } catch (IllegalArgumentException | SecurityException
        | IllegalAccessException | NoSuchFieldException e) {
      fix(parameters, value);
    }
  }

  private void fix(AbstractParameters parameters, String value) {
    // field not available, store in "simple parameter list"
    if (validValues != null) {
      parameters.getSimpleValues().put(fieldName, validValues.get(value));
    } else {
      parameters.getSimpleValues().put(fieldName, value);
    }
  }

}
