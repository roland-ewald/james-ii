/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.observation;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jamesii.core.observe.Observer;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.util.collection.ListenerSupport;
import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.action.ActionIAction;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.experiment.actions.RunSimAction;
import org.jamesii.gui.visualization.grid.Grid2D;
import org.jamesii.gui.visualization.grid.IGrid2DModel;
import org.jamesii.gui.visualization.grid.IGridCellListener;
import org.jamesii.gui.visualization.grid.IGridCellRenderer;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.ICARulesModel;

// TODO: Auto-generated Javadoc
/**
 * The Class CA1DStateModelObs.
 * 
 * Special observer for 1D cellular automatons (Wolfram's rules). This observer
 * stores the history of states, so that these can be redrawn by the
 * {@link org.jamesii.gui.visualization.grid.Grid2D} visualization component.
 * The oldest row is the row with index 0, the youngest the last row in the
 * {@link #stats} list.
 * 
 * @author Jan Himmelspach
 */
public class CA1DStateModelObs extends Observer<ICARulesModel> implements
    IGrid2DModel {

  /** Serialization ID. */
  private static final long serialVersionUID = -7480172059656189149L;

  /**
   * The history of states - here we are interested in all old states as well,
   * and thus we have to store these.
   */
  private List<Object[]> stats = new ArrayList<>();

  /** The run action. */
  private AbstractAction runAction = null;

  /** The listeners. */
  private transient ListenerSupport<IGridCellListener> listeners =
      new ListenerSupport<>();

  /**
   * The renderer to use.
   */
  private transient IGridCellRenderer renderer;

  /**
   * @param renderer
   */
  public CA1DStateModelObs(IGridCellRenderer renderer) {
    this.renderer = renderer;
  }

  @Override
  public void addGridCellListener(IGridCellListener l) {
    listeners.addListener(l);
  }

  /** The model. */
  private transient ICARulesModel model = null;

  /** The grid. */
  private Grid2D grid = null;

  /** The f. */
  private JFrame f;

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
  public void update(ICARulesModel entity) {

    model = (ICARulesModel) entity;

    // grid has not been used so far, thus initialize the window, ...
    if (grid == null) {
      IWindow window =
          new AbstractWindow("CA Visualization", null, Contribution.EDITOR) {

            @Override
            protected JComponent createContent() {
              grid = new Grid2D(CA1DStateModelObs.this);
              if (renderer != null) {
                grid.setCellRenderer(renderer);
              }
              addGridCellListener(grid);
              return grid;
            }

            @Override
            protected IAction[] generateActions() {
              if (runAction != null) {
                return new IAction[] { new ActionIAction(runAction,
                    runAction.toString(), new String[] { "" }, this) };
              }
              return null;
            }

            @Override
            public void windowClosed() {
              if (runAction instanceof RunSimAction) {
                ((IRunnable) ((RunSimAction) runAction).getSimRuntimeInfo()
                    .getComputationTask().getProcessorInfo().getLocal()).stop();
              }
            }

          };
      WindowManagerManager.getWindowManager().addWindow(window);
    }

    // create row object to contain the current row
    Object[] newRow = new Object[model.getGrid().getSize()[0]];

    // add the current row to the list of
    stats.add(newRow);

    // now copy the states of the cells to the current row
    int i = 0;
    for (ICACell<?> cell : model.getGrid().getCellList()) {
      newRow[i] = cell.getState();
      i++;
    }

    // inform any (here in fact only the one grid listener we created above)
    // about the update
    for (IGridCellListener l : listeners) {
      l.dataChanged();
    }
  }

  @Override
  public Rectangle getBounds() {
    int[] dim = model.getGrid().getSize();

    return new Rectangle(0, 0, dim[0], stats.size());
  }

  @Override
  public Object getValueAt(int x, int y) {
    return stats.get(y)[x];
  }

  /**
   * Gets the run action.
   * 
   * @return the run action
   */
  public AbstractAction getRunAction() {
    return runAction;
  }

  /**
   * Sets the run action.
   * 
   * @param runAction
   *          the new run action
   */
  public void setRunAction(AbstractAction runAction) {
    this.runAction = runAction;
  }

}
