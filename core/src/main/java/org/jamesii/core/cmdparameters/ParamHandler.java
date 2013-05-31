/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import java.io.Serializable;

import org.jamesii.SimSystem;

/**
 * A ParamHandler "handles" a certain (command line) parameter.
 * 
 * @author Jan Himmelspach
 */
public abstract class ParamHandler implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = -53337718552914773L;

  /**
   * Value contains the part of the parameter following the equal sign ("="), or
   * null if this part does not exist.
   * 
   * @param value
   *          : value of the parameter or null
   * @param parameters
   *          : object which contains an attribute which will be modified by
   *          this handler
   */
  public abstract void handleParamValue(String value,
      AbstractParameters parameters);

  @Override
  public String toString() {
    return "";
  }

  /**
   * Helper method to set the given field in parameters to the value provided.
   * Will eat all potential reflection exceptions and return true if setting
   * went fine, false otherwise.
   * 
   * @param parameters
   * @param fieldName
   * @param value
   * @return
   */
  protected static boolean set(AbstractParameters parameters, String fieldName,
      Object value) {
    try {
      parameters.getClass().getField(fieldName).set(parameters, value);
      return true;
    } catch (IllegalArgumentException | SecurityException
        | IllegalAccessException | NoSuchFieldException e) {
      SimSystem.report(e);
    }
    return false;
  }

  /**
   * Helper method to set the given field in parameters to the value provided.
   * Will eat all potential reflection exceptions and if such an exception
   * occured the value will be stored in the simple values list of the
   * parameters passed.
   * 
   * @param parameters
   * @param fieldName
   * @param value
   * @return
   */
  protected static void setSimple(AbstractParameters parameters,
      String fieldName, Object value) {
    if (!set(parameters, fieldName, value)) {
      parameters.getSimpleValues().put(fieldName, value);
    }
  }

}