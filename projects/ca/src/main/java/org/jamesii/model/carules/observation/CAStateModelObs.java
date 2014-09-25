/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.observation;

import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jamesii.core.observe.Observer;
import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.utils.ListenerSupport;
import org.jamesii.gui.visualization.grid.Grid2D;
import org.jamesii.gui.visualization.grid.IGrid2DModel;
import org.jamesii.gui.visualization.grid.IGridCellListener;
import org.jamesii.gui.visualization.grid.IGridCellRenderer;
import org.jamesii.model.carules.ICARulesModel;

/**
 * The Class CAStateModelObs.
 * 
 * @author Roland Ewald
 */
public class CAStateModelObs extends Observer<ICARulesModel> implements
    IGrid2DModel {

  /** Serialization ID. */
  private static final long serialVersionUID = -7480172059656189149L;

  /** The listeners. */
  private final transient ListenerSupport<IGridCellListener> listeners =
      new ListenerSupport<>();

  /**
   * The renderer to use.
   */
  private final transient IGridCellRenderer renderer;

  /**
   * @param renderer
   */
  public CAStateModelObs(IGridCellRenderer renderer) {
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

    // System.out.println("About to observe something!");

    if (grid == null) {
      IWindow window =
          new AbstractWindow("CA Visualization", null, Contribution.EDITOR) {

            @Override
            protected JComponent createContent() {
              grid = new Grid2D(CAStateModelObs.this);
              if (renderer != null) {
                grid.setCellRenderer(renderer);
              }
              addGridCellListener(grid);
              return grid;
            }

            @Override
            protected IAction[] generateActions() {
              return null;
            }

          };
      WindowManagerManager.getWindowManager().addWindow(window);
    }

    for (IGridCellListener l : listeners) {
      l.dataChanged();
    }

    // write to file

    // String fN = org.jamesii.core.util.File.getUniqueName(SimSystem
    // .getTempDirectory()
    // + "\\" + "CAStateImage.png");
    //
    // System.out.println(fN);
    //
    // File file = new File(fN);
    // try {
    // ImageIO.write((RenderedImage) grid.toImage(6), "png", file);
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

  }

  @Override
  public Rectangle getBounds() {
    int[] dim = model.getGrid().getSize();

    return new Rectangle(0, 0, dim[0], dim[1]);
  }

  @Override
  public Object getValueAt(int x, int y) {
    return model.getGrid().getState(new int[] { x, y });
  }

}
