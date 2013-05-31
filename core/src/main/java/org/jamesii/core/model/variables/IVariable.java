/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

/**
 * Interface for variables. Defines how a variable can be accessed.
 * 
 * @param <T>
 *          type of the variable
 * @author Jan Himmelspach
 */
public interface IVariable<T> {

  /**
   * Return the value of the variable.
   * 
   * @return the value
   */
  T getValue();

  /**
   * Set the value of the variable.
   * 
   * @param value
   *          the value to be set
   */
  void setValue(T value);

  /**
   * Gets the name.
   * 
   * @return the name
   */
  String getName();

  /**
   * Sets the name.
   * 
   * @param name
   *          the new name
   */
  void setName(String name);

}
