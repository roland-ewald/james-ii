/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// TODO: Auto-generated Javadoc
/**
 * Defines a {@link Grid2D} which has scroll bars to let users scroll who do not
 * have a scroll wheel or a middle mouse button.
 * <p>
 * FIXME: [jr252] When dragging the grid with middle mouse button, speed of
 * dragging seems dependent on direction ... no idea why, as of yet.
 * 
 * @author Johannes Rössel
 */
public class ScrollingGrid2D extends JPanel implements PropertyChangeListener {

  /** Serial version UID. */
  private static final long serialVersionUID = -5156229716854394071L;

  /**
   * How finely grained the scrollbars should be, as scrollbars only have
   * integer values.
   */
  private static final int SCROLL_SENSITIVITY = 100;

  /** The grid. */
  private Grid2D grid;

  /** The model. */
  private IGrid2DModel model;

  /** The vertical scrollbar. */
  private JScrollBar verticalScrollbar;

  /** The horizontal scrollbar. */
  private JScrollBar horizontalScrollbar;

  /** The zoom slider. */
  private JSlider zoomSlider;

  /**
   * Flag when this control is updating. In that case, no events from the grid
   * should be processed.
   */
  private boolean updating;

  /**
   * Instantiates a new scrolling grid2 d.
   * 
   * @param model
   *          the model
   */
  public ScrollingGrid2D(IGrid2DModel model) {
    this.model = model;
    setGrid(new Grid2D(model));
    grid.addPropertyChangeListener(this);
    grid.setAnimationsEnabled(false);
    initializeComponents();
  }

  /**
   * Initialize components.
   */
  private void initializeComponents() {
    this.setLayout(new GridBagLayout());
    GridBagConstraints c;

    // the Grid2D
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    this.add(getGrid(), c);

    // vertical scroll bar
    c = new GridBagConstraints();
    verticalScrollbar = new JScrollBar(Adjustable.VERTICAL);
    verticalScrollbar.addAdjustmentListener(new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {
        updating = true;
        grid.setOffsetY(e.getValue() / (double) SCROLL_SENSITIVITY);
        updating = false;
      }
    });
    c.gridx = 2;
    c.gridy = 0;
    c.weightx = 0;
    c.weighty = 1;
    c.fill = GridBagConstraints.VERTICAL;
    this.add(verticalScrollbar, c);

    // horizontal scroll bar
    c = new GridBagConstraints();
    horizontalScrollbar = new JScrollBar(Adjustable.HORIZONTAL);
    horizontalScrollbar.addAdjustmentListener(new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {
        updating = true;
        grid.setOffsetX(e.getValue() / (double) SCROLL_SENSITIVITY);
        updating = false;
      }
    });
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    c.weighty = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    this.add(horizontalScrollbar, c);

    // center button
    c = new GridBagConstraints();
    JPanel centerButton = new JPanel();
    centerButton.setToolTipText("Center visible grid");
    centerButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        updating = true;
        grid.center();
        updating = false;
        updateScrollBars();
      }
    });
    c.gridx = 2;
    c.gridy = 1;
    c.weightx = 0;
    c.weighty = 0;
    c.fill = GridBagConstraints.BOTH;
    this.add(centerButton, c);

    // zoom slider
    c = new GridBagConstraints();
    zoomSlider = new JSlider(SwingConstants.VERTICAL, 1, 50, 10);
    zoomSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updating = true;
        grid.zoomTo(grid.getWidth() / 2, grid.getHeight() / 2,
            ((JSlider) e.getSource()).getValue());
        updating = false;
      }
    });
    c.gridx = 0;
    c.gridy = 0;
    c.gridheight = 2;
    c.fill = GridBagConstraints.VERTICAL;
    this.add(zoomSlider, c);

    grid.center();
    updateScrollBars();

    grid.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent arg0) {
        updateScrollBars();
      }
    });
  }

  /**
   * Update scroll bars.
   */
  private void updateScrollBars() {
    Rectangle b = model.getBounds();

    if (b.width * grid.getZoom() > grid.getWidth()) {
      horizontalScrollbar.setMaximum(-b.x * SCROLL_SENSITIVITY);
      verticalScrollbar.setMaximum(-b.y * SCROLL_SENSITIVITY);

      horizontalScrollbar.setMinimum((int) ((grid.getWidth() - (b.width + b.x)
          * grid.getZoom())
          / grid.getZoom() * SCROLL_SENSITIVITY));
      verticalScrollbar.setMinimum((int) ((grid.getHeight() - (b.height + b.y)
          * grid.getZoom())
          / grid.getZoom() * SCROLL_SENSITIVITY));
    } else {
      horizontalScrollbar.setMinimum(-b.x * SCROLL_SENSITIVITY);
      verticalScrollbar.setMinimum(-b.y * SCROLL_SENSITIVITY);

      horizontalScrollbar.setMaximum((int) ((grid.getWidth() - (b.width + b.x)
          * grid.getZoom())
          / grid.getZoom() * SCROLL_SENSITIVITY));
      verticalScrollbar.setMaximum((int) ((grid.getHeight() - (b.height + b.y)
          * grid.getZoom())
          / grid.getZoom() * SCROLL_SENSITIVITY));
    }
  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    ScrollingGrid2D g = new ScrollingGrid2D(new AbstractGrid2DModel() {

      private int[][] data = new int[12][12];

      {
        Random r = new Random();
        for (int i = -2; i < 10; i++) {
          for (int j = -2; j < 10; j++) {
            data[i + 2][j + 2] = r.nextInt(20);
          }
        }
      }

      @Override
      public Rectangle getBounds() {
        return new Rectangle(-2, -2, 12, 12);
      }

      @Override
      public Object getValueAt(int x, int y) {
        return data[x + 2][y + 2];
      }

    });
    JFrame f = new JFrame();

    f.setPreferredSize(new Dimension(300, 300));
    f.setSize(f.getPreferredSize());
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setLayout(new BorderLayout());
    f.add(g, BorderLayout.CENTER);
    f.setVisible(true);

  }

  /**
   * Sets the grid.
   * 
   * @param grid
   *          the new grid
   */
  public void setGrid(Grid2D grid) {
    this.grid = grid;
  }

  /**
   * Gets the grid.
   * 
   * @return the grid
   */
  public Grid2D getGrid() {
    return grid;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // possible changes:
    // model, cellrenderer, offsetx, offsety, zoom
    if (!updating) {
      switch (evt.getPropertyName()) {
      case "model":
        this.model = (IGrid2DModel) evt.getNewValue();
        break;
      case "offsetx":
        horizontalScrollbar.setValue((int) (((Double) evt.getNewValue())
            .doubleValue() * SCROLL_SENSITIVITY));
        break;
      case "offsety":
        verticalScrollbar.setValue((int) (((Double) evt.getNewValue())
            .doubleValue() * SCROLL_SENSITIVITY));
        break;
      case "zoom":
        updateScrollBars();
        zoomSlider.setValue(((Double) evt.getNewValue()).intValue());
        break;
      }
    }
  }
}
