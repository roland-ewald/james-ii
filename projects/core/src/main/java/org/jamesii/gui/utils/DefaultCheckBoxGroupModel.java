/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Class implementing a default {@link ICheckBoxGroupModel}. It provides methods
 * to add and remove items. The generic parameter specifies the type of items
 * added.
 * 
 * @see #addItem(Object)
 * @see #addItem(int, Object)
 * @see #removeItem(int)
 * @see #removeItem(Object)
 * 
 * @author Stefan Rybacki
 * @param <T>
 *          type parameter that specifies the type of the items within the
 *          {@link ICheckBoxGroupModel}
 * 
 */
public class DefaultCheckBoxGroupModel<T> extends AbstractListModel
    implements ICheckBoxGroupModel<T> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7835086631615003776L;

  /**
   * list of items
   */
  private final List<T> items = new ArrayList<>();

  private final List<JLabel> labels = new ArrayList<>();

  /**
   * Creates new empty model
   */
  public DefaultCheckBoxGroupModel() {
    this(new ArrayList<T>());
  }

  /**
   * Creates a new model prepopulating the given list as items
   * 
   * @param data
   */
  public DefaultCheckBoxGroupModel(List<T> data) {
    super();
    setItemsImpl(data);
  }

  /**
   * Use this method if for any reason setItems can't be used.
   * 
   * @param data
   */
  protected final void setItemsImpl(List<T> data) {
    if (data == null) {
      return;
    }
    items.clear();
    items.addAll(data);
    labels.clear();
    for (T i : items) {
      labels.add(new JLabel(String.valueOf(i)));
    }
    fireContentsChanged(this, 0, items.size() - 1);
  }

  /**
   * Sets the items of the model to the list given
   * 
   * @param data
   *          the list of data
   */
  public void setItems(List<T> data) {
    setItemsImpl(data);
  }

  @Override
  public JComponent getComponentAt(int index) {
    return labels.get(index);
  }

  @Override
  public boolean isEditable(int index) {
    return true;
  }

  @Override
  public T getElementAt(int index) {
    return items.get(index);
  }

  @Override
  public T getItemAt(int index) {
    return items.get(index);
  }

  @Override
  public int getSize() {
    return items.size();
  }

  /**
   * Adds the given item to the items list
   * 
   * @param item
   *          the item to add
   */
  public void addItem(T item) {
    addItem(items.size(), item);
  }

  /**
   * Inserts the given item at the given index
   * 
   * @param index
   *          the index the item should be inserted at
   * @param item
   *          the item to insert
   */
  public void addItem(int index, T item) {
    items.add(index, item);
    labels.add(index, new JLabel(String.valueOf(item)));
    fireIntervalAdded(this, index, index);
  }

  /**
   * Removes the item at given index
   * 
   * @param index
   *          the index of the item to remove
   */
  public void removeItem(int index) {
    items.remove(index);
    labels.remove(index);
    fireIntervalRemoved(this, index, index);
  }

  /**
   * Removes the given item
   * 
   * @param item
   *          the item to remove
   */
  public void removeItem(T item) {
    int i = items.indexOf(item);
    if (i >= 0) {
      removeItem(i);
    }
  }

  public void clear() {
    T[] is = (T[]) items.toArray();
    for (T i : is) {
      removeItem(i);
    }
  }
}
