package org.jamesii.gui.utils;

import javax.swing.event.TableModelEvent;

/** Helper class for handling {@link TableModelEvent}s. */
public final class TableModelEventHelper {

  /** The type of a {@link TableModelEvent}. */
  public enum TableModelEventType {
    /** A single cell has been updated. */
    CELL_UPDATED,
    /**
     * The complete table data has been changed, yet the columns remain
     * unchanged.
     */
    TABLE_DATA_CHANGED,
    /** The table's structure, that is, its columns, have changed. */
    TABLE_STRUCTURE_CHANGED,
    /** Additional rows have been inserted into the table. */
    ROWS_INSERTED,
    /** A number of rows have been updated. */
    ROWS_UPDATED,
    /** A number of rows have been deleted from the table. */
    ROWS_DELETED,
    /**
     * The type of even couldn't be determined. The
     * {@link #getEventType(TableModelEvent)} method tries to detect all events
     * that can be raised individually by calling the various <code>fire</code>
     * methods in {@link javax.swing.table.AbstractTableModel}, but there still
     * can be value combinations within a {@link TableModelEvent} which may be
     * nonsense or unrecognisable.
     */
    NOT_SURE
  }

  /** Private default constructor to prevent instantiation. */
  private TableModelEventHelper() {
  }

  /**
   * Tries to determine the type of the given {@link TableModelEvent}. While a
   * {@link TableModelEvent} can be very fine-grained it isn't exactly trivial
   * to extract that fine-grained information again.
   * 
   * @param e
   *          A {@link TableModelEvent}.
   * @return A member of {@link TableModelEventType} describing the type of the
   *         given event.
   */
  public static TableModelEventType getEventType(TableModelEvent e) {
    if (e.getType() == TableModelEvent.UPDATE
        && e.getColumn() == TableModelEvent.ALL_COLUMNS && e.getFirstRow() == 0
        && e.getLastRow() == Integer.MAX_VALUE) {
      return TableModelEventType.TABLE_DATA_CHANGED;
    }

    if (e.getType() == TableModelEvent.UPDATE
        && e.getColumn() == TableModelEvent.ALL_COLUMNS
        && e.getFirstRow() == TableModelEvent.HEADER_ROW
        && e.getLastRow() == TableModelEvent.HEADER_ROW) {
      return TableModelEventType.TABLE_STRUCTURE_CHANGED;
    }

    if (e.getType() == TableModelEvent.INSERT
        && e.getColumn() == TableModelEvent.ALL_COLUMNS) {
      return TableModelEventType.ROWS_INSERTED;
    }

    if (e.getType() == TableModelEvent.UPDATE
        && e.getColumn() == TableModelEvent.ALL_COLUMNS) {
      return TableModelEventType.ROWS_UPDATED;
    }

    if (e.getType() == TableModelEvent.DELETE
        && e.getColumn() == TableModelEvent.ALL_COLUMNS) {
      return TableModelEventType.ROWS_DELETED;
    }

    if (e.getType() == TableModelEvent.UPDATE) {
      return TableModelEventType.CELL_UPDATED;
    }

    return TableModelEventType.NOT_SURE;
  }
}
