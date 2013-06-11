/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.jamesii.core.data.runtime.rowset.FileRowSet;

/**
 * Tests whether {@link FileRowSet} methods that get a column index or label
 * throw exceptions if the index or label does not exist.
 * 
 * @author Johannes Rössel
 * 
 * @see FileRowSetFunctionalityTest
 * @see FileRowSetFunctionalityGetterUpdaterTest
 * @see FileRowSetFunctionalityInapplicableTest
 * @see FileRowSetExceptionsTest
 * @see FileRowSetExceptionsClosedTest
 * @see FileRowSetExceptionsInsertRowTest
 * @see FileRowSetExceptionsInapplicableTest
 * @see FileRowSetExceptionsTypeConcurrencyTest
 * @see FileRowSetExceptionsCurrentRowTest
 */
public class FileRowSetExceptionsInvalidColumnTest extends
    AbstractFileRowSetTest {

  public void testGetBigDecimalIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBigDecimal(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBigDecimalIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBigDecimal(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBigDecimalIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBigDecimal(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBigDecimalStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBigDecimal("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetBigDecimalIntIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBigDecimal(0, 0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBigDecimalIntIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBigDecimal(-1, 0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBigDecimalIntIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBigDecimal(frs.getMetaData().getColumnCount() + 1, 0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBigDecimalStringIntThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBigDecimal("blargh", 0);
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetBooleanIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBoolean(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBooleanIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBoolean(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBooleanIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBoolean(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBooleanStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBoolean("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetByteIntThrowsExceptionOnZeroColumnIndex() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getByte(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetByteIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getByte(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetByteIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getByte(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetByteStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getByte("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetBytesIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBytes(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBytesIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBytes(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBytesIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBytes(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetBytesStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBytes("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetDateIntThrowsExceptionOnZeroColumnIndex() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDate(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetDateIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDate(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetDateIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDate(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetDateStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDate("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetDateIntCalendarThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDate(0, Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetDateIntCalendarThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDate(-1, Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetDateIntCalendarThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDate(frs.getMetaData().getColumnCount() + 1,
          Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetDateStringCalendarThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDate("blargh", Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetDoubleIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDouble(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetDoubleIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDouble(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetDoubleIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDouble(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetDoubleStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getDouble("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetFloatIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getFloat(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetFloatIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getFloat(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetFloatIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getFloat(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetFloatStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getFloat("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetIntIntThrowsExceptionOnZeroColumnIndex() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getInt(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetIntIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getInt(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetIntIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getInt(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetIntStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getInt("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetLongIntThrowsExceptionOnZeroColumnIndex() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getLong(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetLongIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getLong(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetLongIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getLong(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetLongStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getLong("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetNStringIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getNString(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetNStringIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getNString(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetNStringIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getNString(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetNStringStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getNString("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetShortIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getShort(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetShortIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getShort(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetShortIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getShort(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetShortStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getShort("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetStringIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getString(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetStringIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getString(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetStringIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getString(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetStringStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getString("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetTimeIntThrowsExceptionOnZeroColumnIndex() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTime(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimeIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTime(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimeIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTime(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimeStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTime("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetTimeIntCalendarThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTime(0, Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimeIntCalendarThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTime(-1, Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimeIntCalendarThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTime(frs.getMetaData().getColumnCount() + 1,
          Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimeStringCalendarThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTime("blargh", Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetTimestampIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTimestamp(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimestampIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTimestamp(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimestampIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTimestamp(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimestampStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTimestamp("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetTimestampIntCalendarThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTimestamp(0, Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimestampIntCalendarThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTimestamp(-1, Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimestampIntCalendarThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTimestamp(frs.getMetaData().getColumnCount() + 1,
          Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetTimestampStringCalendarThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getTimestamp("blargh", Calendar.getInstance());
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testGetURLIntThrowsExceptionOnZeroColumnIndex() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getURL(0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetURLIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getURL(-1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetURLIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getURL(frs.getMetaData().getColumnCount() + 1);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testGetURLStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.getURL("blargh");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  // ======================================================
  // UPDATERS
  // ======================================================

  public void testUpdateBigDecimalIntBigDecimalThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBigDecimal(0, BigDecimal.TEN);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBigDecimalIntBigDecimalThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBigDecimal(-1, BigDecimal.TEN);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBigDecimalIntBigDecimalThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBigDecimal(frs.getMetaData().getColumnCount() + 1,
          BigDecimal.TEN);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBigDecimalStringBigDecimalThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBigDecimal("blargh", BigDecimal.TEN);
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBooleanIntBooleanThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBoolean(0, true);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBooleanIntBooleanThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBoolean(-1, true);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBooleanIntBooleanThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBoolean(frs.getMetaData().getColumnCount() + 1, true);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBooleanStringBooleanThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBoolean("blargh", true);
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateByteIntByteThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateByte(0, (byte) 0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateByteIntByteThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateByte(-1, (byte) 0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateByteIntByteThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateByte(frs.getMetaData().getColumnCount() + 1, (byte) 0);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateByteStringByteThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateByte("blargh", (byte) 0);
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBytesIntBytesThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBytes(0, new byte[] { 72 });
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBytesIntBytesThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBytes(-1, new byte[] { 72 });
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBytesIntBytesThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBytes(frs.getMetaData().getColumnCount() + 1, new byte[] { 72 });
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateBytesStringBytesThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBytes("blargh", new byte[] { 72 });
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateDateIntDateThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateDate(0, new Date(0));
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateDateIntDateThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateDate(-1, new Date(0));
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateDateIntDateThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateDate(frs.getMetaData().getColumnCount() + 1, new Date(0));
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateDateStringDateThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateDate("blargh", new Date(0));
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateDoubleIntDoubleThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateDouble(0, 3.5);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateDoubleIntDoubleThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateDouble(-1, 3.5);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateDoubleIntDoubleThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateDouble(frs.getMetaData().getColumnCount() + 1, 3.5);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateDoubleStringDoubleThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateDouble("blargh", 3.5);
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateFloatIntFloatThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateFloat(0, 3.5f);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateFloatIntFloatThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateFloat(-1, 3.5f);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateFloatIntFloatThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateFloat(frs.getMetaData().getColumnCount() + 1, 3.5f);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateFloatStringFloatThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateFloat("blargh", 3.5f);
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateIntIntIntThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateInt(0, 7);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateIntIntIntThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateInt(-1, 7);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateIntIntIntThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateInt(frs.getMetaData().getColumnCount() + 1, 7);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateIntStringIntThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateInt("blargh", 7);
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateLongIntLongThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateLong(0, 7L);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateLongIntLongThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateLong(-1, 7L);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateLongIntLongThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateLong(frs.getMetaData().getColumnCount() + 1, 7L);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateLongStringLongThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateLong("blargh", 7L);
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateNStringIntNStringThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNString(0, "a");
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateNStringIntNStringThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNString(-1, "a");
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateNStringIntNStringThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNString(frs.getMetaData().getColumnCount() + 1, "a");
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateNStringStringNStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNString("blargh", "a");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateShortIntShortThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateShort(0, (short) 5);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateShortIntShortThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateShort(-1, (short) 5);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateShortIntShortThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateShort(frs.getMetaData().getColumnCount() + 1, (short) 5);
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateShortStringShortThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateShort("blargh", (short) 5);
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateStringIntStringThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateString(0, "a");
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateStringIntStringThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateString(-1, "a");
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateStringIntStringThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateString(frs.getMetaData().getColumnCount() + 1, "a");
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateStringStringStringThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateString("blargh", "a");
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateTimeIntTimeThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateTime(0, new Time(0));
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateTimeIntTimeThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateTime(-1, new Time(0));
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateTimeIntTimeThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateTime(frs.getMetaData().getColumnCount() + 1, new Time(0));
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateTimeStringTimeThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateTime("blargh", new Time(0));
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testUpdateTimestampIntTimestampThrowsExceptionOnZeroColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateTimestamp(0, new Timestamp(0));
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateTimestampIntTimestampThrowsExceptionOnNegativeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateTimestamp(-1, new Timestamp(0));
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateTimestampIntTimestampThrowsExceptionOnTooLargeColumnIndex()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateTimestamp(frs.getMetaData().getColumnCount() + 1,
          new Timestamp(0));
    } catch (SQLException e) {
      assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
    }
  }

  public void testUpdateTimestampStringTimestampThrowsExceptionOnNotExistingColumnLabel()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateTimestamp("blargh", new Timestamp(0));
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

}
