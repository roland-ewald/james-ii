/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.examples.ca.wolframsrules;

import java.awt.Rectangle;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.observe.Observer;
import org.jamesii.gui.utils.ListenerSupport;
import org.jamesii.gui.visualization.grid.IGrid2DModel;
import org.jamesii.gui.visualization.grid.IGridCellListener;

public class Grid1DObserver extends Observer<IEntity> implements IGrid2DModel {

  private static final long serialVersionUID = -2357000465070039713L;

  private final transient ListenerSupport<IGridCellListener> listeners =
      new ListenerSupport<>();

  private RuleGrid grid;

  private int height = 1;

  public Grid1DObserver(RuleGrid grid) {
    this.grid = grid;
  }

  @Override
  public void addGridCellListener(IGridCellListener l) {
    listeners.addListener(l);
  }

  @Override
  public Rectangle getBounds() {
    int[] dims = grid.getDimensions();
    return new Rectangle(0, 0, dims[0], height);
  }

  @Override
  public Object getValueAt(int x, int y) {
    // old values????, i.e., all values < height????
    return ((Boolean) grid.getCell(new int[] { x, 1 }).getState());
  }

  @Override
  public void removeGridCellListener(IGridCellListener l) {
    listeners.removeListener(l);
  }

  @Override
  public void update(IEntity entity) {
    height++;
    for (IGridCellListener l : listeners.getListeners()) {
      l.dataChanged();
    }
  }

}
