/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

/**
 * The translating parameter handler replaces a given command argument with the
 * value given as key. This value is set to the attribute of the given
 * parameters instance specified by the fieldName attribute. The generic
 * attribute specifies the type of the key value.
 * 
 * @author Jan Himmelspach
 * 
 * @param <T>
 *          type of the value to be used instead of the string value
 */
public class TranslatingParamHandler<T> extends ParamHandler {

  /** Serialization ID. */
  private static final long serialVersionUID = -9012452677514560830L;

  /** The name of the field which shall be set to "key". */
  private String fieldName;

  /** The value which shall be used for setting the attribute. */
  private T key;

  /**
   * Create a new "translating" parameter handler. A TranslatingParameterHandler
   * needs to parameters: The name of the field which shall be modified if the
   * parameter is given and the value which shall be used for reflecting the
   * parameter in the Parameters object
   * 
   * @param fieldName
   *          : name of the field to be modified
   * @param key
   *          : translated value (must be of the same type as the field which is
   *          identified by the fieldName property)
   */
  public TranslatingParamHandler(String fieldName, T key) {
    super();
    this.key = key;
    this.fieldName = fieldName;
  }

  /**
   * Sets the field of the parameters parameter specified by the fieldName
   * attribute to the value of key.
   * 
   * @param value
   *          : will be ignored
   * @param parameters
   *          : the object whose field (fieldName) will be set to key
   */
  @Override
  public void handleParamValue(String value, AbstractParameters parameters) {

    setSimple(parameters, fieldName, value);

  }
}