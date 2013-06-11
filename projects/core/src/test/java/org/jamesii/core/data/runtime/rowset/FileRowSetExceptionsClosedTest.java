/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;

import org.jamesii.core.data.runtime.rowset.FileRowSet;

/**
 * Tests whether the {@link FileRowSet} methods throw exceptions when invoked on
 * a closed RowSet.
 * 
 * @author Johannes Rössel
 * 
 * @see FileRowSetFunctionalityTest
 * @see FileRowSetFunctionalityGetterUpdaterTest
 * @see FileRowSetFunctionalityInapplicableTest
 * @see FileRowSetExceptionsTest
 * @see FileRowSetExceptionsInsertRowTest
 * @see FileRowSetExceptionsInapplicableTest
 * @see FileRowSetExceptionsTypeConcurrencyTest
 * @see FileRowSetExceptionsCurrentRowTest
 * @see FileRowSetExceptionsInvalidColumnTest
 */
public class FileRowSetExceptionsClosedTest extends AbstractFileRowSetTest {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    frs.close();
  }

  public void testAbsoluteThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.absolute(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testAfterLastThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.afterLast();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testBeforeFirstThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.beforeFirst();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testCancelRowUpdatesThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.cancelRowUpdates();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testClearWarningsThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.clearWarnings();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testFindColumnThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.findColumn("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testFirstThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.first();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetArrayIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getArray(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetArrayStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getArray("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetAsciiStreamIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getAsciiStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetAsciiStreamStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getAsciiStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getBigDecimal(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getBigDecimal("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalIntIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getBigDecimal(1, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalStringStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getBigDecimal("Column 1", 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBinaryStreamIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getBinaryStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBinaryStreamStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getBinaryStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBlobIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getBlob(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBlobStringThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getBlob("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBooleanIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getBoolean(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBooleanStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getBoolean("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetByteIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getByte(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetByteStringThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getByte("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBytesIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getBytes(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBytesStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getBytes("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetCharacterStreamIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getCharacterStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetCharacterStreamStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getCharacterStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetClobIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getClob(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetClobStringThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getClob("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetCursorNameThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getCursorName();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getDate(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateStringThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getDate("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateIntCalendarThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getDate(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateStringCalendarThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getDate("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDoubleIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getDouble(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDoubleStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getDouble("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetFloatIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getFloat(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetFloatStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getFloat("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetIntIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getInt(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetIntStringThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getInt("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetLongIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getLong(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetLongStringThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getLong("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNCharacterStreamIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getNCharacterStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNCharacterStreamStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getNCharacterStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNClobIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getNClob(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNClobStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getNClob("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNStringIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getNString(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNStringStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getNString("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetObjectIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getObject(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetObjectStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getObject("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetObjectIntMapThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getObject(1, (Map<String, Class<?>>) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetObjectStringMapThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getObject("Column 1", (Map<String, Class<?>>) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetRefIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getRef(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetRefStringThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getRef("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetRowThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getRow();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetShortIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getShort(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetShortStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getShort("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetSQLXMLIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getSQLXML(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetSQLXMLStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getSQLXML("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetStringIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getString(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetStringStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getString("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getTime(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeStringThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getTime("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeIntCalendarThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getTime(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeStringCalendarThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getTime("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getTimestamp(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getTimestamp("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampIntCalendarThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getTimestamp(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampStringCalendarThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getTimestamp("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetUnicodeStreamIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getUnicodeStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetUnicodeStreamStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.getUnicodeStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetURLIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getURL(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetURLStringThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getURL("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetWarningsThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.getWarnings();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testInsertRowThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.insertRow();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testIsAfterLastThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.isAfterLast();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testIsBeforeFirstThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.isBeforeFirst();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testIsFirstThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.isFirst();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testIsLastThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.isLast();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testLastThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.last();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testMoveToCurrentRowThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.moveToCurrentRow();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testMoveToInsertRowThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.moveToInsertRow();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testNextThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.next();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testPreviousThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.previous();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testRefreshRowThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.refreshRow();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testRelativeIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.relative(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testRowDeletedThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.rowDeleted();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testRowInsertedThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.rowInserted();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testRowUpdatedThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.rowUpdated();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateArrayIntArrayThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateArray(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateArrayStringArrayThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateArray("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateAsciiStream(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateAsciiStream("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateAsciiStream(1, null, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateAsciiStream("Column 1", null, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateAsciiStream(1, null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateAsciiStream("Column 1", null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBigDecimalIntBigDecimalThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBigDecimal(1, BigDecimal.ONE);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBigDecimalStringBigDecimalThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBigDecimal("Column 1", BigDecimal.ONE);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobIntBlobThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBlob(1, (Blob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobStringBlobThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBlob("Column 1", (Blob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobIntInputStreamThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBlob(1, (InputStream) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobStringInputStreamThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBlob("Column 1", (InputStream) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobIntInputStreamLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBlob(1, null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobStringInputStreamLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBlob("Column 1", null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBooleanIntBooleanThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBoolean(1, false);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBooleanStringBooleanThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBoolean("Column 1", false);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateByteIntByteThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateByte(1, (byte) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateByteStringByteThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateByte("Column 1", (byte) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBytesIntByteArrayThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBytes(1, new byte[] {});
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBytesStringByteArrayThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateBytes("Column 1", new byte[] {});
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateCharacterStream(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateCharacterStream("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateCharacterStream(1, null, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateCharacterStream("Column 1", null, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateCharacterStream(1, null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateCharacterStream("Column 1", null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobIntClobThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateClob(1, (Clob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobStringClobThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateClob("Column 1", (Clob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobIntReaderThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateClob(1, (Reader) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobStringReaderThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateClob("Column 1", (Reader) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobIntReaderLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateClob(1, null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobStringReaderLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateClob("Column 1", null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDateIntDateThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateDate(1, new Date(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDateStringDateThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateDate("Column 1", new Date(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDoubleIntDoubleThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateDouble(1, 0d);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDoubleStringDoubleThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateDouble("Column 1", 0d);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateFloatIntFloatThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateFloat(1, 0f);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateFloatStringFloatThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateFloat("Column 1", 0f);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateIntIntIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateInt(1, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateIntStringIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateInt("Column 1", 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateLongIntLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateLong(1, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateLongStringLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateLong("Column 1", 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamIntReaderThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNCharacterStream(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamStringReaderThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNCharacterStream("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamIntReaderLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNCharacterStream(1, null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamStringReaderLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNCharacterStream("Column 1", null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobIntNClobThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNClob(1, (NClob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobStringNClobThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNClob("Column 1", (NClob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobIntReaderThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNClob(1, (Reader) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobStringReaderThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNClob("Column 1", (Reader) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobIntReaderLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNClob(1, null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobStringReaderLongThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNClob("Column 1", null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNStringIntStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNString(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNStringStringStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNString("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNullIntThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.updateNull(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNullStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateNull("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectIntObjectThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateObject(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectStringObjectThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateObject("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectIntObjectIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateObject(1, null, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectStringObjectIntThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateObject("Column 1", null, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRefIntRefThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateRef(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRefStringRefThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateRef("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRowThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.updateRow();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRowIdIntRowIdThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateRowId(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRowIdStringRowIdThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateRowId("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateShortIntShortThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateShort(1, (short) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateShortStringShortThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateShort("Column 1", (short) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateSQLXMLIntSQLXMLThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateSQLXML(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateSQLXMLStringSQLXMLThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateSQLXML("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateStringIntStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateString(1, "");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateStringStringStringThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateString("Column 1", "");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimeIntTimeThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateTime(1, new Time(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimeStringTimeThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateTime("Column 1", new Time(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimestampIntTimestampThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateTimestamp(1, new Timestamp(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimestampStringTimestampThrowsExceptionOnClosedRowSet()
      throws Exception {
    try {
      frs.updateTimestamp("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testWasNullThrowsExceptionOnClosedRowSet() throws Exception {
    try {
      frs.wasNull();
      fail();
    } catch (SQLException e) {
    }
  }

}
