/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

/**
 * Listener interface for {@link IFilter} implementations where this interface
 * is used to notify implementing listeners whenever a filter value of an
 * {@link IFilter} changes.
 * 
 * @author Stefan Rybacki
 * @param <E>
 *          a type parameter specifying the type of the filter value
 * 
 */
public interface IFilterChangeListener<E> {
  /**
   * Called from an {@link IFilter} whenever its filter value changed.
   * 
   * @param filter
   *          the filter which's filter value changed
   * @param oldValue
   *          the old filter value
   * @param newValue
   *          the new filter value
   */
  void filterChanged(IFilter<E> filter, E oldValue, E newValue);
}
