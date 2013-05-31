/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.jamesii.gui.base.IPropertyChangeSupport;

/**
 * Abstract combo box model that provides basic functionality for other combo
 * box models
 * 
 * @author Stefan Rybacki
 * @param <T>
 *          type parameter specifying of which type the items in the model are
 * 
 */
public abstract class AbstractComboBoxModel<T> extends AbstractListModel
    implements ComboBoxModel, IPropertyChangeSupport {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4262766175366939837L;

  /**
   * currently selected item
   */
  private T selectedItem = null;

  /**
   * available items
   */
  private final List<T> items = new ArrayList<>();

  /**
   * change support
   */
  private final PropertyChangeSupport changeSupport =
      new PropertyChangeSupport(this);

  /**
   * Helper function that adds an item to the list of items while notifying the
   * list data listeners.
   * 
   * @param item
   *          the item to add
   */
  public final void addElement(T item) {
    items.add(item);
    fireIntervalAdded(this, items.size() - 2, items.size() - 1);
  }

  public final void removeElement(T item) {
    int index0 = items.indexOf(item);
    if (items.remove(item)) {
      fireIntervalRemoved(this, index0, index0);
    }
  }

  @Override
  public final Object getSelectedItem() {
    return selectedItem;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final void setSelectedItem(Object anItem) {
    if (!items.contains(anItem)) {
      return;
    }

    Object old = selectedItem;
    selectedItem = (T) anItem;

    changeSupport.firePropertyChange("selectedItem", old, anItem);
    fireContentsChanged(this, -1, -1);
  }

  @Override
  public final Object getElementAt(int index) {
    if (index == -1) {
      return null;
    }

    return items.get(index);
  }

  @Override
  public final int getSize() {
    return items.size();
  }

  @Override
  public final void addPropertyChangeListener(PropertyChangeListener l) {
    changeSupport.addPropertyChangeListener(l);
  }

  @Override
  public final void removePropertyChangeListener(PropertyChangeListener l) {
    changeSupport.addPropertyChangeListener(l);
  }

}
