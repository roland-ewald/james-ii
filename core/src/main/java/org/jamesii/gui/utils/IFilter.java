/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.io.Serializable;

/**
 * Simple interface that can be used to define filters.
 * 
 * @author Stefan Rybacki
 * @param <E>
 *          type parameter specifying the filter value's type
 * 
 */
public interface IFilter<E> extends Serializable {
  /**
   * Determines whether the given object is going to be filtered or not.
   * 
   * @param object
   *          the object to check
   * @return true if object should be filtered out, false else
   */
  boolean filtered(Object object);

  /**
   * Determines whether the given object is going to be filtered or not.
   * 
   * @param value
   *          the filter value
   * @param object
   *          the object to check
   * @return true if object should be filtered out using the given value, false
   *         else
   */
  boolean filteredWithValue(E value, Object object);

  /**
   * Adds a listener to filter value changes
   * 
   * @param listener
   *          the listener to add
   */
  void addFilterChangeListener(IFilterChangeListener<E> listener);

  /**
   * Removes a previously registered listener
   * 
   * @param listener
   *          the listener to remove
   */
  void removeFilterChangeListener(IFilterChangeListener<E> listener);

  /**
   * @return the filter value
   */
  E getFilterValue();

  /**
   * Sets the filter value to a new filter value
   * 
   * @param value
   *          the new filter value
   */
  void setFilterValue(E value);
}
