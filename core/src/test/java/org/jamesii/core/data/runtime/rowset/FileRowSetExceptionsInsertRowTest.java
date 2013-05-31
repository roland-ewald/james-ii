/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.sql.SQLException;

import org.jamesii.core.data.runtime.rowset.FileRowSet;

/**
 * Tests whether numerous {@link FileRowSet} methods that either require the
 * cursor to be on the insert row throw exceptions if the cursor is not.
 * Similarly for the other case where a method requires the cursor to be on a
 * normal row.
 * 
 * @author Johannes Rössel
 * 
 * @see FileRowSetFunctionalityTest
 * @see FileRowSetFunctionalityGetterUpdaterTest
 * @see FileRowSetFunctionalityInapplicableTest
 * @see FileRowSetExceptionsTest
 * @see FileRowSetExceptionsClosedTest
 * @see FileRowSetExceptionsInapplicableTest
 * @see FileRowSetExceptionsTypeConcurrencyTest
 * @see FileRowSetExceptionsCurrentRowTest
 * @see FileRowSetExceptionsInvalidColumnTest
 */
public class FileRowSetExceptionsInsertRowTest extends AbstractFileRowSetTest {

  public void testAbsoluteThrowsExceptionOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    try {
      frs.absolute(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testAfterLastThrowsExceptionOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    try {
      frs.afterLast();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testBeforeFirstThrowsExceptionOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    try {
      frs.beforeFirst();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testCancelRowUpdatesThrowsExceptionOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    try {
      frs.cancelRowUpdates();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testFirstThrowsExceptionOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    try {
      frs.first();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testInsertRowThrowsExceptionWhenNotOnInsertRow() throws Exception {
    load(small);
    frs.next();
    frs.updateString(1, "");
    frs.updateString(2, "");
    frs.updateString(3, "");
    try {
      frs.insertRow();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testLastThrowsExceptionOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    try {
      frs.last();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testNextThrowsExceptionOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    try {
      frs.next();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testPreviousThrowsExceptionOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    try {
      frs.previous();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testRefreshRowThrowsExceptionWhenOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    try {
      frs.refreshRow();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testRelativeThrowsExceptionOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    try {
      frs.relative(1);
      fail();
    } catch (SQLException e) {
    }
  }

}
