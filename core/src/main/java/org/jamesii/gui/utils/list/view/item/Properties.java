/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.gui.utils.list.IViewableItem;

/**
 * 
 * @author Jan Himmelspach
 * 
 * @param <I>
 */
public class Properties {

  /**
   * The list of properties which can be retrieved from the item.
   */
  private Map<String, IProperty<? extends IViewableItem>> properties =
      new HashMap<>();

  /**
   * Add a new property to the list of available properties of this item type.
   * 
   * @param property
   */
  protected void registerProperty(IProperty<? extends IViewableItem> property) {
    properties.put(property.getName(), property);
  }

  /**
   * Provide access to the internal list of properties.
   */
  public List<IProperty<? extends IViewableItem>> getProperties() {
    List<IProperty<? extends IViewableItem>> result = new ArrayList<>();
    for (IProperty<?> i : properties.values()) {
      result.add(i);
    }
    return result;
  }

  public Object getValue(String ident, IViewableItem item) {
    IProperty<IViewableItem> prop =
        (IProperty<IViewableItem>) properties.get(ident);
    if (prop != null) {
      return prop.getValue(item);
    }
    return null;
  }

}
