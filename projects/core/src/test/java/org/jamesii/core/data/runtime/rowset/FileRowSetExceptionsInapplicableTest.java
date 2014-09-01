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
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;

import javax.sql.RowSet;

import org.jamesii.core.data.runtime.rowset.FileRowSet;

/**
 * Tests whether the {@link RowSet} methods that are inapplicable in
 * {@link FileRowSet} throw exceptions when invoked.
 * 
 * @author Johannes Rössel
 * 
 * @see FileRowSetFunctionalityTest
 * @see FileRowSetFunctionalityGetterUpdaterTest
 * @see FileRowSetFunctionalityInapplicableTest
 * @see FileRowSetExceptionsTest
 * @see FileRowSetExceptionsInsertRowTest
 * @see FileRowSetExceptionsClosedTest
 * @see FileRowSetExceptionsTypeConcurrencyTest
 * @see FileRowSetExceptionsCurrentRowTest
 * @see FileRowSetExceptionsInvalidColumnTest
 */
public class FileRowSetExceptionsInapplicableTest extends
    AbstractFileRowSetTest {

  public void testClearParametersThrowsException() throws Exception {
    try {
      frs.clearParameters();
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testDeleteRowThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.deleteRow();
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetArrayIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getArray(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetArrayStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getArray("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetAsciiStreamIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getAsciiStream(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetAsciiStreamStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getAsciiStream("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetBinaryStreamIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBinaryStream(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetBinaryStreamStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBinaryStream("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetBlobIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBlob(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetBlobStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getBlob("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetCharacterStreamIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getCharacterStream(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetCharacterStreamStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getCharacterStream("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetClobIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getClob(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetClobStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getClob("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetHoldabilityThrowsException() throws Exception {
    try {
      frs.getHoldability();
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testGetNCharacterStreamIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getNCharacterStream(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetNCharacterStreamStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getNCharacterStream("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetNClobIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getNClob(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetNClobStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getNClob("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetRefIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getRef(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetRefStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getRef("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetRowIdIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getRowId(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetRowIdStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getRowId("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetSQLXMLIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getSQLXML(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetSQLXMLStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getSQLXML("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetUnicodeStreamIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getUnicodeStream(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testGetUnicodeStreamStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.getUnicodeStream("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testSetArrayIntArrayThrowsException() throws Exception {
    try {
      frs.setArray(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetAsciiStreamIntInputStreamThrowsException()
      throws Exception {
    try {
      frs.setAsciiStream(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetAsciiStreamIntInputStreamIntThrowsException()
      throws Exception {
    try {
      frs.setAsciiStream(1, null, 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBigDecimalIntBigDecimalThrowsException() throws Exception {
    try {
      frs.setBigDecimal(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBinaryStreamIntInputStreamThrowsException()
      throws Exception {
    try {
      frs.setBinaryStream(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBinaryStreamIntInputStreamIntThrowsException()
      throws Exception {
    try {
      frs.setBinaryStream(1, null, 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBlobIntBlobThrowsException() throws Exception {
    try {
      frs.setBlob(1, (Blob) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBlobIntInputStreamThrowsException() throws Exception {
    try {
      frs.setBlob(1, (InputStream) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBlobIntinputStreamLongThrowsException() throws Exception {
    try {
      frs.setBlob(1, null, 0L);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBooleanIntBooleanThrowsException() throws Exception {
    try {
      frs.setBoolean(1, true);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetByteIntByteThrowsException() throws Exception {
    try {
      frs.setByte(1, (byte) 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBytesIntByteArrayThrowsException() throws Exception {
    try {
      frs.setBytes(1, new byte[] {});
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetCharacterStreamIntReaderThrowsException() throws Exception {
    try {
      frs.setCharacterStream(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetCharacterStreamIntReaderIntThrowsException()
      throws Exception {
    try {
      frs.setCharacterStream(1, null, 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetClobIntClobThrowsException() throws Exception {
    try {
      frs.setClob(1, (Clob) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetClobIntReaderThrowsException() throws Exception {
    try {
      frs.setClob(1, (Reader) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetClobIntReaderLongThrowsException() throws Exception {
    try {
      frs.setClob(1, null, 0L);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetDateIntDateThrowsException() throws Exception {
    try {
      frs.setDate(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetDateIntDateCalendarThrowsException() throws Exception {
    try {
      frs.setDate(1, null, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetDoubleIntDoubleThrowsException() throws Exception {
    try {
      frs.setDouble(1, 0d);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetFloatIntFloatThrowsException() throws Exception {
    try {
      frs.setFloat(1, 0f);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetIntIntIntThrowsException() throws Exception {
    try {
      frs.setInt(1, 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetLongIntLongThrowsException() throws Exception {
    try {
      frs.setLong(1, 0L);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNCharacterStreamIntReaderThrowsException()
      throws Exception {
    try {
      frs.setNCharacterStream(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNCharacterStreamIntReaderLongThrowsException()
      throws Exception {
    try {
      frs.setNCharacterStream(1, null, 0L);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNClobIntNClobThrowsException() throws Exception {
    try {
      frs.setNClob(1, (NClob) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNClobIntReaderThrowsException() throws Exception {
    try {
      frs.setNClob(1, (Reader) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNClobIntReaderLongThrowsException() throws Exception {
    try {
      frs.setNClob(1, null, 0L);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNStringIntStringThrowsException() throws Exception {
    try {
      frs.setNString(1, "");
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNullIntIntThrowsException() throws Exception {
    try {
      frs.setNull(1, Types.VARCHAR);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNullIntIntStringThrowsException() throws Exception {
    try {
      frs.setNull(1, Types.VARCHAR, "");
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetObjectIntObjectThrowsException() throws Exception {
    try {
      frs.setObject(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetObjectIntObjectIntThrowsException() throws Exception {
    try {
      frs.setObject(1, null, Types.VARCHAR);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetObjectIntObjectIntIntThrowsException() throws Exception {
    try {
      frs.setObject(1, null, Types.VARCHAR, 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetRefIntRefThrowsException() throws Exception {
    try {
      frs.setRef(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetRowIdIntRowIdThrowsException() throws Exception {
    try {
      frs.setRowId(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetShortIntShortThrowsException() throws Exception {
    try {
      frs.setShort(1, (short) 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetSQLXMLIntSQLXMLThrowsException() throws Exception {
    try {
      frs.setSQLXML(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetStringIntStringThrowsException() throws Exception {
    try {
      frs.setString(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetTimeIntTimeThrowsException() throws Exception {
    try {
      frs.setTime(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetTimeIntTimeCalendarThrowsException() throws Exception {
    try {
      frs.setTime(1, null, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetTimestampIntTimestampThrowsException() throws Exception {
    try {
      frs.setTimestamp(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetTimestampIntTimestampCalendarThrowsException()
      throws Exception {
    try {
      frs.setTimestamp(1, null, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetUnicodeStreamInputStreamIntThrowsException()
      throws Exception {
    try {
      frs.setUnicodeStream(1, null, 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetURLIntURLThrowsException() throws Exception {
    try {
      frs.setURL(1, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  // named arguments

  public void testSetAsciiStreamStringInputStreamThrowsException()
      throws Exception {
    try {
      frs.setAsciiStream("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetAsciiStreamStringInputStreamIntThrowsException()
      throws Exception {
    try {
      frs.setAsciiStream("Column 1", null, 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBigDecimalStringBigDecimalThrowsException()
      throws Exception {
    try {
      frs.setBigDecimal("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBinaryStreamStringInputStreamThrowsException()
      throws Exception {
    try {
      frs.setBinaryStream("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBinaryStreamStringInputStreamIntThrowsException()
      throws Exception {
    try {
      frs.setBinaryStream("Column 1", null, 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBlobStringBlobThrowsException() throws Exception {
    try {
      frs.setBlob("Column 1", (Blob) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBlobStringInputStreamThrowsException() throws Exception {
    try {
      frs.setBlob("Column 1", (InputStream) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBlobStringInputStreamLongThrowsException()
      throws Exception {
    try {
      frs.setBlob("Column 1", null, 0L);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBooleanStringBooleanThrowsException() throws Exception {
    try {
      frs.setBoolean("Column 1", true);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetByteStringByteThrowsException() throws Exception {
    try {
      frs.setByte("Column 1", (byte) 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetBytesStringByteArrayThrowsException() throws Exception {
    try {
      frs.setBytes("Column 1", new byte[] {});
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetCharacterStreamStringReaderThrowsException()
      throws Exception {
    try {
      frs.setCharacterStream("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetCharacterStreamStringReaderIntThrowsException()
      throws Exception {
    try {
      frs.setCharacterStream("Column 1", null, 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetClobStringClobThrowsException() throws Exception {
    try {
      frs.setClob("Column 1", (Clob) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetClobStringReaderThrowsException() throws Exception {
    try {
      frs.setClob("Column 1", (Reader) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetClobStringReaderLongThrowsException() throws Exception {
    try {
      frs.setClob("Column 1", null, 0L);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetDateStringDateThrowsException() throws Exception {
    try {
      frs.setDate("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetDateStringDateCalendarThrowsException() throws Exception {
    try {
      frs.setDate("Column 1", null, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetDoubleStringDoubleThrowsException() throws Exception {
    try {
      frs.setDouble("Column 1", 0d);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetFloatStringFloatThrowsException() throws Exception {
    try {
      frs.setFloat("Column 1", 0f);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetIntStringIntThrowsException() throws Exception {
    try {
      frs.setInt("Column 1", 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetLongStringLongThrowsException() throws Exception {
    try {
      frs.setLong("Column 1", 0L);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNCharacterStreamStringReaderThrowsException()
      throws Exception {
    try {
      frs.setNCharacterStream("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNCharacterStreamStringReaderLongThrowsException()
      throws Exception {
    try {
      frs.setNCharacterStream("Column 1", null, 0L);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNClobStringNClobThrowsException() throws Exception {
    try {
      frs.setNClob("Column 1", (NClob) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNClobStringReaderThrowsException() throws Exception {
    try {
      frs.setNClob("Column 1", (Reader) null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNClobStringReaderLongThrowsException() throws Exception {
    try {
      frs.setNClob("Column 1", null, 0L);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNStringStringStringThrowsException() throws Exception {
    try {
      frs.setNString("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNullStringIntThrowsException() throws Exception {
    try {
      frs.setNull("Column 1", Types.VARCHAR);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetNullStringIntStringThrowsException() throws Exception {
    try {
      frs.setNull("Column 1", Types.VARCHAR, "");
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetObjectStringObjectThrowsException() throws Exception {
    try {
      frs.setObject("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetObjectStringObjectIntThrowsException() throws Exception {
    try {
      frs.setObject("Column 1", null, Types.VARCHAR);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetObjectStringObjectIntIntThrowsException() throws Exception {
    try {
      frs.setObject("Column 1", null, Types.VARCHAR, 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetRowIdStringRowIdThrowsException() throws Exception {
    try {
      frs.setRowId("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetShortStringShortThrowsException() throws Exception {
    try {
      frs.setShort("Column 1", (short) 0);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetSQLXMLStringSQLXMLThrowsException() throws Exception {
    try {
      frs.setSQLXML("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetStringStringStringThrowsException() throws Exception {
    try {
      frs.setString("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetTimeStringTimeThrowsException() throws Exception {
    try {
      frs.setTime("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetTimeStringTimeCalendarThrowsException() throws Exception {
    try {
      frs.setTime("Column 1", null, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetTimestampStringTimestampThrowsException() throws Exception {
    try {
      frs.setTimestamp("Column 1", null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetTimestampStringTimestampCalendarThrowsException()
      throws Exception {
    try {
      frs.setTimestamp("Column 1", null, null);
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetCommandThrowsException() throws Exception {
    try {
      frs.setCommand("");
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetUsernameThrowsException() {
    try {
      frs.setUsername("");
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetPasswordThrowsException() {
    try {
      frs.setPassword("");
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetUrlThrowsException() throws Exception {
    try {
      frs.setUrl("");
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testUnwrapThrowsException() {
    try {
      frs.unwrap(null);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testUpdateArrayIntArrayThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateArray(1, null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateArrayStringArrayThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateArray("Column 1", null);
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateAsciiStream(1, new ByteArrayInputStream(new byte[] { 72 }));
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateAsciiStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }));
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamIntThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateAsciiStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamIntThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateAsciiStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1);
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateAsciiStreamIntInputStreamLongThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateAsciiStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1L);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateAsciiStreamStringInputStreamLongThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateAsciiStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1L);
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }));
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }));
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamIntThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamIntThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1);
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBinaryStreamIntInputStreamLongThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBinaryStream(1, new ByteArrayInputStream(new byte[] { 72 }), 1L);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBinaryStreamStringInputStreamLongThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBinaryStream("Column 1", new ByteArrayInputStream(
          new byte[] { 72 }), 1L);
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBlobIntBlobThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBlob(1, (Blob) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBlobStringBlobThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBlob("Column 1", (Blob) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBlobIntInputStreamThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBlob(1, (InputStream) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBlobStringInputStreamThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBlob("Column 1", (InputStream) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBlobIntInputStreamLongThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBlob(1, null, 0L);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateBlobStringInputStreamLongThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateBlob("Column 1", null, 0L);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateCharacterStream(1, new StringReader("a"));
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateCharacterStream("Column 1", new StringReader("a"));
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderIntThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateCharacterStream(1, new StringReader("a"), 1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderIntThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateCharacterStream("Column 1", new StringReader("a"), 1);
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateCharacterStreamIntReaderLongThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateCharacterStream(1, new StringReader("a"), 1L);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateCharacterStreamStringReaderLongThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateCharacterStream("Column 1", new StringReader("a"), 1L);
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateClobIntClobThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateClob(1, (Clob) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateClobStringClobThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateClob("Column 1", (Clob) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateClobIntReaderThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateClob(1, (Reader) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateClobStringReaderThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateClob("Column 1", (Reader) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateClobIntReaderLongThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateClob(1, null, 0L);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateClobStringReaderLongThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateClob("Column 1", null, 0L);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNCharacterStreamIntReaderIntThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNCharacterStream(1, new StringReader("a"), 1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNCharacterStreamStringReaderIntThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNCharacterStream("Column 1", new StringReader("a"), 1);
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNCharacterStreamIntReaderLongThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNCharacterStream(1, new StringReader("a"), 1L);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNCharacterStreamStringReaderLongThrowsException()
      throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNCharacterStream("Column 1", new StringReader("a"), 1L);
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNClobIntNClobThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNClob(1, (NClob) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNClobStringNClobThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNClob("Column 1", (NClob) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNClobIntReaderThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNClob(1, (Reader) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNClobStringReaderThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNClob("Column 1", (Reader) null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNClobIntReaderLongThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNClob(1, null, 0L);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNClobStringReaderLongThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNClob("Column 1", null, 0L);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNullIntThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNull(1);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateNullStringThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateNull("Column 1");
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateRefIntRefThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateRef(1, null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateRefStringRefThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateRef("Column 1", null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateRowThrowsException() throws Exception {
    load(small);
    frs.next();
    frs.updateInt(1, 1);
    try {
      frs.updateRow();
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateRowIdIntRowIdThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateRowId(1, null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateRowIdStringRowIdThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateRowId("Column 1", null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateSQLXMLIntRowIdThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateSQLXML(1, null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

  public void testUpdateSQLXMLStringRowIdThrowsException() throws Exception {
    load(small);
    frs.next();
    try {
      frs.updateSQLXML("Column 1", null);
      fail();
    } catch (SQLFeatureNotSupportedException e) {
    }
  }

}
