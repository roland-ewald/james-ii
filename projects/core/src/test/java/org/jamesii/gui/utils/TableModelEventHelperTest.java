/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import static org.jamesii.gui.utils.TableModelEventHelper.*;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jamesii.gui.utils.TableModelEventHelper.TableModelEventType;

import junit.framework.TestCase;

public class TableModelEventHelperTest extends TestCase {

  public void testGetEventType() {
    TableModel tm = new DefaultTableModel(4, 4);

    assertEquals(TableModelEventType.TABLE_DATA_CHANGED,
        getEventType(new TableModelEvent(tm)));
    assertEquals(TableModelEventType.TABLE_STRUCTURE_CHANGED,
        getEventType(new TableModelEvent(tm, TableModelEvent.HEADER_ROW)));
    assertEquals(TableModelEventType.ROWS_INSERTED,
        getEventType(new TableModelEvent(tm, 1, 3, TableModelEvent.ALL_COLUMNS,
            TableModelEvent.INSERT)));
    assertEquals(TableModelEventType.ROWS_INSERTED,
        getEventType(new TableModelEvent(tm, 1, 1, TableModelEvent.ALL_COLUMNS,
            TableModelEvent.INSERT)));
    assertEquals(TableModelEventType.ROWS_UPDATED,
        getEventType(new TableModelEvent(tm, 0, 1, TableModelEvent.ALL_COLUMNS,
            TableModelEvent.UPDATE)));
    assertEquals(TableModelEventType.ROWS_UPDATED,
        getEventType(new TableModelEvent(tm, 0, 0, TableModelEvent.ALL_COLUMNS,
            TableModelEvent.UPDATE)));
    assertEquals(TableModelEventType.ROWS_DELETED,
        getEventType(new TableModelEvent(tm, 1, 2, TableModelEvent.ALL_COLUMNS,
            TableModelEvent.DELETE)));
    assertEquals(TableModelEventType.ROWS_DELETED,
        getEventType(new TableModelEvent(tm, 2, 2, TableModelEvent.ALL_COLUMNS,
            TableModelEvent.DELETE)));
    assertEquals(TableModelEventType.CELL_UPDATED,
        getEventType(new TableModelEvent(tm, 3, 3, 2, TableModelEvent.UPDATE)));
  }

}
