/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * This class filters the elements of a {@link ListModel} and shows only the
 * filtered elements to the listener objects.
 * 
 * @author Enrico Seib
 * @param <E>
 *          type parameter specifying the filter value type
 */
public class FilteredListModel<E> extends AbstractListModel implements
    ListDataListener, IFilterChangeListener<E> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 3833606118620347744L;

  /**
   * The filter to use
   */
  private IFilter<E> filter;

  /**
   * The model to wrap
   */
  private ListModel model = null;

  /**
   * Internal list of items
   */
  private List<Object> itemList = new ArrayList<>();

  /**
   * Creates a new instance of a filterable {@link ListModel}
   * 
   * @param wrappedModel
   *          the model to filter
   */
  public FilteredListModel(ListModel wrappedModel) {
    this(wrappedModel, null);
  }

  /**
   * Creates a new instance of a filterable comboBox model wrapping the
   * specified model using the given {@link IFilter}.
   * 
   * @param wrappedModel
   *          the model to filter
   * @param filter
   *          the filter to use
   */
  public FilteredListModel(ListModel wrappedModel, IFilter<E> filter) {
    super();
    model = wrappedModel;
    model.addListDataListener(this);
    setFilter(filter);
  }

  /**
   * Returns element at position index, if a filter is applied, from internal
   * base model or wrapped model.
   * 
   * @param index
   * 
   * @return item at position index
   */
  @Override
  public final Object getElementAt(int index) {
    if (filter == null) {
      return model.getElementAt(index);
    }

    return itemList.get(index);
  }

  /**
   * Returns the size of the base model or, if a filter is applied, from the
   * wrapped model.
   * 
   * @return size of the item list
   */
  @Override
  public final int getSize() {
    if (filter == null) {
      return model.getSize();
    }

    return itemList.size();

  }

  /**
   * Sets the filter.
   * 
   * @param filter
   *          new filter to apply
   */
  public void setFilter(IFilter<E> filter) {
    if (filter == null) {
      return;
    }
    if (this.filter != null) {
      this.filter.removeFilterChangeListener(this);
    }
    this.filter = filter;
    this.filter.addFilterChangeListener(this);

    // for HistoryTextFieldTest
    // if (filter != null && model != null && model.getItems() != null
    // && model.getSize() > 0) {
    // sufficient for JAMES GUI

    applyFilter();
    fireContentsChanged(this, 0, getSize());
  }

  /**
   * Gets the currently used filter
   * 
   * @return the currently used filter
   */
  public IFilter<E> getFilter() {
    return filter;
  }

  /**
   * Applies the filter
   */
  private void applyFilter() {

    if (filter != null) {

      this.itemList.clear();

      for (int i = 0; i < model.getSize(); i++) {
        Object obj = model.getElementAt(i);
        if (!filter.filtered(obj)) {
          this.itemList.add(obj);
        }
      }
    }
  }

  @Override
  public void contentsChanged(ListDataEvent e) {
    applyFilter();
    fireContentsChanged(this, 0, itemList.size());
  }

  @Override
  public void intervalAdded(ListDataEvent e) {
    applyFilter();
    fireContentsChanged(this, 0, itemList.size());
  }

  @Override
  public void intervalRemoved(ListDataEvent e) {
    applyFilter();
    fireContentsChanged(this, 0, itemList.size());
  }

  @Override
  public void filterChanged(IFilter<E> filtr, E oldValue, E newValue) {
    if (filtr.equals(this.filter)) {
      applyFilter();
      fireContentsChanged(this, 0, itemList.size());
    }
  }

}