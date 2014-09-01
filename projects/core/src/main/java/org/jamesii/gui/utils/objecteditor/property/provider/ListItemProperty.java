/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.provider;

import java.util.List;

import org.jamesii.gui.utils.objecteditor.property.AbstractProperty;

/**
 * Example implementation of a property that represents a list item rather than
 * an actual property.
 * 
 * @author Stefan Rybacki
 * @see ListPropertyProvider
 */
public class ListItemProperty extends AbstractProperty {

  /**
   * The index of the item in the list.
   */
  private int index;

  /**
   * Instantiates a new list item property.
   * 
   * @param name
   *          the name
   * @param index
   *          the item index
   * @param type
   *          the type
   */
  public ListItemProperty(String name, int index, Class<?> type) {
    super(name, type);
    this.index = index;
  }

  @Override
  public Object getValue(Object parent) {
    if (parent instanceof List<?>) {
      return ((List<?>) parent).get(index);
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setValue(Object parent, Object value) {
    if (parent instanceof List<?>
        && (value == null || getType().isAssignableFrom(value.getClass()))) {
      ((List<Object>) parent).set(index, value);
    }

  }

  @Override
  public boolean isWritable() {
    return false;
  }

}
