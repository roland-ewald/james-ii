package org.jamesii.gui.utils.list.view.item;

import org.jamesii.gui.utils.list.IViewableItem;

/**
 * Interface for properties of items. Each viewable item might have any number
 * of properties (e.g., size, ...).
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IProperty<I extends IViewableItem> {

  /**
   * The name of the property.
   * 
   * @return
   */
  String getName();

  /**
   * The value stored for the property.
   * 
   * @param view
   * @return
   */
  Object getValue(I view);

}
