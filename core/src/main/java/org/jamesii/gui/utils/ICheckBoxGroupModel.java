/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import javax.swing.JComponent;
import javax.swing.ListModel;

/**
 * Model interface for a {@link CheckBoxGroup}.
 * 
 * @author Stefan Rybacki
 * @param <T>
 *          the type parameter for the items shown in the {@link CheckBoxGroup}
 * 
 */
public interface ICheckBoxGroupModel<T> extends ListModel {
  /**
   * @param index
   *          the items index
   * @return the text to display for item at given index
   */
  JComponent getComponentAt(int index);

  /**
   * @param index
   *          the items index
   * @return true if item at index should be enabled
   */
  boolean isEditable(int index);

  /**
   * Method to ensure type safety. Returns the item at given index and must be
   * from type T
   * 
   * @param i
   *          items index
   * @return item at index i
   */
  T getItemAt(int i);
}
