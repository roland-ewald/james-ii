/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

/**
 * The listener interface for receiving IGridSelection events. The class that is
 * interested in processing a IGridSelection event implements this interface,
 * and the object created with that class is registered with a component using
 * the component's <code>addIGridSelectionListener<code> method. When
 * the IGridSelection event occurs, that object's appropriate
 * method is invoked.
 * 
 */
public interface IGridSelectionListener {

  /**
   * Selection changed.
   * 
   * @param x1
   *          the x1
   * @param y1
   *          the y1
   * @param x2
   *          the x2
   * @param y2
   *          the y2
   */
  void selectionChanged(int x1, int y1, int x2, int y2);

}
