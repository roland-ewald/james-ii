/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Basic class that provides selection capabilities for the supplied grid.
 * Select/Deselect cells by just clicking on them. By dragging the mouse over
 * the grid with the left mouse button down all visited cells become selected
 * (depending on selection model policy). While dragging and holding the SHIFT
 * button pressed the visited cells will be unselected. If pressing CTRL and
 * left mouse down a rectangular range can be selected. If you hold SHIFT + CTRL
 * a rectangular range will be deselected.
 * 
 * @author Stefan Rybacki
 */
public final class GridSelectionMetaphor extends MouseAdapter {

  /**
   * The grid the selector is generated for.
   */
  private Grid2D grid;

  /**
   * Flag indicating whether cells are currently selecting if dragged.
   */
  private boolean isSelecting = false;

  /**
   * Flag indicating whether range selection is on
   */
  private boolean isRangeSelecting;

  /**
   * A {@link Set} of previously (that means before starting to drag) selected
   * cell coordinates need in range selection mode
   */
  private Set<Point> prevSelection;

  /**
   * The point where the drag started
   */
  private Point dragStart;

  /**
   * Hidden constructor
   * 
   * @param grid
   *          the {@link Grid2D} that should be extended for selection
   *          functionality
   */
  private GridSelectionMetaphor(Grid2D grid) {
    this.grid = grid;
    grid.addMouseListener(this);
    grid.addMouseMotionListener(this);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    Point cell = grid.getCellForCoordinates(e.getX(), e.getY());
    if (isSelecting && !isRangeSelecting) {
      int onmask = InputEvent.SHIFT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK;
      int offmask = InputEvent.CTRL_DOWN_MASK;
      if ((e.getModifiersEx() & (onmask | offmask)) == onmask) {
        grid.getSelectionModel().removeSelectedCellRange(cell.x, cell.y,
            cell.x, cell.y);
      } else {
        grid.getSelectionModel().addSelectedCellRange(cell.x, cell.y, cell.x,
            cell.y);
      }
    }

    if (isSelecting && isRangeSelecting) {
      // restore old selection
      grid.getSelectionModel().clearSelection();
      for (Point p : prevSelection) {
        grid.getSelectionModel().addSelectedCellRange(p.x, p.y, p.x, p.y);
      }

      Point cell2 = grid.getCellForCoordinates(dragStart.x, dragStart.y);

      // if SHIFT button is pressed remove selected range else add range
      // selection
      if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) > 0) {
        grid.getSelectionModel().removeSelectedCellRange(
            Math.min(cell.x, cell2.x), Math.min(cell.y, cell2.y),
            Math.max(cell.x, cell2.x), Math.max(cell.y, cell2.y));
      } else {
        grid.getSelectionModel().addSelectedCellRange(
            Math.min(cell.x, cell2.x), Math.min(cell.y, cell2.y),
            Math.max(cell.x, cell2.x), Math.max(cell.y, cell2.y));
      }
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    Point cell = grid.getCellForCoordinates(e.getX(), e.getY());

    if (grid.getSelectionModel().isSelected(cell.x, cell.y)) {
      grid.getSelectionModel().removeSelectedCellRange(cell.x, cell.y, cell.x,
          cell.y);
    } else {
      grid.getSelectionModel().addSelectedCellRange(cell.x, cell.y, cell.x,
          cell.y);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    dragStart = new Point(e.getX(), e.getY());
    if (e.getButton() == MouseEvent.BUTTON1) {
      isSelecting = true;
    }
    // check whether STRG is pressed (if so range selection is enabled)
    if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) > 0) {
      // also store current selection to restore it each time a new range is
      // selected
      Rectangle bb = grid.getSelectionModel().getSelectionBoundingBox();
      prevSelection = new HashSet<>();
      for (int y = bb.y; y <= bb.y + bb.height; y++) {
        for (int x = bb.x; x <= bb.x + bb.width; x++) {
          if (grid.getSelectionModel().isSelected(x, y)) {
            prevSelection.add(new Point(x, y));
          }
        }
      }
      isRangeSelecting = true;
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    isSelecting = false;
    isRangeSelecting = false;
  }

  /**
   * Creates the selection metaphor for the specified {@link Grid2D}.
   * 
   * @param grid
   *          the grid to create the selection metaphor for
   */
  public static final void createSelectionMetaphorFor(Grid2D grid) {
    new GridSelectionMetaphor(grid);
  }
}
