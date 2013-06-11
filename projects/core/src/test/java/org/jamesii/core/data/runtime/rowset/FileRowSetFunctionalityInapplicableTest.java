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
 * Tests whether the inapplicable methods in the {@link FileRowSet} class return
 * their intended return values. This is mostly {@code null} or {@code false}.
 * 
 * @author Johannes Rössel
 * 
 * @see FileRowSetFunctionalityTest
 * @see FileRowSetFunctionalityGetterUpdaterTest
 * @see FileRowSetExceptionsTest
 * @see FileRowSetExceptionsClosedTest
 * @see FileRowSetExceptionsInsertRowTest
 * @see FileRowSetExceptionsInapplicableTest
 * @see FileRowSetExceptionsTypeConcurrencyTest
 * @see FileRowSetExceptionsCurrentRowTest
 * @see FileRowSetExceptionsInvalidColumnTest
 */
public class FileRowSetFunctionalityInapplicableTest extends
    AbstractFileRowSetTest {

  public void testClearWarningsDoesNotThrowException() throws Exception {
    load(small);
    frs.clearWarnings();
  }

  public void testGetCommandReturnsNull() {
    assertNull(frs.getCommand());
  }

  public void testGetCursorNameReturnsEmptyString() throws Exception {
    load(small);
    assertEquals("", frs.getCursorName());
  }

  public void testGetParamsReturnsEmptyArray() throws Exception {
    assertEquals(0, frs.getParams().length);
  }

  public void testGetPasswordReturnsNull() {
    assertNull(frs.getPassword());
  }

  public void testGetStatementReturnsNull() throws Exception {
    assertNull(frs.getStatement());
  }

  public void testGetUsernameReturnsNull() {
    assertNull(frs.getUsername());
  }

  public void testGetWarningsReturnsNull() throws Exception {
    assertNull(frs.getWarnings());
  }

  public void testGetUrlReturnsNull() throws SQLException {
    assertNull(frs.getUrl());
  }

  public void testIsWrapperForReturnsFalse() throws Exception {
    assertFalse(frs.isWrapperFor(null));
    assertFalse(frs.isWrapperFor(FileRowSet.class));
  }

  public void testRowDeletedReturnsFalse() throws Exception {
    load(small);
    assertFalse(frs.rowDeleted());
    frs.next();
    assertFalse(frs.rowDeleted());
    frs.absolute(5);
    assertFalse(frs.rowDeleted());
    frs.last();
    assertFalse(frs.rowDeleted());
    frs.afterLast();
    assertFalse(frs.rowDeleted());
  }

  public void testRowInsertedReturnsFalse() throws Exception {
    load(small);
    assertFalse(frs.rowInserted());
    frs.next();
    assertFalse(frs.rowInserted());
    frs.absolute(5);
    assertFalse(frs.rowInserted());
    frs.last();
    assertFalse(frs.rowInserted());
    frs.afterLast();
    assertFalse(frs.rowInserted());
  }

  public void testRowUpdatedReturnsFalse() throws Exception {
    load(small);
    assertFalse(frs.rowUpdated());
    frs.next();
    assertFalse(frs.rowUpdated());
    frs.absolute(5);
    assertFalse(frs.rowUpdated());
    frs.last();
    assertFalse(frs.rowUpdated());
    frs.afterLast();
    assertFalse(frs.rowUpdated());
  }

  public void testWasNullReturnsFalse() throws Exception {
    load(small);
    assertFalse(frs.wasNull());
    frs.next();
    frs.getInt(1);
    assertFalse(frs.wasNull());
  }

}
