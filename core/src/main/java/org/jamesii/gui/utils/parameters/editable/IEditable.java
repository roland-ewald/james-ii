/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable;

import java.util.Map;

/**
 * Basic interface for all editable variables.
 * 
 * Created: 23.05.2004
 * 
 * @param <V>
 *          the type of the variable to be edited
 * 
 * @author Roland Ewald
 */
public interface IEditable<V> extends IConstrainableVariable<V> {

  /**
   * Get map of all attributes.
   * 
   * @return hash table with all attributes (name => attribute)
   */
  Map<String, IEditable<?>> getAllAttributes();

  /**
   * Get attribute.
   * 
   * @param name
   *          name of attribute
   * 
   * @return attribute of this parameter and that name, null if not exists
   */
  IEditable<?> getAttribute(String name);

  /**
   * Returns deep copy of parameter object.
   * 
   * @return deep copy of parameter object
   * 
   * @throws CopyNotSupportedException
   *           if copying is not supported by this editable object
   */
  IEditable<V> getCopy();

  /**
   * Get default value.
   * 
   * @return value of parameter
   */
  V getDefaultValue();

  /**
   * Returns the documentation annotation of this variable or an empty string if
   * none exists.
   * 
   * @return documentation
   */
  String getDocumentation();

  /**
   * Get string value.
   * 
   * @return string value of parameter
   */
  String getStringValue();

  /**
   * Get sub variable.
   * 
   * @return element of this variable, null if not exists.
   */
  IEditable<?> getSubVariable();

  /**
   * Tests whether there is a sub variable.
   * 
   * @return true, if there is a sub variable
   */
  boolean hasSubVariable();

  /**
   * Flag that determines whether parameter has a complex type. Complex types
   * have attributes and/or sub-variables.
   * 
   * @return true, if editable is complex, otherwise false
   */
  boolean isComplex();

  /**
   * Flag to determine whether the editable may be deleted.
   * 
   * @return true, if it is deletable, otherwise false
   */
  boolean isDeletable();

  /**
   * Set attribute of the variable.
   * 
   * @param name
   *          name of attribute
   * @param attribute
   *          attribute variable
   */
  void setAttribute(String name, IEditable<?> attribute);

  /**
   * Set default value of parameter.
   * 
   * @param defaultValue
   *          value to be set
   */
  void setDefaultValue(V defaultValue);

  /**
   * Sets documentation.
   * 
   * @param documentation
   *          documentation
   */
  void setDocumentation(String documentation);

  /**
   * Sets sub-variable of this variable.
   * 
   * @param variable
   *          variable to be set
   */
  void setSubVariable(IEditable<?> variable);
}
