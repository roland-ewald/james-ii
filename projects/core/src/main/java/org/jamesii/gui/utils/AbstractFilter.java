/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

/**
 * Implements basic functionality of {@link IFilter} and should be the starting
 * point for custom {@link IFilter} implementations.
 * 
 * @author Stefan Rybacki
 * @param <E>
 *          type parameter specifying the type of the filter value
 * 
 */
public abstract class AbstractFilter<E> implements IFilter<E> {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -5886796856719700912L;

  /**
   * support for {@link IFilterChangeListener}s
   */
  private final ListenerSupport<IFilterChangeListener<E>> listenerSupport =
      new ListenerSupport<>();

  /**
   * the filter value
   */
  private E filterValue;

  /**
   * Creates a new abstract tree filter
   */
  public AbstractFilter() {
    this(null);
  }

  /**
   * Creates a new abstract tree filter and sets the filter value
   * 
   * @param filterValue
   *          the filter value to set
   */
  public AbstractFilter(E filterValue) {
    setFilterValue(filterValue);
  }

  @Override
  public final void setFilterValue(E newFilterValue) {
    E old = filterValue;
    this.filterValue = newFilterValue;
    if ((old != null && !old.equals(newFilterValue))
        || (newFilterValue != null && !newFilterValue.equals(old))) {
      fireFilterValueChange(old, filterValue);
    }
  }

  /**
   * Notifies the registered listeners of a filter value change event.
   * 
   * @param oldValue
   *          the old filter value
   * @param newValue
   *          the new filter value
   */
  protected final synchronized void fireFilterValueChange(E oldValue, E newValue) {
    for (IFilterChangeListener<E> l : listenerSupport.getListeners()) {
      if (l != null) {
        l.filterChanged(this, oldValue, newValue);
      }
    }
  }

  @Override
  public final synchronized void addFilterChangeListener(
      IFilterChangeListener<E> listener) {
    listenerSupport.addListener(listener);
  }

  @Override
  public final boolean filtered(Object object) {
    return filteredWithValue(filterValue, object);
  }

  @Override
  public final E getFilterValue() {
    return filterValue;
  }

  @Override
  public final synchronized void removeFilterChangeListener(
      IFilterChangeListener<E> listener) {
    listenerSupport.removeListener(listener);
  }

}
