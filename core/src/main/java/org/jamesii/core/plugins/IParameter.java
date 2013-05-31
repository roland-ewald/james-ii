/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.io.Serializable;

/**
 * Interface to be implemented by any object representing a factory parameter.
 * 
 * @author Jan Himmelspach
 */
public interface IParameter extends Serializable {

  /**
   * Get description.
   * 
   * @return the description of the parameter
   */
  String getDescription();

  /**
   * Get name.
   * 
   * @return the name of the parameter
   */
  String getName();

  /**
   * Determines whether this parameter is required by the factory or not.
   * 
   * @return true if parameter is required by the factory, otherwise false
   */
  Boolean isRequired();

  /**
   * Get the fully-qualified class name of the parameter's value.
   * 
   * @return FQCN of parameter
   */
  String getType();

  /**
   * Get a string representation of the default value of the parameter.
   * 
   * @return default value as string or null
   */
  String getDefaultValue();

  /**
   * Get the plug-in type of the parameter.
   * 
   * @return FQCN of base factory if parameter refers to plug-in type
   */
  String getPluginType();

  /**
   * Check whether the parameter is associated with a plug-in type.
   * 
   * @return true, if a plug-in type has been set; otherwise false
   */
  boolean hasPluginType();

}
