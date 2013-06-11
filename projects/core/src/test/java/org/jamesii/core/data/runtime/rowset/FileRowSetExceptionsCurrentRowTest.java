/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

import org.jamesii.core.data.runtime.rowset.FileRowSet;

/**
 * Tests whether numerous {@link FileRowSet} methods that require the cursor to
 * be on a current row throw exceptions if the cursor is not. Most notably this
 * includes getters and updaters trying to access a row when the cursor is
 * before the first or after the last row.
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
 * @see FileRowSetExceptionsInvalidColumnTest
 */
public class FileRowSetExceptionsCurrentRowTest extends AbstractFileRowSetTest {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    load(small);
  }

  public void testGetBigDecimalIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getBigDecimal(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getBigDecimal(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalStringThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.getBigDecimal("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalStringThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getBigDecimal("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBlobIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getBlob(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBlobIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getBlob(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBlobStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getBlob("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBlobStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getBlob("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBooleanIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getBoolean(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBooleanIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getBoolean(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBooleanStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getBoolean("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBooleanStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getBoolean("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetByteIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getByte(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetByteIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getByte(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetByteStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getByte("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetByteStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getByte("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBytesIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getBytes(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBytesIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getBytes(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBytesStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getBytes("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBytesStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getBytes("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetClobIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getClob(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetCharacterStreamIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.getCharacterStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetCharacterStreamIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getCharacterStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetCharacterStreamStringThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.getCharacterStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetCharacterStreamStringThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getCharacterStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetClobIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getClob(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetClobStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getClob("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetClobStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getClob("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getDate(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getDate(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getDate("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getDate("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateIntCalendarThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getDate(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateIntCalendarThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getDate(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateStringCalendarThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.getDate("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateStringCalendarThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getDate("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDoubleIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getDouble(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDoubleIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getDouble(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDoubleStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getDouble("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDoubleStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getDouble("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetFloatIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getFloat(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetFloatIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getFloat(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetFloatStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getFloat("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetFloatStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getFloat("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetIntIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getInt(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetIntIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getInt(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetIntStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getInt("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetIntStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getInt("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetLongIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getLong(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetLongIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getLong(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetLongStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getLong("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetLongStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getLong("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNCharacterStreamIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.getNCharacterStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNCharacterStreamIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getNCharacterStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNCharacterStreamStringThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.getNCharacterStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNCharacterStreamStringThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getNCharacterStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNClobIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getNClob(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNClobIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getNClob(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNClobStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getNClob("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNClobStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getNClob("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNStringIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getNString(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNStringIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getNString(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNStringStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getNString("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNStringStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getNString("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetObjectIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getObject(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetObjectIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getObject(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetObjectStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getObject("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetObjectStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getObject("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetObjectIntMapThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getObject(1, new HashMap<String, Class<?>>());
    } catch (SQLException e) {
    }
  }

  public void testGetObjectIntMapThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getObject(1, new HashMap<String, Class<?>>());
    } catch (SQLException e) {
    }
  }

  public void testGetObjectStringMapThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getObject("Column 1", new HashMap<String, Class<?>>());
    } catch (SQLException e) {
    }
  }

  public void testGetObjectStringMapThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getObject("Column 1", new HashMap<String, Class<?>>());
    } catch (SQLException e) {
    }
  }

  public void testGetRefIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getRef(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetRefIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getRef(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetRefStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getRef("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetRefStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getRef("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetRowIdIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getRowId(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetRowIdIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getRowId(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetRowIdStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getRowId("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetRowIdStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getRowId("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetShortIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getShort(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetShortIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getShort(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetShortStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getShort("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetShortStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getShort("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetSQLXMLIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getSQLXML(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetSQLXMLIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getSQLXML(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetSQLXMLStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getSQLXML("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetSQLXMLStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getSQLXML("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetStringIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getString(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetStringIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getString(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetStringStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getString("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetStringStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getString("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getTime(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getTime(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getTime("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getTime("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getTimestamp(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getTimestamp(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getTimestamp("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampStringThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getTimestamp("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetUnicodeStreamIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.getUnicodeStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetUnicodeStreamIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getUnicodeStream(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetUnicodeStreamStringThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.getUnicodeStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetUnicodeStreamStringThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.getUnicodeStream("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetURLIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getURL(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetURLIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getURL(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetURLStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.getURL("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetURLStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.getURL("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateAsciiStream(1, new ByteArrayInputStream(new byte[] { 72 }));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateAsciiStream(1, new ByteArrayInputStream(new byte[] { 72 }));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateAsciiStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateAsciiStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateAsciiStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateAsciiStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateAsciiStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateAsciiStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateAsciiStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateAsciiStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateAsciiStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateAsciiStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBigDecimalIntBigDecimalThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBigDecimal(1, BigDecimal.ONE);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBigDecimalIntBigDecimalThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBigDecimal(1, BigDecimal.ONE);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBigDecimalStringBigDecimalThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBigDecimal("Column 1", BigDecimal.ONE);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBigDecimalStringBigDecimalThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBigDecimal("Column 1", BigDecimal.ONE);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobIntBlobThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateBlob(1, (Blob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobIntBlobThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBlob(1, (Blob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobStringBlobThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBlob("Column 1", (Blob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobStringBlobThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBlob("Column 1", (Blob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobIntInputStreamThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBlob(1, (InputStream) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobIntInputStreamThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBlob(1, (InputStream) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobStringInputStreamThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBlob("Column 1", (InputStream) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobStringInputStreamThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBlob("Column 1", (InputStream) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobIntInputStreamLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBlob(1, null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobIntInputStreamLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBlob(1, null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobStringInputStreamLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBlob("Column 1", null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBlobStringInputStreamLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBlob("Column 1", null, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBooleanIntBooleanThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBoolean(1, false);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBooleanIntBooleanThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBoolean(1, false);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBooleanStringBooleanThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBoolean("Column 1", false);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBooleanStringBooleanThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBoolean("Column 1", false);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateByteIntByteThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateByte(1, (byte) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateByteIntByteThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateByte(1, (byte) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateByteStringByteThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateByte("Column 1", (byte) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateByteStringByteThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateByte("Column 1", (byte) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBytesIntByteArrayThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBytes(1, new byte[] {});
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBytesIntByteArrayThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBytes(1, new byte[] {});
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBytesStringByteArrayThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateBytes("Column 1", new byte[] {});
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateBytesStringByteArrayThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateBytes("Column 1", new byte[] {});
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateCharacterStream(1, new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateCharacterStream(1, new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateCharacterStream("Column 1", new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateCharacterStream("Column 1", new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateCharacterStream(1, new StringReader(""), 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateCharacterStream(1, new StringReader(""), 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateCharacterStream("Column 1", new StringReader(""), 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateCharacterStream("Column 1", new StringReader(""), 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateCharacterStream(1, new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateCharacterStream(1, new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateCharacterStream("Column 1", new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateCharacterStream("Column 1", new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobIntClobThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateClob(1, (Clob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobIntClobThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateClob(1, (Clob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobStringClobThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateClob("Column 1", (Clob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobStringClobThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateClob("Column 1", (Clob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobIntReaderThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateClob(1, new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobIntReaderThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateClob(1, new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobStringReaderThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateClob("Column 1", new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobStringReaderThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateClob("Column 1", new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobIntReaderLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateClob(1, new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobIntReaderLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateClob(1, new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobStringReaderLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateClob("Column 1", new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateClobStringReaderLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateClob("Column 1", new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDateIntDateThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateDate(1, new Date(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDateIntDateThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateDate(1, new Date(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDateStringDateThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateDate("Column 1", new Date(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDateStringDateThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateDate("Column 1", new Date(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDoubleIntDoubleThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateDouble(1, 0d);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDoubleIntDoubleThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateDouble(1, 0d);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDoubleStringDoubleThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateDouble("Column 1", 0d);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateDoubleStringDoubleThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateDouble("Column 1", 0d);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateFloatIntFloatThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateFloat(1, 0f);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateFloatIntFloatThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateFloat(1, 0f);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateFloatStringFloatThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateFloat("Column 1", 0f);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateFloatStringFloatThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateFloat("Column 1", 0f);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateIntIntIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateInt(1, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateIntIntIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.updateInt(1, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateIntStringIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateInt("Column 1", 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateIntStringIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateInt("Column 1", 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateLongIntLongThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateLong(1, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateLongIntLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateLong(1, 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateLongStringLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateLong("Column 1", 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateLongStringLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateLong("Column 1", 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamIntReaderThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNCharacterStream(1, new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamIntReaderThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNCharacterStream(1, new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamStringReaderThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNCharacterStream("Column 1", new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamStringReaderThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNCharacterStream("Column 1", new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamIntReaderLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNCharacterStream(1, new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamIntReaderLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNCharacterStream(1, new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamStringReaderLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNCharacterStream("Column 1", new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNCharacterStreamStringReaderLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNCharacterStream("Column 1", new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobIntNClobThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNClob(1, (NClob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobIntNClobThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNClob(1, (NClob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobStringNClobThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNClob("Column 1", (NClob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobStringNClobThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNClob("Column 1", (NClob) null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobIntReaderThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNClob(1, new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobIntReaderThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNClob(1, new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobStringReaderThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNClob("Column 1", new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobStringReaderThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNClob("Column 1", new StringReader(""));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobIntReaderLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNClob(1, new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobIntReaderLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNClob(1, new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobStringReaderLongThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNClob("Column 1", new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNClobStringReaderLongThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNClob("Column 1", new StringReader(""), 0L);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNStringIntStringThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNString(1, "");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNStringIntStringThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNString(1, "");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNStringStringStringThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateNString("Column 1", "");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNStringStringStringThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNString("Column 1", "");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNullIntThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateNull(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNullIntThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNull(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNullStringThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateNull("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateNullStringThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.updateNull("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectIntObjectThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateObject(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectIntObjectThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateObject(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectStringObjectThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateObject("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectStringObjectThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateObject("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectIntObjectIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateObject(1, null, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectIntObjectIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateObject(1, null, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectStringObjectIntThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateObject("Column 1", null, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateObjectStringObjectIntThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateObject("Column 1", null, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRefIntRefThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateRef(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRefIntRefThrowsExceptionBeforeFirst() throws Exception {
    frs.beforeFirst();
    try {
      frs.updateRef(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRefStringRefThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateRef("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRefStringRefThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateRef("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRowIdIntRowIdThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateRowId(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRowIdIntRowIdThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateRowId(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRowIdStringRowIdThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateRowId("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateRowIdStringRowIdThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateRowId("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateShortIntShortThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateShort(1, (short) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateShortIntShortThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateShort(1, (short) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateShortStringShortThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateShort("Column 1", (short) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateShortStringShortThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateShort("Column 1", (short) 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateSQLXMLIntSQLXMLThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateSQLXML(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateSQLXMLIntSQLXMLThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateSQLXML(1, null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateSQLXMLStringSQLXMLThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateSQLXML("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateSQLXMLStringSQLXMLThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateSQLXML("Column 1", null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateStringIntStringThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateString(1, "");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateStringIntStringThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateString(1, "");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateStringStringStringThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateString("Column 1", "");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateStringStringStringThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateString("Column 1", "");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimeIntTimeThrowsExceptionAfterLast() throws Exception {
    frs.afterLast();
    try {
      frs.updateTime(1, new Time(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimeIntTimeThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateTime(1, new Time(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimeStringTimeThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateTime("Column 1", new Time(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimeStringTimeThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateTime("Column 1", new Time(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimestampIntTimestampThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateTimestamp(1, new Timestamp(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimestampIntTimestampThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateTimestamp(1, new Timestamp(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimestampStringTimestampThrowsExceptionAfterLast()
      throws Exception {
    frs.afterLast();
    try {
      frs.updateTimestamp("Column 1", new Timestamp(0));
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateTimestampStringTimestampThrowsExceptionBeforeFirst()
      throws Exception {
    frs.beforeFirst();
    try {
      frs.updateTimestamp("Column 1", new Timestamp(0));
      fail();
    } catch (SQLException e) {
    }
  }

}
