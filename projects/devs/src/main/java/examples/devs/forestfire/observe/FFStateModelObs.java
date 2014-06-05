/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire.observe;


import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.Observer;
import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.utils.ListenerSupport;
import org.jamesii.gui.visualization.grid.Grid2D;
import org.jamesii.gui.visualization.grid.IGrid2DModel;
import org.jamesii.gui.visualization.grid.IGridCellListener;

import model.devs.ICoupledModel;
import examples.devs.forestfire.ForestFire;
import examples.devs.forestfire.Map;

/**
 * The Class FFStateModelObs.
 * 
 * @author Jan Himmelspach
 */
public class FFStateModelObs extends Observer implements IGrid2DModel {

  /** Serialization ID. */
  private static final long serialVersionUID = -7480172059656189149L;

  /** The listeners. */
  private ListenerSupport<IGridCellListener> listeners =
      new ListenerSupport<>();

  @Override
  public void addGridCellListener(IGridCellListener l) {
    listeners.addListener(l);
  }

  /** The model. */
  ForestFire model = null;

  Map map = null;

  /** The grid. */
  Grid2D grid = null;

  /** The f. */
  JFrame f;

  public FFStateModelObs(IModel model) {
    super();
    this.model = (ForestFire) model;
    map = (Map) ((ICoupledModel) model).getModel("Map");
  }

  // @Override
  // public Rectangle getBounds() {
  // int[] dims = grid.;
  // return new Rectangle(0, 0, dims[0], dims[1]);
  // }

  // @Override
  // public Object getValueAt(int x, int y) {
  // return model.get.getCell(new int[] { x, y }).getState();
  // }

  @Override
  public void removeGridCellListener(IGridCellListener l) {
    listeners.removeListener(l);
  }

  @Override
  public void update(IObservable entity) {

    // System.out.println("About to observe something!" +entity);

    if (grid == null) {
      System.out.println("Creating visualization");
      IWindow window =
          new AbstractWindow("CA Visualization", null, Contribution.EDITOR) {

            @Override
            protected JComponent createContent() {
              grid = new Grid2D(FFStateModelObs.this);
              addGridCellListener(grid);
              return grid;
            }

            @Override
            protected IAction[] generateActions() {
              return null;
            }

          };

      JFrame f = new JFrame();
      f.add(window.getContent());
      f.pack();
      f.show();
      System.out.println("Created visualization");
      // WindowManagerManager.getWindowManager().addWindow(window);
    }

    for (IGridCellListener l : listeners) {
      l.dataChanged();
    }

  }

  @Override
  public Rectangle getBounds() {
    return new Rectangle(0, 0, model.width, model.height);
  }

  @Override
  public Object getValueAt(int x, int y) {
    return map.getState().getMapPos(x, y);
  }

}
