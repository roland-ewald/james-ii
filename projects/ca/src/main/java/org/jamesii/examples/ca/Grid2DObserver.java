/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.examples.ca;

import java.awt.Rectangle;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.observe.Observer;
import org.jamesii.gui.utils.ListenerSupport;
import org.jamesii.gui.visualization.grid.IGrid2DModel;
import org.jamesii.gui.visualization.grid.IGridCellListener;
import org.jamesii.model.ca.grid.ICAGrid;

public class Grid2DObserver extends Observer<IEntity> implements IGrid2DModel {

  private static final long serialVersionUID = -2357000465070039713L;

  private final transient ListenerSupport<IGridCellListener> listeners =
      new ListenerSupport<>();

  private ICAGrid grid;

  public Grid2DObserver(ICAGrid grid) {
    this.grid = grid;
    if (grid.getDimensions().length != 2) {
      throw new IllegalArgumentException(
          "Provided Grid does not have 2 dimensions!");
    }
  }

  @Override
  public void addGridCellListener(IGridCellListener l) {
    listeners.addListener(l);
  }

  @Override
  public Rectangle getBounds() {
    int[] dims = grid.getDimensions();
    return new Rectangle(0, 0, dims[0], dims[1]);
  }

  @Override
  public Object getValueAt(int x, int y) {
    return grid.getCell(new int[] { x, y }).getState();
  }

  @Override
  public void removeGridCellListener(IGridCellListener l) {
    listeners.removeListener(l);
  }

  @Override
  public void update(IEntity entity) {
    for (IGridCellListener l : listeners) {
      l.dataChanged();
    }
  }

}
