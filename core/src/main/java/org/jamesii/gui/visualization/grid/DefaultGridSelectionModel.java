/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

/**
 * Basic selection model implementing {@link IGridSelectionModel} providing
 * three selection modes. {@link #SINGLE_SELECTION} where only one cell can be
 * selected at any time {@link #RANGE_SELECTION} where only a rectangular block
 * of cells can be selected at any time {@link #MULTIPLE_SELECTION} where any
 * cell can be selected additionally to the already selected cells and
 * {@link #NO_SELECTION} where no cell can be selected.
 * 
 * @author Stefan Rybacki
 */
public class DefaultGridSelectionModel extends AbstractGridSelectionModel {

  /**
   * The Constant NO_SELECTION. No cell can be selected.
   */
  public static final int NO_SELECTION = 0;

  /**
   * The Constant SINGLE_SELECTION. Only one cell can be selected at any time
   */
  public static final int SINGLE_SELECTION = 1;

  /**
   * The Constant RANGE_SELECTION. Only a rectangular block of cells can be
   * selected at any time
   */
  public static final int RANGE_SELECTION = 2;

  /**
   * The Constant MULTIPLE_SELECTION. Any combination of cells can be selected
   * at any time
   */
  public static final int MULTIPLE_SELECTION = 3;

  /**
   * The selection mode.
   */
  private int selectionMode = SINGLE_SELECTION;

  /**
   * The selected cells.
   */
  private Set<Point> selectedCells = new HashSet<>();

  /**
   * The last selection used in case the selection mode changes
   */
  private Rectangle lastSelection = null;

  /**
   * Instantiates a new grid selection model.
   * 
   * @param selectionMode
   *          the selection mode
   * @see #setSelectionMode(int)
   */
  public DefaultGridSelectionModel(int selectionMode) {
    super();
    setSelectionMode(selectionMode);
  }

  /**
   * Sets the selection mode.
   * 
   * @param selectionMode
   *          the new selection mode
   * @see #SINGLE_SELECTION
   * @see #RANGE_SELECTION
   * @see #MULTIPLE_SELECTION
   * @see #NO_SELECTION
   */
  public final void setSelectionMode(int selectionMode) {
    this.selectionMode = selectionMode;
    if (lastSelection != null) {
      addSelectedCellRange(lastSelection.x, lastSelection.y, lastSelection.x
          + lastSelection.width, lastSelection.y + lastSelection.height);
    }
  }

  @Override
  public void addSelectedCellRange(int x1, int y1, int x2, int y2) {
    Rectangle prevSelection = getSelectionBoundingBox();
    switch (selectionMode) {
    case SINGLE_SELECTION:
      selectedCells.clear();
      selectedCells.add(new Point(x2, y2));
      break;
    case RANGE_SELECTION:
      selectedCells.clear(); // NOSONAR (--> forward to MULTIPLE_SELECTION)
    case MULTIPLE_SELECTION:
      for (int y = y1; y <= y2; y++) {
        for (int x = x1; x <= x2; x++) {
          selectedCells.add(new Point(x, y));
        }
      }
      break;
    case NO_SELECTION:
      selectedCells.clear();
      break;
    default:
    }
    lastSelection = new Rectangle(x1, y1, x2 - x1, y2 - y1);
    fireSelectionChanged(Math.min(x1, prevSelection.x),
        Math.min(y1, prevSelection.y),
        Math.max(x2, prevSelection.x + prevSelection.width),
        Math.max(y2, prevSelection.y + prevSelection.height));
  }

  @Override
  public void clearSelection() {
    Rectangle prevSelection = getSelectionBoundingBox();
    selectedCells.clear();
    lastSelection = null;
    fireSelectionChanged(prevSelection.x, prevSelection.y, prevSelection.x
        + prevSelection.width, prevSelection.y + prevSelection.height);
  }

  @Override
  public Rectangle getSelectionBoundingBox() {
    int x1 = Integer.MAX_VALUE;
    int y1 = Integer.MAX_VALUE;
    int x2 = Integer.MIN_VALUE;
    int y2 = Integer.MIN_VALUE;

    if (isSelectionEmpty()) {
      return new Rectangle(0, 0, 0, 0);
    }

    for (Point p : selectedCells) {
      x1 = Math.min(x1, p.x);
      x2 = Math.max(x2, p.x);
      y1 = Math.min(y1, p.y);
      y2 = Math.max(y2, p.y);
    }

    return new Rectangle(x1, y1, x2 - x1, y2 - y1);
  }

  @Override
  public boolean isSelected(int x, int y) {
    return selectedCells.contains(new Point(x, y));
  }

  @Override
  public boolean isSelectionEmpty() {
    return selectedCells.isEmpty();
  }

  @Override
  public void removeSelectedCellRange(int x1, int y1, int x2, int y2) {
    Rectangle prevSelection = getSelectionBoundingBox();
    for (int y = y1; y <= y2; y++) {
      for (int x = x1; x <= x2; x++) {
        selectedCells.remove(new Point(x, y));
      }
    }

    // check in case of RANGE_SELECTION if the selection is still a
    // range, the policy is that every thing of the range remains that
    // is left and above the removed selection
    if (selectionMode == RANGE_SELECTION) {
      for (int y = y1; y <= prevSelection.y + prevSelection.height; y++) {
        for (int x = prevSelection.x; x < x1; x++) {
          selectedCells.remove(new Point(x, y));
        }
      }

      for (int x = x1; x <= prevSelection.x + prevSelection.width; x++) {
        for (int y = prevSelection.y; y < y1; y++) {
          selectedCells.remove(new Point(x, y));
        }
      }
    }

    fireSelectionChanged(Math.min(x1, prevSelection.x),
        Math.min(y1, prevSelection.y),
        Math.max(x2, prevSelection.x + prevSelection.width),
        Math.max(y2, prevSelection.y + prevSelection.height));
  }

}
