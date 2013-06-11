/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list;

import java.awt.Dimension;
import java.util.List;

import javax.swing.Icon;

import org.jamesii.gui.utils.list.view.item.IProperty;

/**
 * An item which can be displayed in the {@link JItemList}.
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IViewableItem {

  /**
   * Predefined icon size for use in the different views: SMALL.
   */
  Dimension SMALL = new Dimension(15, 15);

  /**
   * Predefined icon size for use in the different views: SMALL.
   */
  Dimension TILES = new Dimension(35, 35);

  /**
   * Predefined icon size for use in the different views: SMALL.
   */
  Dimension LARGE = new Dimension(50, 50);

  /**
   * 
   * @return
   */
  Object getObject();

  /**
   * Get the name / label of the item. E.g., an 8+3 filename.
   * 
   * @return
   */
  String getLabel();

  /**
   * Get the type of the item. E.g., folder, document, .....
   * 
   * @return
   */
  String getType();

  /**
   * Get the "size" of the item. Might be the number of subitems, kilobytes or
   * whatever refers to "size".
   * 
   * @return
   */
  String getSize();

  /**
   * Get the value of the property identified by the ident passed.
   * 
   * @param ident
   * @return
   */
  <V> V getProperty(String ident);

  /**
   * Get the icon, scaled to the dimension passed, to represent this item.
   * 
   * @param dim
   *          , dimension to scale the icon to
   * @return an instance of the icon to be used
   */
  Icon getIcon(Dimension dim);

  /**
   * Get the list of properties supported by this item type.
   * 
   * @return
   */
  List<IProperty<? extends IViewableItem>> getProperties();

}
