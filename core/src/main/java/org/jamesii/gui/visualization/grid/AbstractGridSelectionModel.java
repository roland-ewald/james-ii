/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import org.jamesii.gui.utils.ListenerSupport;

/**
 * Basic abstract implementation of {@link IGridSelectionModel}.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractGridSelectionModel implements IGridSelectionModel {

  /**
   * The listeners.
   */
  private final ListenerSupport<IGridSelectionListener> listeners =
      new ListenerSupport<>();

  @Override
  public void addGridSelectionListener(IGridSelectionListener l) {
    listeners.addListener(l);
  }

  @Override
  public void removeGridSelectionListener(IGridSelectionListener l) {
    listeners.removeListener(l);
  }

  /**
   * Notifies all registered listeners that the selection changed in the
   * specified region.
   * 
   * @param x1
   *          the x coordinate of the upper left corner of the changed area
   * @param y1
   *          the y coordinate of the upper left corner of the changed area
   * @param x2
   *          the x coordinate of the lower right corner of the changed area
   * @param y2
   *          the y coordinate of the lower right corner of the changed area
   */
  protected final synchronized void fireSelectionChanged(int x1, int y1,
      int x2, int y2) {
    for (IGridSelectionListener l : listeners) {
      l.selectionChanged(x1, y1, x2, y2);
    }
  }

}
