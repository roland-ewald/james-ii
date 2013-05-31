/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jamesii.core.util.misc.Colors;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.animator.IAnimatorListener;
import org.jamesii.gui.utils.animator.LinearInterpolator;
import org.jamesii.gui.utils.animator.SplineInterpolator;
import org.jamesii.gui.utils.animator.SwingAnimator;

/**
 * A component displaying a visual two-dimensional grid of square grid cells.
 * Underlying is a {@link IGrid2DModel} which contains the data to be displayed.
 * A default cell renderer is provided if none is set.
 * <p>
 * This component enables the user to zoom and pan the displayed grid. Other
 * methods for changing the visible segment are provided.
 * <p>
 * By default this grid will fit its used contents into the view on first
 * display. Setting any of zoom or offset will override this.
 * 
 * @author Johannes Rössel
 */
public class Grid2D extends JComponent implements IGridCellListener,
    IGridSelectionListener, IRenderingChangedListener, MouseWheelListener,
    MouseMotionListener, MouseListener {

  private final class IAnimatorListenerImplementation implements
      IAnimatorListener {
    private final double deltaOffsetX;

    private final double deltaOffsetY;

    private double oldX, oldY, newX, newY;

    private IAnimatorListenerImplementation(double deltaOffsetX,
        double deltaOffsetY) {
      this.deltaOffsetX = deltaOffsetX;
      this.deltaOffsetY = deltaOffsetY;
    }

    @Override
    public void animate(double frac) {
      setOffsetX(oldX + (newX - oldX) * frac);
      setOffsetY(oldY + (newY - oldY) * frac);
    }

    @Override
    public void started() {
      oldX = getOffsetX();
      oldY = getOffsetY();
      newX = oldX + deltaOffsetX;
      newY = oldY + deltaOffsetY;
    }

    @Override
    public void stopped() {
      setOffsetX(newX);
      setOffsetY(newY);
    }
  }

  /** Serial version UID. */
  private static final long serialVersionUID = -1286871691450250780L;

  /** The zoom factor. Nothing less than 1 at the moment. */
  private double zoom;

  /** Minimum zoom factor, Can't zoom out further than this. */
  private double minZoom;

  /** Maximum zoom factor. Can't zoom in further than this. */
  private double maxZoom;

  /** Offset from the upper left corner in grid cells (X-coord). */
  private double offsetX;

  /** Offset from the upper left corner in grid cells (Y-coord). */
  private double offsetY;

  /** The data model for this grid. */
  private IGrid2DModel model;

  /** The selection model for this grid. */
  private transient IGridSelectionModel selectionModel =
      new DefaultGridSelectionModel(DefaultGridSelectionModel.NO_SELECTION);

  /** The cell renderer that displays the cells in the grid. */
  private transient IGridCellRenderer cellRenderer;

  /**
   * The cell the mouse pointer currently hovers over. Is {@code null} if none.
   */
  private Point hoverCell;

  /** Whether grid lines are shown or not. */
  private boolean linesVisible;

  /** Whether animations are enabled */
  private boolean animationsEnabled;

  /** Whether to allow panning of the content. */
  private boolean panningAllowed;

  /** Whether to allow zooming of content. */
  private boolean zoomingAllowed;

  /** Whether the grid enables hover effects by default. */
  private boolean hoverAllowed;

  /**
   * A flag whether the grid should be centered on first invocation of
   * {@link #paintComponent(Graphics)}.
   */
  private boolean shouldCenterOnFirstPaint;

  /**
   * An animator for animation of some actions, like centering the used cell
   * range.
   */
  private transient SwingAnimator ani;

  /** The origin point of the drag. */
  private Point dragOrigin;

  /**
   * The selection color.
   */
  private Color selectionColor;

  /**
   * The focus color.
   */
  private Color focusColor;

  /** Registered {@link PropertyChangeListener}s. */
  private PropertyChangeSupport pcListeners = new PropertyChangeSupport(this);

  /**
   * A listener used (and re-used) for animations within the grid. Needs to be
   * an instance variable so other methods can stop an animation if desired and
   * subsequently start a new one for example.
   */
  private transient IAnimatorListener listener;

  /**
   * Flag indicating whether panning is currently on
   */
  private boolean isPanning = false;

  /**
   * Constructs a new {@code Grid2D} with the given {@link IGrid2DModel}. A
   * default grid cell renderer will be automatically provided.
   * <p>
   * Furthermore there will be default colors for background (black) and grid
   * lines (foreground, light gray). Grid lines, hover and animations are
   * enabled by default as well as panning and zooming.
   * <p>
   * Zooming is allowed up to a minimum zoom factor of 1 and is unbounded
   * upwards. This should be changed for individual uses of this control as both
   * defaults are probably not suitable for many applications. However, finding
   * sensible default values that satisfy most uses is equally hard.
   * 
   * @param model
   *          The model to use.
   */
  public Grid2D(final IGrid2DModel model) {
    super();
    this.model = model;
    model.addGridCellListener(this);

    // some default settings
    cellRenderer = new DefaultGridCellRenderer();
    setForeground(Color.LIGHT_GRAY);
    setBackground(Color.BLACK);
    setSelectionColor(Color.BLUE.brighter());
    setFocusColor(Color.RED);
    setPreferredSize(new Dimension(100, 100));
    setLinesVisible(true);
    setAnimationsEnabled(true);
    setPanningAllowed(true);
    setZoomingAllowed(true);
    setHoverAllowed(true);
    setOpaque(true);
    setMinimumZoom(1);
    setMaximumZoom(Double.MAX_VALUE);

    shouldCenterOnFirstPaint = true;

    this.addMouseWheelListener(this);
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
    this.selectionModel.addGridSelectionListener(this);
  }

  /**
   * Adds a property change listener to this grid. The listener will be informed
   * upon changes of the following properties (the corresponding string given in
   * the event is in parentheses):
   * <ul>
   * <li>Model ("model")</li>
   * <li>Cell renderer ("cellrenderer")</li>
   * <li>Zoom factor ("zoom")</li>
   * <li>X axis offset ("offsetx")</li>
   * <li>Y axis offset ("offsety")</li>
   * </ul>
   * 
   * @param l
   *          the {@link PropertyChangeListener} to add
   */
  @Override
  public void addPropertyChangeListener(PropertyChangeListener l) {
    pcListeners.addPropertyChangeListener(l);
  }

  @Override
  public void boundsChanged(Rectangle newBounds) {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        repaint();
      }
    });
  }

  @Override
  public void cellChanged(final int x, final int y) {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        repaint(getCoordinatesForCell(x, y).getBounds());
      }
    });
  }

  @Override
  public void cellRangeChanged(Rectangle r) {
    Rectangle a = getCoordinatesForCell(r.x, r.y).getBounds();
    Rectangle b =
        getCoordinatesForCell(r.x + r.width, r.y + r.height).getBounds();
    final Rectangle x =
        new Rectangle(a.x, a.y, (b.x + b.width) - a.x, (b.y + b.height) - a.y);
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        repaint(x);
      }
    });
  }

  /**
   * Centers the used grid inside the view and sets the zoom factor accordingly.
   * If animations are enabled this transition will be animated.
   */
  public void center() {
    double newZ;
    final double newOffsetX;
    final double newOffsetY;

    Rectangle b = model.getBounds();

    // get the dimension that needs to be fitted inside the canvas
    // will be at least 1, otherwise we get nasty exceptions if the
    // grid is
    // larger than this control
    if (getWidth() / (double) getHeight() > b.width / (double) b.height) {
      // height will hit the bounds first
      newZ = Math.max(getHeight() / (double) b.height, 1);
    } else {
      // width will hit the bounds first
      newZ = Math.max(getWidth() / (double) b.width, 1);
    }

    newZ = Math.max(newZ, minZoom);
    newZ = Math.min(newZ, maxZoom);

    final double newZoom = newZ;

    newOffsetX = (getWidth() - (b.width * newZoom)) / 2d / newZoom - b.x;
    newOffsetY = (getHeight() - (b.height * newZoom)) / 2d / newZoom - b.y;

    if (animationsEnabled) {
      if (ani != null && ani.isRunning()) {
        ani.abort();
      }
      ani = new SwingAnimator(300, new SplineInterpolator(0.2, 0.2));
      ani.addAnimatorListener(new IAnimatorListener() {
        private double oldZ, neoZ;

        private double oldX, oldY, newX, newY;

        @Override
        public void animate(double frac) {
          double z = oldZ + (neoZ - oldZ) * frac;
          double x = oldX * oldZ + (newX * neoZ - oldX * oldZ) * frac;
          x /= z;
          double y = oldY * oldZ + (newY * neoZ - oldY * oldZ) * frac;
          y /= z;
          setZoomAndOffset(z, x, y);
        }

        @Override
        public void started() {
          oldX = getOffsetX();
          oldY = getOffsetY();
          oldZ = getZoom();
          neoZ = newZoom;
          newX = newOffsetX;
          newY = newOffsetY;
        }

        @Override
        public void stopped() {
          setZoomAndOffset(neoZ, newX, newY);
        }
      });
      ani.start();
    } else {
      setZoomAndOffset(newZoom, newOffsetX, newOffsetY);
    }
  }

  /**
   * Centers the view on the point at the given coordinate. The zoom factor
   * remains unchanged. This method performs an animation if animations are
   * enabled.
   * 
   * @param x
   *          The X coordinate to center.
   * @param y
   *          The Y coordinate to center.
   */
  public void centerTo(int x, int y) {
    int deltaViewX, deltaViewY;
    deltaViewX = (getWidth() / 2) - x;
    deltaViewY = (getHeight() / 2) - y;

    final double deltaOffsetX, deltaOffsetY;
    deltaOffsetX = deltaViewX / zoom;
    deltaOffsetY = deltaViewY / zoom;

    if (animationsEnabled) {
      if (ani != null && ani.isRunning()) {
        ani.removeAnimatorListener(listener);
        ani.abort();
      }

      ani = new SwingAnimator(150, new LinearInterpolator());
      listener =
          new IAnimatorListenerImplementation(deltaOffsetX, deltaOffsetY);
      ani.addAnimatorListener(listener);
      ani.start();
    } else {
      setOffsetX(offsetX + deltaOffsetX);
      setOffsetY(offsetY + deltaOffsetY);
    }
  }

  @Override
  public void dataChanged() {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        repaint();
      }
    });
  }

  @Override
  public void renderingChanged() {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        repaint();
      }
    });
  }

  /**
   * Returns the model coordinates of the cell at the given view coordinates.
   * 
   * @param x
   *          X coordinate.
   * @param y
   *          Y coordinate.
   * @return The coordinates of the cell in the model.
   */
  public Point getCellForCoordinates(int x, int y) {
    return new Point((int) Math.floor((x / zoom - offsetX)),
        (int) Math.floor((y / zoom - offsetY)));
  }

  /**
   * Returns the current cell renderer.
   * 
   * @return The cell renderer currently used by the grid.
   */
  public IGridCellRenderer getCellRenderer() {
    return cellRenderer;
  }

  /**
   * Returns the view coordinates of the cell at the given model coordinates.
   * 
   * @param x
   *          X coordinate.
   * @param y
   *          Y coordinate.
   * @return A {@code Rectangle} describing position and size of the cell in
   *         view.
   */
  public Shape getCoordinatesForCell(int x, int y) {
    Point upperLeftViewCorner = getUpperLeftViewCorner();
    if (linesVisible && zoom >= 3) {
      // lines are shown
      return new Rectangle(
          (int) (upperLeftViewCorner.x + x * zoom),
          (int) (upperLeftViewCorner.y + y * zoom),
          ((int) (upperLeftViewCorner.x + (x + 1) * zoom) - (int) (upperLeftViewCorner.x + x
              * zoom)) - 1,
          ((int) (upperLeftViewCorner.y + (y + 1) * zoom) - (int) (upperLeftViewCorner.y + y
              * zoom)) - 1);
    }

    return new Rectangle(
        (int) (upperLeftViewCorner.x + x * zoom),
        (int) (upperLeftViewCorner.y + y * zoom),
        ((int) (upperLeftViewCorner.x + (x + 1) * zoom) - (int) (upperLeftViewCorner.x + x
            * zoom)),
        ((int) (upperLeftViewCorner.y + (y + 1) * zoom) - (int) (upperLeftViewCorner.y + y
            * zoom)));
  }

  /**
   * Returns the currently used model.
   * 
   * @return The model currently used by the grid.
   */
  public IGrid2DModel getModel() {
    return model;
  }

  /**
   * Returns the current X offset.
   * 
   * @return The current offset along the X axis.
   */
  public double getOffsetX() {
    return offsetX;
  }

  /**
   * Returns the current Y offset.
   * 
   * @return The current offset along the Y axis.
   */
  public double getOffsetY() {
    return offsetY;
  }

  /**
   * Returns the coordinates of the upper left corner of the rectangle of
   * visible cells.
   * 
   * @return A {@code Point} describing the coordinates of the upper left corner
   *         of visible cells.
   */
  protected Point getUpperLeftViewCorner() {
    return new Point((int) (zoom * offsetX), (int) (zoom * offsetY));
  }

  /**
   * Returns a rectangular range of cells that are currently visible in the
   * specified rectangle.
   * 
   * @param r
   *          The rectangle to check for visible cells.
   * @return A {@code Rectangle} of cells visible in the given {@code Rectangle}
   *         .
   */
  protected Rectangle getVisibleCells(Rectangle r) {
    Rectangle b = model.getBounds();

    Point p1 = getCellForCoordinates(r.x, r.y);
    p1.x = Math.max(p1.x, b.x);
    p1.y = Math.max(p1.y, b.y);
    Point p2 = getCellForCoordinates(r.x + r.width, r.y + r.height);
    p2.x = Math.min(p2.x + 1, b.x + b.width);
    p2.y = Math.min(p2.y + 1, b.y + b.height);

    return new Rectangle(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
  }

  /**
   * Returns the current zoom factor.
   * 
   * @return The current zoom factor.
   */
  public double getZoom() {
    return zoom;
  }

  /**
   * Returns the minimum zoom factor.
   * 
   * @return The minimum allowed zoom factor.
   */
  public double getMinimumZoom() {
    return minZoom;
  }

  /**
   * Returns the maximum zoom factor.
   * 
   * @return The maximum allowed zoom factor.
   */
  public double getMaximumZoom() {
    return maxZoom;
  }

  /**
   * Returns, whether animations are enabled on this control.
   * 
   * @return  {@code true} if animations are enabled, {@code false} otherwise.
   */
  public boolean isAnimationsEnabled() {
    return animationsEnabled;
  }

  /**
   * Returns whether hovering the mouse pointer over cells will highlight them.
   * 
   * @return {@code true} if mouse hovering highlights the cells, {@code false}
   *         otherwise.
   */
  public boolean isHoverAllowed() {
    return hoverAllowed;
  }

  /**
   * Returns whether grid lines may be shown. However, grid lines are hidden
   * when the zoom factor is below a certain threshold to avoid visual clutter.
   * That behavior is independent of this setting and not configurable.
   * 
   * @return {@code true} if grid lines are turned on, {@code false} otherwise.
   */
  public boolean isLinesVisible() {
    return linesVisible;
  }

  /**
   * Returns whether the user can pan the content of this grid.
   * 
   * @return  {@code true} if panning is allowed, {@code false} otherwise.
   */
  public boolean isPanningAllowed() {
    return panningAllowed;
  }

  /**
   * Returns whether the user can zoom the content of this grid.
   * 
   * @return  {@code true} if zooming is allowed, {@code false} otherwise.
   */
  public boolean isZoomingAllowed() {
    return zoomingAllowed;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    updateHoverCell(e.getX(), e.getY());
    if (panningAllowed && dragOrigin != null && isPanning) {
      setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      setOffsetX(getOffsetX() - (dragOrigin.x - e.getX()) / zoom);
      setOffsetY(getOffsetY() - (dragOrigin.y - e.getY()) / zoom);
      dragOrigin.x = e.getX();
      dragOrigin.y = e.getY();
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
    if (hoverCell != null) {
      redrawCell(hoverCell.x, hoverCell.y);
    }
    hoverCell = null;
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    updateHoverCell(e.getX(), e.getY());
  }

  @Override
  public void mousePressed(MouseEvent e) {
    dragOrigin = new Point(e.getX(), e.getY());
    if (e.getButton() == MouseEvent.BUTTON3) {
      isPanning = true;
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    dragOrigin = null;
    isPanning = false;
    setCursor(Cursor.getDefaultCursor());
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (zoomingAllowed) {
      if (e.getWheelRotation() == 0) {
        return;
      }
      double oldZoom = getZoom();
      double newZoom =
          e.getWheelRotation() < 0 ? 2 * oldZoom : Math.max(1, oldZoom / 2);

      zoomTo(e.getX(), e.getY(), newZoom);

      updateHoverCell(e.getX(), e.getY());
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    // is this the first call to paintComponent()?
    if (shouldCenterOnFirstPaint) {
      // don't use animations when centering here
      boolean oldAnimationFlag = isAnimationsEnabled();
      setAnimationsEnabled(false);
      center();
      setAnimationsEnabled(oldAnimationFlag);
      // don't need to draw again, center() does that already, so just
      // jump out
      return;
    }

    // honor opaque
    if (isOpaque()) {
      // clear the background
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }

    Rectangle visibleCells =
        getVisibleCells(g.getClipBounds() == null ? new Rectangle(getSize())
            : g.getClipBounds());

    // let the renderer draw cells
    Graphics g2 = g.create();
    Rectangle clipBounds = g2.getClipBounds();
    for (int y = visibleCells.y; y < visibleCells.y + visibleCells.height; y++) {
      for (int x = visibleCells.x; x < visibleCells.x + visibleCells.width; x++) {
        Shape s = getCoordinatesForCell(x, y);
        Rectangle r = s.getBounds();

        Area clip = new Area(clipBounds);
        clip.intersect(new Area(s));

        g2.setClip(clip);

        cellRenderer.draw(this, g2, r.x, r.y, r.width, r.height, s, x, y,
            model.getValueAt(x, y),
            (selectionModel != null) ? selectionModel.isSelected(x, y) : false,
            hoverAllowed && hoverCell != null && hoverCell.x == x
                && hoverCell.y == y);
      }
    }
    g2.dispose();

    if (linesVisible && zoom >= 3) {
      drawOutline(g, visibleCells);
    }
  }

  protected void drawOutline(Graphics g, Rectangle visibleCells) {
    Point ul = getUpperLeftViewCorner();

    Rectangle b = model.getBounds();

    int minX = Math.min(b.x, visibleCells.x);
    int maxX = Math.max(b.x + b.width, visibleCells.x + visibleCells.width);
    int minY = Math.min(b.y, visibleCells.y);
    int maxY = Math.max(b.y + b.height, visibleCells.y + visibleCells.height);

    // darker lines
    g.setColor(Colors.getIntermediateColor(getForeground(), getBackground()));
    for (int x = minX; x <= maxX; x++) {
      g.drawLine((int) (ul.x + zoom * x - 1), (int) (ul.y + zoom * minY - 1),
          (int) (ul.x + zoom * x - 1), (int) (ul.y + zoom * maxY - 1));
    }
    for (int y = minY; y <= maxY; y++) {
      g.drawLine((int) (ul.x + zoom * minX - 1), (int) (ul.y + zoom * y - 1),
          (int) (ul.x + zoom * maxX - 1), (int) (ul.y + zoom * y - 1));
    }

    // bright lines, every 10
    g.setColor(getForeground());
    for (int x = minX / 10 * 10; x <= maxX; x += 10) {
      g.drawLine((int) (ul.x + zoom * x - 1), (int) (ul.y + zoom * minY - 1),
          (int) (ul.x + zoom * x - 1), (int) (ul.y + zoom * maxY - 1));
    }
    for (int y = minY / 10 * 10; y <= maxY; y += 10) {
      g.drawLine((int) (ul.x + zoom * minX - 1), (int) (ul.y + zoom * y - 1),
          (int) (ul.x + zoom * maxX - 1), (int) (ul.y + zoom * y - 1));
    }
  }

  /**
   * Redraws a cell at the given coordinate.
   * 
   * @param x
   *          The x coordinate of the cell
   * @param y
   *          The y coordinate of the cell
   */
  protected void redrawCell(int x, int y) {
    repaint(getCoordinatesForCell(x, y).getBounds());
  }

  /**
   * Redraws a cell at the given coordinates.
   * 
   * @param p
   *          The coordinates.
   */
  protected void redrawCell(Point p) {
    if (p == null) {
      return;
    }
    redrawCell(p.x, p.y);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener l) {
    pcListeners.removePropertyChangeListener(l);
  }

  @Override
  public void selectionChanged(int x1, int y1, int x2, int y2) {
    final Rectangle upperLeft = getCoordinatesForCell(x1, y1).getBounds();
    final Rectangle lowerRight = getCoordinatesForCell(x2, y2).getBounds();

    BasicUtilities.invokeLaterOnEDT(new Runnable() {

      @Override
      public void run() {
        repaint(upperLeft.x, upperLeft.y, lowerRight.x - upperLeft.x
            + lowerRight.width, lowerRight.y - upperLeft.y + lowerRight.height);
      }

    });
  }

  /**
   * Sets whether the control should animate certain actions, like centering the
   * used grid in the view, etc.
   * 
   * @param animationsEnabled
   *          {@code true} if animations should be enabled, {@code false}
   *          otherwise.
   */
  public final void setAnimationsEnabled(boolean animationsEnabled) {
    this.animationsEnabled = animationsEnabled;
  }

  /**
   * Sets a new cell renderer.
   * 
   * @param cellRenderer
   *          The new cell renderer.
   */
  public final void setCellRenderer(IGridCellRenderer cellRenderer) {
    IGridCellRenderer oldRenderer = this.cellRenderer;
    this.cellRenderer = cellRenderer;
    pcListeners.firePropertyChange("cellrenderer", oldRenderer,
        this.cellRenderer);
  }

  /**
   * Sets whether hovering over cells will highlight them.
   * 
   * @param hoverAllowed
   *          {@code true} if mouse hovering should highlight the cells,
   *          {@code false} otherwise.
   */
  public final void setHoverAllowed(boolean hoverAllowed) {
    this.hoverAllowed = hoverAllowed;
  }

  /**
   * Sets whether the grid lines should be shown. Grid lines are always hidden
   * if the zoom factor is below 3.
   * 
   * @param linesVisible
   *          {@code true} if grid lines should be shown, {@code false}
   *          otherwise.
   */
  public final void setLinesVisible(boolean linesVisible) {
    this.linesVisible = linesVisible;
    repaint();
  }

  /**
   * Sets a new model.
   * 
   * @param model
   *          The new model to use.
   */
  public final void setModel(IGrid2DModel model) {
    IGrid2DModel oldModel = this.model;
    this.model = model;
    pcListeners.firePropertyChange("model", oldModel, this.model);
  }

  /**
   * Sets a new X offset.
   * 
   * @param offsetX
   *          The new offset on the X axis.
   */
  public final void setOffsetX(double offsetX) {
    shouldCenterOnFirstPaint = false;
    double oldX = this.offsetX;
    this.offsetX = offsetX;
    pcListeners.firePropertyChange("offsetx", oldX, this.offsetX);
    repaint();
  }

  /**
   * Sets a new Y offset.
   * 
   * @param offsetY
   *          The new offset on the Y axis.
   */
  public final void setOffsetY(double offsetY) {
    shouldCenterOnFirstPaint = false;
    double oldY = this.offsetY;
    this.offsetY = offsetY;
    pcListeners.firePropertyChange("offsety", oldY, this.offsetY);
    repaint();
  }

  /**
   * Sets whether the user is allowed to pan this grid's content.
   * 
   * @param panningAllowed
   *          {@code true} if the user may pan the content, {@code false}
   *          otherwise.
   */
  public final void setPanningAllowed(boolean panningAllowed) {
    this.panningAllowed = panningAllowed;
  }

  /**
   * Sets a new zoom factor. The zoom factor equals the size of width and height
   * of one cell displayed.
   * 
   * @param zoomFactor
   *          The new zoom factor.
   */
  public final void setZoom(double zoomFactor) {
    double oldZoom = this.zoom;
    shouldCenterOnFirstPaint = false;

    this.zoom = zoomFactor;

    if (zoom < minZoom) {
      zoom = minZoom;
    }
    if (zoom > maxZoom) {
      zoom = maxZoom;
    }

    pcListeners.firePropertyChange("zoom", oldZoom, this.zoom);
    repaint();
  }

  /**
   * Sets the minimum zoom factor of this grid.
   * 
   * @param minZoom
   *          The new minimum zoom factor.
   */
  public final void setMinimumZoom(double minZoom) {
    this.minZoom = minZoom;
  }

  /**
   * Sets the maximum zoom factor of this grid.
   * 
   * @param maxZoom
   *          The new maximum zoom factor.
   */
  public final void setMaximumZoom(double maxZoom) {
    this.maxZoom = maxZoom;
  }

  /**
   * Convenience method of setting both zoom and offset in one method call. May
   * save unnecessary repaints but not necessarily so.
   * 
   * @param zoomFactor
   *          The new zoom factor.
   * @param offsetX
   *          The new X offset.
   * @param offsetY
   *          The new Y offset.
   */
  public void setZoomAndOffset(double zoomFactor, double offsetX, double offsetY) {

    double oldZoom = this.zoom;
    double oldX = this.offsetX;
    double oldY = this.offsetY;

    this.zoom = zoomFactor;

    if (zoom < minZoom) {
      zoom = minZoom;
    }
    if (zoom > maxZoom) {
      zoom = maxZoom;
    }
    shouldCenterOnFirstPaint = false;

    this.offsetX = offsetX;
    this.offsetY = offsetY;

    pcListeners.firePropertyChange("zoom", oldZoom, this.zoom);
    pcListeners.firePropertyChange("offsetx", oldX, this.offsetX);
    pcListeners.firePropertyChange("offsety", oldY, this.offsetX);

    repaint();
  }

  /**
   * Sets whether the user is allowed to zoom this grid's content.
   * 
   * @param zoomingAllowed
   *          {@code true} if the user may zoom the content, {@code false}
   *          otherwise.
   */
  public final void setZoomingAllowed(boolean zoomingAllowed) {
    this.zoomingAllowed = zoomingAllowed;
  }

  /**
   * @return the selectionModel
   */
  public final IGridSelectionModel getSelectionModel() {
    return selectionModel;
  }

  /**
   * @param selectionModel
   *          the selectionModel to set
   */
  public final void setSelectionModel(IGridSelectionModel selectionModel) {
    if (selectionModel == null) {
      return;
    }

    if (this.selectionModel != null) {
      this.selectionModel.removeGridSelectionListener(this);
    }

    this.selectionModel = selectionModel;
    this.selectionModel.addGridSelectionListener(this);

    repaint();
  }

  /**
   * Updates the rendering of the cell currently hovered over (mouse coordinates
   * are provided as arguments). This includes redrawing the cell last hovered
   * over as well as redrawing the cell the mouse pointer is currently over. ,
   * 
   * @param x
   *          The mouse X coordinate.
   * @param y
   *          The mouse Y coordinate.
   */
  private void updateHoverCell(int x, int y) {
    if (hoverAllowed) {
      Point oldHoverCell = hoverCell;
      hoverCell = getCellForCoordinates(x, y);
      Rectangle b = model.getBounds();
      if (hoverCell.x < b.x || hoverCell.x >= b.x + b.width
          || hoverCell.y < b.y || hoverCell.y >= b.y + b.height) {
        hoverCell = null;
      }

      if (oldHoverCell != null && hoverCell != null) {
        if (oldHoverCell.equals(hoverCell)) {
          return;
        }
      }

      if (oldHoverCell != null) {
        redrawCell(oldHoverCell);
      }
      if (hoverCell != null) {
        redrawCell(hoverCell);
      }
    }
  }

  /**
   * Zooms the viewport to a specified coordinate and zoom factor. Basically
   * this means that the point in model coordinates at the specified view
   * coordinate will stay at that point. This method performs an animation if
   * animations are enabled.
   * 
   * @param x
   *          The X coordinate to zoom to.
   * @param y
   *          The Y coordinate to zoom to.
   * @param zoomFactor
   *          The new zoom factor.
   */
  public void zoomTo(int x, int y, final double zoomFactor) {
    if (zoomFactor < minZoom || zoomFactor > maxZoom) {
      return;
    }

    double modelX, modelY;
    modelX = x / zoom - offsetX;
    modelY = y / zoom - offsetY;

    int newViewX, newViewY;
    newViewX = (int) ((modelX + offsetX) * zoomFactor);
    newViewY = (int) ((modelY + offsetY) * zoomFactor);

    int deltaViewX, deltaViewY;
    deltaViewX = newViewX - x;
    deltaViewY = newViewY - y;

    final double deltaOffsetX, deltaOffsetY;
    deltaOffsetX = deltaViewX / zoomFactor;
    deltaOffsetY = deltaViewY / zoomFactor;

    if (animationsEnabled) {
      if (ani != null && ani.isRunning()) {
        ani.removeAnimatorListener(listener);
        ani.abort();
      }

      ani = new SwingAnimator(150, new LinearInterpolator());

      listener = new IAnimatorListener() {
        private double oldZ, newZ;

        private double oldX, oldY, newX, newY;

        @Override
        public void animate(double frac) {
          double z = oldZ + (newZ - oldZ) * frac;
          double x = oldX * oldZ + (newX * newZ - oldX * oldZ) * frac;
          x /= z;
          double y = oldY * oldZ + (newY * newZ - oldY * oldZ) * frac;
          y /= z;
          setZoomAndOffset(z, x, y);
        }

        @Override
        public void started() {
          oldX = getOffsetX();
          oldY = getOffsetY();
          oldZ = getZoom();
          newZ = zoomFactor;
          newX = oldX - deltaOffsetX;
          newY = oldY - deltaOffsetY;
        }

        @Override
        public void stopped() {
          setZoomAndOffset(newZ, newX, newY);
        }
      };

      ani.addAnimatorListener(listener);
      ani.start();
    } else {
      setZoomAndOffset(zoomFactor, offsetX - deltaOffsetX, offsetY
          - deltaOffsetY);
    }
  }

  /**
   * Sets the selection color.
   * 
   * @param selectionColor
   *          the new selection color
   */
  public void setSelectionColor(Color selectionColor) {
    this.selectionColor = selectionColor;
    repaint();
  }

  /**
   * Gets the selection color.
   * 
   * @return the selection color
   */
  public Color getSelectionColor() {
    return selectionColor;
  }

  /**
   * Sets the focus color.
   * 
   * @param focusColor
   *          the new focus color
   */
  public final void setFocusColor(Color focusColor) {
    this.focusColor = focusColor;
    repaint();
  }

  /**
   * Gets the focus color.
   * 
   * @return the focus color
   */
  public Color getFocusColor() {
    return focusColor;
  }

  /**
   * Converts the grid to an image. Use this if you want to export the "entire"
   * grid with a specific cell size as {@link Image}.
   * 
   * @param cellSize
   *          the cell size to use when exporting (independent from the current
   *          zoom level)
   * @return the image containing the entire grid as image (independent from
   *         currently visible grid area)
   */
  public Image toImage(int cellSize) {
    Rectangle bounds = model.getBounds();
    BufferedImage result =
        new BufferedImage(bounds.width * cellSize, bounds.height * cellSize,
            BufferedImage.TYPE_4BYTE_ABGR);

    Graphics2D g2 = (Graphics2D) result.getGraphics();
    g2.setColor(getBackground());
    g2.fillRect(0, 0, result.getWidth(), result.getHeight());

    // draw cells
    for (int y = 0; y < bounds.height; y++) {
      for (int x = 0; x < bounds.width; x++) {
        int cx = ((x + bounds.x) * cellSize);
        int cy = ((y + bounds.y) * cellSize);
        cellRenderer.draw(this, g2, cx, cy, cellSize, cellSize,
            getCoordinatesForCell(x, y), x, y, model.getValueAt(x, y),
            (selectionModel != null) ? selectionModel.isSelected(x, y) : false,
            false);
      }
    }

    // draw grid
    if (linesVisible && cellSize >= 3) {

      for (int y = 0; y < bounds.height; y++) {
        if (y % 10 == 0) {
          g2.setColor(getForeground());
        } else {
          g2.setColor(Colors.getIntermediateColor(getForeground(),
              getBackground()));
        }

        g2.drawLine(0, cellSize * y - 1, bounds.width * cellSize, cellSize * y
            - 1);
      }

      for (int x = 0; x < bounds.width; x++) {
        if (x % 10 == 0) {
          g2.setColor(getForeground());
        } else {
          g2.setColor(Colors.getIntermediateColor(getForeground(),
              getBackground()));
        }

        g2.drawLine(cellSize * x - 1, 0, cellSize * x - 1, cellSize
            * bounds.height);
      }
    }
    g2.dispose();

    return result;
  }

  /**
   * Quick test method
   * 
   * @param args
   */
  public static void main(String[] args) {
    JFrame f = new JFrame();

    final Grid2D g = new Grid2D(new AbstractGrid2DModel() {

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

    GridSelectionMetaphor.createSelectionMetaphorFor(g);
    g.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          if (e.getClickCount() == 2) {
            g.center();
          }
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2) {
          g.centerTo(e.getX(), e.getY());
        }
      }
    });

    ImageGridCellRenderer renderer = new ImageGridCellRenderer();
    for (int i = 0; i < 20; i++) {
      renderer.setImageMapping(Integer.valueOf(i), IconManager.getImage(String
          .format(
              "image:/org/jamesii/gui/icons/pinvoke/diagona/04/16/%02d.png",
              i + 1), true));
    }
    renderer.setKeepAspectRatio(true);

    g.setCellRenderer(renderer);

    g.setMinimumZoom(4);
    ((DefaultGridSelectionModel) g.getSelectionModel())
        .setSelectionMode(DefaultGridSelectionModel.MULTIPLE_SELECTION);

    f.setLayout(new BorderLayout());
    f.add(g, BorderLayout.CENTER);

    JPanel p = new JPanel();
    p.setLayout(new GridLayout(2, 1));

    JLabel hint1 =
        new JLabel(
            "Drag around with middle mouse button, zoom with scroll wheel.");
    p.add(hint1);
    JLabel hint2 =
        new JLabel(
            "Double-click to fit into view, Right click to center the clicked point in view.");
    p.add(hint2);

    f.add(p, BorderLayout.NORTH);

    f.pack();
    f.setSize(new Dimension(600, 600));
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
  }

}
