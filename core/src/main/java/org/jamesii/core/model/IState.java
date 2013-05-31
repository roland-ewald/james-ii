/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model;

import org.jamesii.core.model.variables.BaseVariable;

/**
 * The basic state interface. Provides methods for variable management,
 * retrieval and setting.
 * 
 * Many models have a state - even if its empty. Whenever a state is needed the
 * class for the state can implement this interface. However, there is no need
 * from the frameworks side to do so.
 * 
 * @author Jan Himmelspach
 */
public interface IState {

  /**
   * Add a variable to a state.
   * 
   * @param ident
   *          the ident by which the variable is accessible
   * @param variable
   *          the instance of the variable as such
   */
  void addVariable(String ident, BaseVariable<?> variable);

  /**
   * Return the internal value of the variable (BaseVariable<T>).
   * 
   * @param <T>
   *          <T> the type of the variable
   * @param ident
   *          the ident of the variable of which we are going to retrieve the
   *          internal value
   * @return the value (internal value) of the variable (of type T)
   */
  <T> T getValue(String ident);

  /**
   * Return the variable. Auto converts to the correct type.
   * 
   * @param <V>
   *          the type of the variable to be returned, must be a descendant of
   *          BaseVariable
   * @param ident
   *          the ident of the variable to be returned
   * @return the value of the variable
   */
  <V extends BaseVariable<?>> V getVariable(String ident);

  /**
   * Remove a variable. Should only be called if a state is changed due to
   * dynamically changing model structures.
   * 
   * @param ident
   *          the ident of the variable to be removed
   */
  void removeVariable(String ident);

  /**
   * Set the internal value of the given variable.
   * 
   * @param <T>
   *          the type of the variable
   * @param ident
   *          the ident of the variable of which we are going to set the
   *          internal value
   * @param value
   *          the value to be set
   */
  <T> void setValue(String ident, T value);
}
