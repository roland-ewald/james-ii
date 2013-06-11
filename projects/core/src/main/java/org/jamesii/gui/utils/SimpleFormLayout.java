/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import static org.jamesii.gui.utils.SimpleFormLayout.FormConstraint.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * Very simple variation of a grid bag layout that is by default left adjusted
 * and has fewer parameters for layouting the content of cells.
 * 
 * @author Stefan Rybacki
 */
public class SimpleFormLayout implements LayoutManager2 {

  /**
   * Class that is used to attribute the components when arranging in the grid
   * that is used to represent the {@link SimpleFormLayout}.
   * 
   * @author Stefan Rybacki
   */
  public static final class FormConstraint {

    /**
     * Centers the content horizontally and vertically within the occupied
     * cells.
     */
    public static final int CENTER = 0;

    /**
     * Centers the content horizontally and aligns it to the top of the occupied
     * cells.
     */
    public static final int NORTH = 1 << 0;

    /**
     * Centers the content vertically and aligns it to the left of the occupied
     * cells.
     */
    public static final int WEST = 1 << 1;

    /**
     * Centers the content vertically and aligns it to the right of the occupied
     * cells.
     */
    public static final int EAST = 1 << 2;

    /**
     * Centers the content horizontally and aligns it to the bottom of the
     * occupied cells.
     */
    public static final int SOUTH = 1 << 3;

    /** Aligns the content at the upper left corner of the occupied cells. */
    public static final int NORTH_WEST = NORTH | WEST;

    /** Aligns the content at the upper right corner of the occupied cells. */
    public static final int NORTH_EAST = NORTH | EAST;

    /** Aligns the content at the lower left corner of the occupied cells. */
    public static final int SOUTH_WEST = SOUTH | WEST;

    /** Aligns the content at the lower right corner of the occupied cells. */
    public static final int SOUTH_EAST = SOUTH | EAST;

    /**
     * Fills the content horizontally over the entire region of the occupied
     * cells.
     */
    public static final int HORIZONTAL = 1 << 0;

    /**
     * Fills the content vertically over the entire region of the occupied
     * cells.
     */
    public static final int VERTICAL = 1 << 1;

    /**
     * Fills the content horizontally and vertically over the entire region of
     * the occupied cells.
     */
    public static final int BOTH = HORIZONTAL | VERTICAL;

    /** Filling constant specifying that no filling is expected. */
    public static final int NONE = 0;

    /** column in grid. */
    private final int x;

    /** row in grid. */
    private final int y;

    /** position within the occupied cells. */
    private final int anchor;

    /** how to fill the region of occupied cells. */
    private final int fill;

    /** specifies how many cells are occupied in x direction. */
    private final int width;

    /** specifies how many cells are occupied in y direction. */
    private final int height;

    /**
     * Omitted constructor. use static methods.
     * 
     * @param x
     *          the column
     * @param y
     *          the row
     * @param anchor
     *          the anchor
     * @param fill
     *          the fill options
     * @param width
     *          the width
     * @param height
     *          the height
     */
    private FormConstraint(int x, int y, int anchor, int fill, int width,
        int height) {
      if (width < 1) {
        throw new IllegalArgumentException("width must be >=1");
      }
      if (height < 1) {
        throw new IllegalArgumentException("height must be >=1");
      }
      this.x = x;
      this.y = y;
      this.anchor = anchor;
      this.fill = fill;
      this.width = width;
      this.height = height;
    }

    /**
     * Cell xy.
     * 
     * @param x
     *          the column in the grid
     * @param y
     *          the row in the grid
     * 
     * @return constraint object pointing to specified cell
     */
    public static FormConstraint cellXY(int x, int y) {
      return new FormConstraint(x, y, NORTH_WEST, -1, 1, 1);
    }

    /**
     * Cell xy.
     * 
     * @param x
     *          the column in the grid
     * @param y
     *          the row in the grid
     * @param anchor
     *          the position of the content within the cell
     * 
     * @return constraint object pointing to specified cell and position
     * 
     * @see FormConstraint#NORTH
     * @see FormConstraint#SOUTH
     * @see FormConstraint#WEST
     * @see FormConstraint#EAST
     * @see FormConstraint#NORTH_WEST
     * @see FormConstraint#NORTH_EAST
     * @see FormConstraint#SOUTH_WEST
     * @see FormConstraint#SOUTH_EAST
     */
    public static FormConstraint cellXY(int x, int y, int anchor) {
      return new FormConstraint(x, y, anchor, -1, 1, 1);
    }

    /**
     * Cell xy.
     * 
     * @param x
     *          the column in the grid
     * @param y
     *          the row in the grid
     * @param anchor
     *          the position of the content within the cell
     * @param fill
     *          the fill option
     * 
     * @return constraint object pointing to specified cell and position
     * 
     * @see FormConstraint#NORTH
     * @see FormConstraint#SOUTH
     * @see FormConstraint#WEST
     * @see FormConstraint#EAST
     * @see FormConstraint#NORTH_WEST
     * @see FormConstraint#NORTH_EAST
     * @see FormConstraint#SOUTH_WEST
     * @see FormConstraint#SOUTH_EAST
     * @see FormConstraint#HORIZONTAL
     * @see FormConstraint#VERTICAL
     * @see FormConstraint#BOTH
     */
    public static FormConstraint cellXY(int x, int y, int anchor, int fill) {
      return new FormConstraint(x, y, anchor, fill, 1, 1);
    }

    /**
     * Cell xy.
     * 
     * @param x
     *          the column in the grid
     * @param y
     *          the row in the grid
     * @param anchor
     *          the position of the content within the cell
     * @param fill
     *          the fill option
     * @param width
     *          the number of covered cells in x direction
     * @param height
     *          the number of covered cells in y direction
     * @return constraint object pointing to specified cell and position
     * @see FormConstraint#NORTH
     * @see FormConstraint#SOUTH
     * @see FormConstraint#WEST
     * @see FormConstraint#EAST
     * @see FormConstraint#NORTH_WEST
     * @see FormConstraint#NORTH_EAST
     * @see FormConstraint#SOUTH_WEST
     * @see FormConstraint#SOUTH_EAST
     * @see FormConstraint#HORIZONTAL
     * @see FormConstraint#VERTICAL
     * @see FormConstraint#BOTH
     */
    public static FormConstraint cellXY(int x, int y, int anchor, int fill,
        int width, int height) {
      return new FormConstraint(x, y, anchor, fill, width, height);
    }

  }

  /**
   * Internal class used to represent a grid cell.
   * 
   * @author Stefan Rybacki
   */
  private static final class GridElement {

    /** the component in the grid. */
    private final Component comp;

    /** the constraints used to align and fill the component. */
    private final FormConstraint constraint;

    /**
     * Omitted constructor for internal use only.
     * 
     * @param constraints
     *          the constraints
     * @param comp
     *          the comp
     */
    private GridElement(FormConstraint constraints, Component comp) {
      this.constraint = constraints;
      this.comp = comp;
    }
  }

  /** The grid representing the layout. */
  private GridElement[][] grid = new GridElement[10][10];

  /** current occupied width of the grid (<=grid.length) */
  private int gridWidth = 0;

  /** current occupied height of the grid (<=grid[x].length) */
  private int gridHeight = 0;

  /** The rows. */
  private int[] rows;

  /** The columns. */
  private int[] columns;

  /**
   * Instantiates a new simple form layout.
   */
  public SimpleFormLayout() {
    this(10, 10);
  }

  /**
   * Instantiates a new simple form layout with an initial grid size.
   * <p>
   * Note: the grid is extended as needed.
   * 
   * @param width
   *          the grid width
   * @param height
   *          the grid height
   */
  public SimpleFormLayout(int width, int height) {
    super();
    grid = new GridElement[width][height];
    columns = getColumnSizes();
    rows = getRowSizes();
  }

  @Override
  public void addLayoutComponent(String name, Component comp) {
    columns = getColumnSizes();
    rows = getRowSizes();
  }

  /**
   * Helper function that creates a new grid if the old one is to small to fit
   * the needs. It also copies the old grid to the new one. No resizing takes
   * place if the width and height of the old grid is sufficient.
   * 
   * @param w
   *          the needed minimum width
   * @param h
   *          the needed minimum height
   */
  private void resizeGridToFit(int w, int h) {
    if (grid.length < w || grid[0].length < h) {
      GridElement[][] newGrid =
          new GridElement[Math.max(w, grid.length)][Math.max(h, grid[0].length)];

      // copy elements
      for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[i].length; j++) {
          newGrid[i][j] = grid[i][j];
        }
      }

      grid = newGrid;
    }
  }

  /**
   * Gets the column sizes.
   * 
   * @return the column sizes
   */
  private int[] getColumnSizes() {
    int cols[] = new int[gridWidth];
    for (int i = 0; i < gridWidth; i++) {
      for (int j = 0; j < gridHeight; j++) {
        // only process items that are !=null and that do not spread across more
        // than one cell in width
        if (grid[i][j] != null && grid[i][j].comp != null
            && grid[i][j].constraint.width == 1) {
          Dimension pref = grid[i][j].comp.getPreferredSize();
          cols[i] = Math.max(cols[i], pref.width);
        }
      }
    }

    // now process all items that spread over more than one cell
    for (int i = 0; i < gridWidth; i++) {
      for (int j = 0; j < gridHeight; j++) {
        // only process items that are !=null and that do not spread across more
        // than one cell in width
        if (grid[i][j] != null && grid[i][j].comp != null
            && grid[i][j].constraint.width > 1) {
          // calculate size of cells i till i+width-1 and only stretch the last
          // cell that is occupied by this item (if needed)
          int width = 0;
          for (int k = 0; k < grid[i][j].constraint.width - 1; k++) {
            width += cols[i + k];
          }
          // calculate difference
          width = grid[i][j].comp.getPreferredSize().width - width;

          cols[i + grid[i][j].constraint.width - 1] =
              Math.max(cols[i + grid[i][j].constraint.width - 1], width);
        }
      }
    }

    return cols;
  }

  /**
   * Gets the row sizes.
   * 
   * @return the row sizes
   */
  private int[] getRowSizes() {
    int row[] = new int[gridHeight];
    for (int j = 0; j < gridHeight; j++) {
      for (int i = 0; i < gridWidth; i++) {
        // only process items that don't spread across rows
        if (grid[i][j] != null && grid[i][j].comp != null
            && grid[i][j].constraint.height == 1) {
          Dimension pref = grid[i][j].comp.getPreferredSize();
          row[j] = Math.max(row[j], pref.height);
        }
      }
    }

    // now process all items that spread over more than one row
    for (int j = 0; j < gridHeight; j++) {
      for (int i = 0; i < gridWidth; i++) {
        // only process items that are !=null and that do not spread across more
        // than one cell in width
        if (grid[i][j] != null && grid[i][j].comp != null
            && grid[i][j].constraint.height > 1) {
          // calculate size of cells i till i+width-1 and only stretch the last
          // cell that is occupied by this item (if needed)
          int height = 0;
          for (int k = 0; k < grid[i][j].constraint.height - 1; k++) {
            height += row[j + k];
          }
          // calculate difference
          height = grid[i][j].comp.getPreferredSize().height - height;

          row[j + grid[i][j].constraint.height - 1] =
              Math.max(row[j + grid[i][j].constraint.height - 1], height);

        }
      }
    }

    return row;
  }

  @Override
  public void layoutContainer(Container parent) {
    columns = getColumnSizes();
    rows = getRowSizes();
    // first determine max width and max height for each row and column

    int x = 2;
    for (int i = 0; i < gridWidth; i++) {
      int y = 2;
      for (int j = 0; j < gridHeight; j++) {
        if (grid[i][j] != null) {
          Dimension cellSize = new Dimension(columns[i], rows[j]);

          // calculate cellSize according to cell span
          for (int k = 1; k < grid[i][j].constraint.width; k++) {
            cellSize.width += columns[i + k];
          }
          for (int k = 1; k < grid[i][j].constraint.height; k++) {
            cellSize.height += rows[j + k];
          }

          Dimension pref = grid[i][j].comp.getPreferredSize();
          switch (grid[i][j].constraint.fill) {
          case HORIZONTAL:
            pref.width = cellSize.width;
            break;
          case VERTICAL:
            pref.height = cellSize.height;
            break;
          case BOTH:
            pref.width = cellSize.width;
            pref.height = cellSize.height;
            break;
          }

          int rx = x;
          int ry = y;

          // place the component according to anchor
          switch (grid[i][j].constraint.anchor) {
          case CENTER:
            rx = x + cellSize.width / 2 - pref.width / 2;
            ry = y + cellSize.height / 2 - pref.height / 2;
            break;
          case NORTH:
            rx = x + cellSize.width / 2 - pref.width / 2;
            ry = y;
            break;
          case SOUTH:
            rx = x + cellSize.width / 2 - pref.width / 2;
            ry = y + cellSize.height - pref.height;
            break;
          case WEST:
            rx = x;
            ry = y + cellSize.height / 2 - pref.height / 2;
            break;
          case EAST:
            rx = x + cellSize.width - pref.width;
            ry = y + cellSize.height / 2 - pref.height / 2;
            break;
          case NORTH_EAST:
            rx = x + cellSize.width - pref.width;
            ry = y;
            break;
          case SOUTH_EAST:
            rx = x + cellSize.width - pref.width;
            ry = y + cellSize.height - pref.height;
            break;
          case SOUTH_WEST:
            rx = x;
            ry = y + cellSize.height - pref.height;
            break;
          case NORTH_WEST:
          default:
            rx = x;
            ry = y;
            break;
          }

          grid[i][j].comp.setBounds(rx, ry, pref.width, pref.height);
        }
        y += 2 + rows[j];
      }
      x += columns[i] + 2;
    }
  }

  @Override
  public Dimension minimumLayoutSize(Container parent) {
    int x = 2;
    int y = 2;
    for (int i = 0; i < gridWidth; i++) {
      y = 0;
      for (int j = 0; j < gridHeight; j++) {
        y += 2 + rows[j];
      }
      x += 2 + columns[i];
    }
    return new Dimension(x, y);
  }

  @Override
  public Dimension preferredLayoutSize(Container parent) {
    return minimumLayoutSize(parent);
  }

  @Override
  public void removeLayoutComponent(Component comp) {

  }

  @Override
  public void addLayoutComponent(Component comp, Object constraints) {
    if (constraints == null || !(constraints instanceof FormConstraint)) {
      constraints = FormConstraint.cellXY(0, gridHeight);
    }
    FormConstraint c = (FormConstraint) constraints;
    resizeGridToFit(c.x + c.width, c.y + c.height);
    gridWidth = Math.max(c.x + c.width, gridWidth);
    gridHeight = Math.max(c.y + c.height, gridHeight);
    grid[c.x][c.y] = new GridElement(c, comp);
    columns = getColumnSizes();
    rows = getRowSizes();
  }

  @Override
  public float getLayoutAlignmentX(Container target) {
    return 0;
  }

  @Override
  public float getLayoutAlignmentY(Container target) {
    return 0;
  }

  @Override
  public void invalidateLayout(Container target) {

  }

  @Override
  public Dimension maximumLayoutSize(Container target) {
    return minimumLayoutSize(target);
  }

}
