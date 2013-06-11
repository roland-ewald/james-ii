/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import javax.sql.RowSet;

/**
 * Tests whether {@link RowSet} methods that require a specific type (forward
 * only) or concurrency (read-only) throw exceptions when they cannot be
 * invoked.
 * 
 * @author Johannes Rössel
 * 
 * @see FileRowSetFunctionalityTest
 * @see FileRowSetFunctionalityGetterUpdaterTest
 * @see FileRowSetFunctionalityInapplicableTest
 * @see FileRowSetExceptionsTest
 * @see FileRowSetExceptionsInsertRowTest
 * @see FileRowSetExceptionsClosedTest
 * @see FileRowSetExceptionsInapplicableTest
 * @see FileRowSetExceptionsCurrentRowTest
 * @see FileRowSetExceptionsInvalidColumnTest
 */
public class FileRowSetExceptionsTypeConcurrencyTest extends
    AbstractFileRowSetTest {

  private void loadReadOnly(File f) {
    try {
      frs.setConcurrency(ResultSet.CONCUR_READ_ONLY);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    load(f);
  }

  private void loadForwardOnly(File f) {
    try {
      frs.setType(ResultSet.TYPE_FORWARD_ONLY);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    load(f);
  }

  public void testAbsoluteThrowsExceptionOnForwardOnlyRowSet() throws Exception {
    loadForwardOnly(small);
    try {
      frs.absolute(5);
    } catch (SQLException e) {
    }
  }

  public void testAbsoluteDoesNotThrowExceptionOnScrollingRowSet()
      throws Exception {
    load(small);
    frs.absolute(5);
  }

  public void testAfterLastThrowsExceptionOnForwardOnlyRowSet()
      throws Exception {
    loadForwardOnly(small);
    try {
      frs.afterLast();
    } catch (SQLException e) {
    }
  }

  public void testAfterLastDoesNotThrowExceptionOnScrollingRowSet()
      throws Exception {
    load(small);
    frs.afterLast();
  }

  public void testBeforeFirstThrowsExceptionOnForwardOnlyRowSet()
      throws Exception {
    loadForwardOnly(small);
    try {
      frs.beforeFirst();
    } catch (SQLException e) {
    }
  }

  public void testBeforeFirstDoesNotThrowExceptionOnScrollingRowSet()
      throws Exception {
    load(small);
    frs.beforeFirst();
  }

  public void testFirstThrowsExceptionOnForwardOnlyRowSet() throws Exception {
    loadForwardOnly(small);
    try {
      frs.first();
    } catch (SQLException e) {
    }
  }

  public void testFirstDoesNotThrowExceptionOnScrollingRowSet()
      throws Exception {
    load(small);
    frs.first();
  }

  public void testInsertRowThrowsExceptionOnReadOnlyRowSet() throws Exception {
    loadReadOnly(createTestFile("insertTest", 5));
    try {
      frs.moveToInsertRow();
      frs.updateInt(1, 1);
      frs.updateInt(2, 1);
      frs.updateString(3, "foo");
      frs.insertRow();
    } catch (SQLException e) {
    }
  }

  public void testInsertRowDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(createTestFile("insertTest", 5));
    frs.moveToInsertRow();
    frs.updateInt(1, 1);
    frs.updateInt(2, 1);
    frs.updateString(3, "foo");
    frs.insertRow();
  }

  public void testLastThrowsExceptionOnForwardOnlyRowSet() throws Exception {
    loadForwardOnly(small);
    try {
      frs.last();
    } catch (SQLException e) {
    }
  }

  public void testLastDoesNotThrowExceptionOnScrollingRowSet() throws Exception {
    load(small);
    frs.last();
  }

  public void testPreviousThrowsExceptionOnForwardOnlyRowSet() throws Exception {
    loadForwardOnly(small);
    frs.next();
    frs.next();
    try {
      frs.previous();
    } catch (SQLException e) {
    }
  }

  public void testPreviousDoesNotThrowExceptionOnScrollingRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.next();
    frs.previous();
  }

  public void testRefreshRowThrowsExceptionOnForwardOnlyRowSet()
      throws Exception {
    loadForwardOnly(small);
    frs.next();
    try {
      frs.refreshRow();
    } catch (SQLException e) {
    }
  }

  public void testRefreshRowDoesNotThrowExceptionOnScrollingRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.refreshRow();
  }

  public void testRelativeThrowsExceptionOnForwardOnlyRowSet() throws Exception {
    loadForwardOnly(small);
    try {
      frs.relative(2);
    } catch (SQLException e) {
    }
  }

  public void testRelativeDoesNotThrowExceptionOnScrollingRowSet()
      throws Exception {
    load(small);
    frs.relative(2);
  }

  public void testUpdateBigDecimalIntBigDecimalThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateBigDecimal(1, BigDecimal.ONE);
    } catch (SQLException e) {
    }
  }

  public void testUpdateBigDecimalIntBigDecimalDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateBigDecimal(1, BigDecimal.ONE);
  }

  public void testUpdateBigDecimalStringBigDecimalThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateBigDecimal("Column 1", null);
    } catch (SQLException e) {
    }
  }

  public void testUpdateBigDecimalStringBigDecimalDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateBigDecimal("Column 1", BigDecimal.ONE);
  }

  public void testUpdateBooleanIntBooleanThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateBoolean(1, false);
    } catch (SQLException e) {
    }
  }

  public void testUpdateBooleanIntBooleanDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateBoolean(1, true);
  }

  public void testUpdateBooleanStringBooleanThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateBoolean("Column 1", false);
    } catch (SQLException e) {
    }
  }

  public void testUpdateBooleanStringBooleanDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateBoolean("Column 1", true);
  }

  public void testUpdateByteIntByteThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateByte(1, (byte) 0);
    } catch (SQLException e) {
    }
  }

  public void testUpdateByteIntByteDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateByte(1, (byte) 5);
  }

  public void testUpdateByteStringByteThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateByte("Column 1", (byte) 0);
    } catch (SQLException e) {
    }
  }

  public void testUpdateByteStringByteDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateByte("Column 1", (byte) 5);
  }

  public void testUpdateBytesIntByteArrayThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateBytes(1, new byte[] { 72 });
    } catch (SQLException e) {
    }
  }

  public void testUpdateBytesIntByteArrayDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateBytes(1, new byte[] { 72 });
  }

  public void testUpdateBytesStringByteArrayThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateBytes("Column 1", new byte[] { 72 });
    } catch (SQLException e) {
    }
  }

  public void testUpdateBytesStringByteArrayDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateBytes("Column 1", new byte[] { 72 });

  }

  public void testUpdateDateIntDateThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateDate(1, new Date(0));
    } catch (SQLException e) {
    }
  }

  public void testUpdateDateIntDateDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateDate(1, new Date(0));
  }

  public void testUpdateDateStringDateThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateDate("Column 1", new Date(0));
    } catch (SQLException e) {
    }
  }

  public void testUpdateDateStringDateDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateDate("Column 1", new Date(0));
  }

  public void testUpdateDoubleIntDoubleThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateDouble(1, 0d);
    } catch (SQLException e) {
    }
  }

  public void testUpdateDoubleIntDoubleDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateDouble(1, 3.5);
  }

  public void testUpdateDoubleStringDoubleThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateDouble("Column 1", 0d);
    } catch (SQLException e) {
    }
  }

  public void testUpdateDoubleStringDoubleDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateDouble("Column 1", 3.5);
  }

  public void testUpdateFloatIntFloatThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateFloat(1, 0f);
    } catch (SQLException e) {
    }
  }

  public void testUpdateFloatIntFloatDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateFloat(1, 3.5f);
  }

  public void testUpdateFloatStringFloatThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateFloat("Column 1", 0f);
    } catch (SQLException e) {
    }
  }

  public void testUpdateFloatStringFloatDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateFloat("Column 1", 3.5f);
  }

  public void testUpdateIntIntIntThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateInt(1, 0);
    } catch (SQLException e) {
    }
  }

  public void testUpdateIntIntIntDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateInt(1, 7);
  }

  public void testUpdateIntStringIntThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateInt("Column 1", 0);
    } catch (SQLException e) {
    }
  }

  public void testUpdateIntStringIntDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateInt("Column 1", 7);
  }

  public void testUpdateLongIntLongThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateLong(1, 0L);
    } catch (SQLException e) {
    }
  }

  public void testUpdateLongIntLongDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateLong(1, 7L);
  }

  public void testUpdateLongStringLongThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateLong("Column 1", 0L);
    } catch (SQLException e) {
    }
  }

  public void testUpdateLongStringLongDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateLong("Column 1", 7L);
  }

  public void testUpdateNStringIntStringThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateNString(1, null);
    } catch (SQLException e) {
    }
  }

  public void testUpdateNStringIntStringDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateNString(1, "a");
  }

  public void testUpdateNStringStringStringThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateNString("Column 1", null);
    } catch (SQLException e) {
    }
  }

  public void testUpdateNStringStringStringDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateNString("Column 1", "a");
  }

  public void testUpdateShortIntShortThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateShort(1, (short) 0);
    } catch (SQLException e) {
    }
  }

  public void testUpdateShortIntShortDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateShort(1, (short) 7);
  }

  public void testUpdateShortStringShortThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateShort("Column 1", (short) 0);
    } catch (SQLException e) {
    }
  }

  public void testUpdateShortStringShortDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateShort("Column 1", (short) 7);
  }

  public void testUpdateStringIntStringThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateString(1, "");
    } catch (SQLException e) {
    }
  }

  public void testUpdateStringIntStringDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateString(1, "a");
  }

  public void testUpdateStringStringStringThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateString("Column 1", "");
    } catch (SQLException e) {
    }
  }

  public void testUpdateStringStringStringDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateString("Column 1", "a");
  }

  public void testUpdateTimeIntTimeThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateTime(1, new Time(0));
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimeIntTimeDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateTime(1, new Time(0));
  }

  public void testUpdateTimeStringTimeThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateTime("Column 1", new Time(0));
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimeStringTimeDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateTime("Column 1", new Time(0));
  }

  public void testUpdateTimestampIntTimestampThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateTimestamp(1, new Timestamp(0));
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimestampIntTimestampDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateTimestamp(1, new Timestamp(0));
  }

  public void testUpdateTimestampStringTimestampThrowsExceptionOnReadOnlyRowSet()
      throws Exception {
    loadReadOnly(small);
    frs.next();
    try {
      frs.updateTimestamp("Column 1", new Timestamp(0));
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimestampStringTimestampDoesNotThrowExceptionOnUpdatableRowSet()
      throws Exception {
    load(small);
    frs.next();
    frs.updateTimestamp("Column 1", new Timestamp(0));
  }

}
