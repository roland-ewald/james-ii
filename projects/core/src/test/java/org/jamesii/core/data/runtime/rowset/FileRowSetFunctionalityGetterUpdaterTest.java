/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import org.jamesii.core.data.runtime.rowset.FileRowSet;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.misc.Base64;

/**
 * Tests that the getter and updater methods in {@link FileRowSet} work as
 * expected.
 * 
 * @author Johannes Rössel
 * 
 * @see FileRowSetFunctionalityTest
 * @see FileRowSetFunctionalityInapplicableTest
 * @see FileRowSetExceptionsTest
 * @see FileRowSetExceptionsInsertRowTest
 * @see FileRowSetExceptionsClosedTest
 * @see FileRowSetExceptionsInapplicableTest
 * @see FileRowSetExceptionsTypeConcurrencyTest
 * @see FileRowSetExceptionsCurrentRowTest
 * @see FileRowSetExceptionsInvalidColumnTest
 */
public class FileRowSetFunctionalityGetterUpdaterTest extends
    AbstractFileRowSetTest {

  public void testGetBigDecimalIntWholeNumber() throws Exception {
    load(types);
    frs.next();
    assertEquals(BigDecimal.TEN, frs.getBigDecimal(6));
  }

  public void testGetBigDecimalStringWholeNumber() throws Exception {
    load(types);
    frs.next();
    assertEquals(BigDecimal.TEN, frs.getBigDecimal("BigDecimal"));
  }

  public void testGetBigDecimalIntFraction() throws Exception {
    load(types);
    frs.absolute(2);
    assertEquals(new BigDecimal("3.1415926"), frs.getBigDecimal(6));
  }

  public void testGetBigDecimalStringFraction() throws Exception {
    load(types);
    frs.absolute(2);
    assertEquals(new BigDecimal("3.1415926"), frs.getBigDecimal("BigDecimal"));
  }

  public void testGetBigDecimalIntLargeNegative() throws Exception {
    load(types);
    frs.absolute(3);
    assertEquals(new BigDecimal(
        "-89347894893489593476897689347689347896789345768937896e25"),
        frs.getBigDecimal(6));
  }

  public void testGetBigDecimalStringLargeNegative() throws Exception {
    load(types);
    frs.absolute(3);
    assertEquals(new BigDecimal(
        "-89347894893489593476897689347689347896789345768937896e25"),
        frs.getBigDecimal("BigDecimal"));
  }

  public void testGetBigDecimalIntIntWholeNumber() throws Exception {
    load(types);
    frs.next();
    assertEquals(BigDecimal.TEN.setScale(3), frs.getBigDecimal(6, 3));
  }

  public void testGetBigDecimalStringIntWholeNumber() throws Exception {
    load(types);
    frs.next();
    assertEquals(BigDecimal.TEN.setScale(3), frs.getBigDecimal("BigDecimal", 3));
  }

  public void testGetBigDecimalIntIntFraction() throws Exception {
    load(types);
    frs.absolute(2);
    assertEquals(new BigDecimal("3.1415926").setScale(12),
        frs.getBigDecimal(6, 12));
  }

  public void testGetBigDecimalStringIntFraction() throws Exception {
    load(types);
    frs.absolute(2);
    assertEquals(new BigDecimal("3.1415926").setScale(12),
        frs.getBigDecimal("BigDecimal", 12));
  }

  public void testGetBigDecimalIntIntLargeNegative() throws Exception {
    load(types);
    frs.absolute(3);
    assertEquals(
        new BigDecimal(
            "-89347894893489593476897689347689347896789345768937896e25").setScale(50),

        frs.getBigDecimal(6, 50));
  }

  public void testGetBigDecimalStringIntLargeNegative() throws Exception {
    load(types);
    frs.absolute(3);
    assertEquals(
        new BigDecimal(
            "-89347894893489593476897689347689347896789345768937896e25")
            .setScale(50),
        frs.getBigDecimal("BigDecimal", 50));
  }

  public void testGetBooleanInt() throws Exception {
    load(types);
    frs.next();
    assertFalse(frs.getBoolean(1));
    frs.next();
    assertTrue(frs.getBoolean(1));
  }

  public void testGetBooleanIntReturnsFalseOn0() throws Exception {
    load(small);
    frs.next();
    assertFalse(frs.getBoolean(1));
  }

  public void testGetBooleanIntReturnsFalseOnNonNumber() throws Exception {
    load(small);
    frs.next();
    assertFalse(frs.getBoolean(3));
  }

  public void testGetBooleanIntReturnsTrueOnNon0() throws Exception {
    load(small);
    frs.next();
    frs.next();
    assertTrue(frs.getBoolean(1));
  }

  public void testGetBooleanString() throws Exception {
    load(types);
    frs.next();
    assertFalse(frs.getBoolean("Boolean"));
    frs.next();
    assertTrue(frs.getBoolean("Boolean"));
  }

  public void testGetBooleanStringReturnsFalseOn0() throws Exception {
    load(small);
    frs.next();
    assertFalse(frs.getBoolean("Column 1"));
  }

  public void testGetBooleanStringReturnsTrueOnNon0() throws Exception {
    load(small);
    frs.next();
    frs.next();
    assertTrue(frs.getBoolean("Column 1"));
  }

  public void testGetBooleanStringReturnsFalseOnNonNumber() throws Exception {
    load(small);
    frs.next();
    assertFalse(frs.getBoolean("Col3"));
  }

  public void testGetByteInt() throws Exception {
    load(small);
    frs.next();
    assertEquals(0, frs.getByte(1));
    assertEquals(0, frs.getByte(2));
  }

  public void testGetByteString() throws Exception {
    load(small);
    frs.next();
    assertEquals(0, frs.getByte("Column 1"));
    assertEquals(0, frs.getByte("Column 2"));
  }

  public void testGetBytesInt() throws Exception {
    load(types);
    frs.next();
    assertEquals(
        Base64.encode(new byte[] { 72, 101, 108, 108, 111, 32, 87, 111, 114,
            108, 100 }), Base64.encode(frs.getBytes(5)));
  }

  public void testGetBytesString() throws Exception {
    load(types);
    frs.next();
    assertEquals(
        Base64.encode(new byte[] { 72, 101, 108, 108, 111, 32, 87, 111, 114,
            108, 100 }), Base64.encode(frs.getBytes("ByteArray")));
  }

  public void testGetDateInt() throws Exception {
    load(types);
    frs.next();
    assertEquals(Date.valueOf("2011-02-04"), frs.getDate(2));
    frs.next();
    assertEquals(Date.valueOf("2010-02-28"), frs.getDate(2));
  }

  public void testGetDateString() throws Exception {
    load(types);
    frs.next();
    assertEquals(Date.valueOf("2011-02-04"), frs.getDate("Date"));
    frs.next();
    assertEquals(Date.valueOf("2010-02-28"), frs.getDate("Date"));
  }

  public void testGetDoubleInt() throws Exception {
    load(small);
    frs.next();
    assertEquals(0d, frs.getDouble(1));
    assertEquals(0d, frs.getDouble(2));
  }

  public void testGetDoubleString() throws Exception {
    load(small);
    frs.next();
    assertEquals(0d, frs.getDouble("Column 1"));
    assertEquals(0d, frs.getDouble("Column 2"));
  }

  public void testGetFloatInt() throws Exception {
    load(small);
    frs.next();
    assertEquals(0f, frs.getFloat(1));
    assertEquals(0f, frs.getFloat(2));
  }

  public void testGetFloatString() throws Exception {
    load(small);
    frs.next();
    assertEquals(0f, frs.getFloat("Column 1"));
    assertEquals(0f, frs.getFloat("Column 2"));
  }

  public void testGetIntInt() throws Exception {
    load(small);
    frs.next();
    assertEquals(0, frs.getInt(1));
    assertEquals(0, frs.getInt(2));
  }

  public void testGetIntString() throws Exception {
    load(small);
    frs.next();
    assertEquals(0, frs.getInt("Column 1"));
    assertEquals(0, frs.getInt("Column 2"));
  }

  public void testGetLongInt() throws Exception {
    load(small);
    frs.next();
    assertEquals(0L, frs.getLong(1));
    assertEquals(0L, frs.getLong(2));
  }

  public void testGetLongString() throws Exception {
    load(small);
    frs.next();
    assertEquals(0L, frs.getLong("Column 1"));
    assertEquals(0L, frs.getLong("Column 2"));
  }

  public void testGetNStringInt() throws Exception {
    load(small);
    frs.next();
    assertEquals("0", frs.getNString(1));
    assertEquals("0", frs.getNString(1));
    assertEquals("foo", frs.getNString(3));
  }

  public void testGetNStringString() throws Exception {
    load(small);
    frs.next();
    assertEquals("0", frs.getNString("Column 1"));
    assertEquals("0", frs.getNString("Column 2"));
    assertEquals("foo", frs.getNString("Col3"));
  }

  public void testGetObjectInt() throws Exception {
    load(small);
    frs.next();
    assertEquals("0", frs.getObject(1));
    assertEquals("0", frs.getObject(2));
    assertEquals("foo", frs.getObject(3));
  }

  public void testGetObjectIntReturnsString() throws Exception {
    load(small);
    frs.next();
    assertEquals(String.class, frs.getObject(1).getClass());
    assertEquals(String.class, frs.getObject(2).getClass());
    assertEquals(String.class, frs.getObject(3).getClass());
    frs.updateObject(1, new ParameterBlock());
    assertEquals(String.class, frs.getObject(1).getClass());
  }

  public void testGetObjectIntMap() throws Exception {
    load(small);
    frs.next();
    assertEquals("0", frs.getObject(1, new HashMap<String, Class<?>>()));
    assertEquals("0", frs.getObject(2, new HashMap<String, Class<?>>()));
    assertEquals("foo", frs.getObject(3, new HashMap<String, Class<?>>()));
  }

  public void testGetObjectIntMapReturnsString() throws Exception {
    load(small);
    frs.next();
    assertEquals(String.class, frs
        .getObject(1, new HashMap<String, Class<?>>()).getClass());
    assertEquals(String.class, frs
        .getObject(2, new HashMap<String, Class<?>>()).getClass());
    assertEquals(String.class, frs
        .getObject(3, new HashMap<String, Class<?>>()).getClass());
    frs.updateObject(1, new ParameterBlock());
    assertEquals(String.class, frs
        .getObject(1, new HashMap<String, Class<?>>()).getClass());
  }

  public void testGetObjectString() throws Exception {
    load(small);
    frs.next();
    assertEquals("0", frs.getObject("Column 1"));
    assertEquals("0", frs.getObject("Column 2"));
    assertEquals("foo", frs.getObject("Col3"));
  }

  public void testGetObjectStringReturnsString() throws Exception {
    load(small);
    frs.next();
    assertEquals(String.class, frs.getObject("Column 1").getClass());
    assertEquals(String.class, frs.getObject("Column 2").getClass());
    assertEquals(String.class, frs.getObject("Col3").getClass());
    frs.updateObject("Column 1", new ParameterBlock());
    assertEquals(String.class, frs.getObject("Column 1").getClass());
  }

  public void testGetObjectStringMap() throws Exception {
    load(small);
    frs.next();
    assertEquals("0",
        frs.getObject("Column 1", new HashMap<String, Class<?>>()));
    assertEquals("0",
        frs.getObject("Column 2", new HashMap<String, Class<?>>()));
    assertEquals("foo", frs.getObject("Col3", new HashMap<String, Class<?>>()));
  }

  public void testGetObjectStringMapReturnsString() throws Exception {
    load(small);
    frs.next();
    assertEquals(String.class,
        frs.getObject("Column 1", new HashMap<String, Class<?>>()).getClass());
    assertEquals(String.class,
        frs.getObject("Column 2", new HashMap<String, Class<?>>()).getClass());
    assertEquals(String.class,
        frs.getObject("Col3", new HashMap<String, Class<?>>()).getClass());
    frs.updateObject("Column 1", new ParameterBlock());
    assertEquals(String.class,
        frs.getObject("Column 1", new HashMap<String, Class<?>>()).getClass());
  }

  public void testGetShortInt() throws Exception {
    load(small);
    frs.next();
    assertEquals(0, frs.getShort(1));
    assertEquals(0, frs.getShort(2));
  }

  public void testGetShortString() throws Exception {
    load(small);
    frs.next();
    assertEquals(0, frs.getShort("Column 1"));
    assertEquals(0, frs.getShort("Column 2"));
  }

  public void testGetStringInt() throws Exception {
    load(small);
    frs.next();
    assertEquals("0", frs.getString(1));
    assertEquals("0", frs.getString(1));
    assertEquals("foo", frs.getString(3));
  }

  public void testGetStringString() throws Exception {
    load(small);
    frs.next();
    assertEquals("0", frs.getString("Column 1"));
    assertEquals("0", frs.getString("Column 2"));
    assertEquals("foo", frs.getString("Col3"));
  }

  public void testGetTimeInt() throws Exception {
    load(types);
    frs.next();
    assertEquals(Time.valueOf("15:34:13"), frs.getTime(3));
    frs.next();
    assertEquals(Time.valueOf("15:34:00"), frs.getTime(3));
  }

  public void testGetTimeString() throws Exception {
    load(types);
    frs.next();
    assertEquals(Time.valueOf("15:34:13"), frs.getTime("Time"));
    frs.next();
    assertEquals(Time.valueOf("15:34:00"), frs.getTime("Time"));
  }

  public void testGetTimestampInt() throws Exception {
    load(types);
    frs.next();
    assertEquals(Timestamp.valueOf("2010-01-01 15:34:26.123456"),
        frs.getTimestamp(4));
    frs.next();
    assertEquals(Timestamp.valueOf("2010-01-01 15:34:26.1"),
        frs.getTimestamp(4));
    frs.next();
    assertEquals(Timestamp.valueOf("2010-01-01 15:34:26"), frs.getTimestamp(4));
  }

  public void testGetTimestampString() throws Exception {
    load(types);
    frs.next();
    assertEquals(Timestamp.valueOf("2010-01-01 15:34:26.123456"),
        frs.getTimestamp("Timestamp"));
    frs.next();
    assertEquals(Timestamp.valueOf("2010-01-01 15:34:26.1"),
        frs.getTimestamp("Timestamp"));
    frs.next();
    assertEquals(Timestamp.valueOf("2010-01-01 15:34:26"),
        frs.getTimestamp("Timestamp"));
  }

  public void testGetURLInt() throws Exception {
    load(types);
    frs.next();
    assertEquals(new URL("http://example.com"), frs.getURL(7));
    frs.next();
    assertEquals(new URL("ftp://example.com/some/path"), frs.getURL(7));
    frs.next();
    assertEquals(new URL("http://example.com/some/path?a=query&string=here"),
        frs.getURL(7));
  }

  public void testGetURLString() throws Exception {
    load(types);
    frs.next();
    assertEquals(new URL("http://example.com"), frs.getURL("URL"));
    frs.next();
    assertEquals(new URL("ftp://example.com/some/path"), frs.getURL("URL"));
    frs.next();
    assertEquals(new URL("http://example.com/some/path?a=query&string=here"),
        frs.getURL("URL"));
  }

  public void testUpdateBigDecimalIntBigDecimal() throws Exception {
    load(small);
    frs.next();
    frs.updateBigDecimal(1, BigDecimal.TEN);
    assertEquals(BigDecimal.TEN, frs.getBigDecimal(1));
    frs.updateBigDecimal(3, BigDecimal.valueOf(3.14159));
    assertEquals(BigDecimal.valueOf(3.14159d), frs.getBigDecimal(3));
  }

  public void testUpdateBigDecimalStringBigDecimal() throws Exception {
    load(small);
    frs.next();
    frs.updateBigDecimal("Column 1", BigDecimal.TEN);
    assertEquals(BigDecimal.TEN, frs.getBigDecimal("Column 1"));
    frs.updateBigDecimal("Col3", BigDecimal.valueOf(3.14159));
    assertEquals(BigDecimal.valueOf(3.14159d), frs.getBigDecimal("Col3"));
  }

  public void testUpdateBooleanIntBoolean() throws Exception {
    load(small);
    frs.next();
    frs.updateBoolean(1, true);
    assertTrue(frs.getBoolean(1));
    frs.updateBoolean(3, false);
    assertFalse(frs.getBoolean(3));
  }

  public void testUpdateBooleanStringBoolean() throws Exception {
    load(small);
    frs.next();
    frs.updateBoolean("Column 1", true);
    assertTrue(frs.getBoolean("Column 1"));
    frs.updateBoolean("Col3", false);
    assertFalse(frs.getBoolean("Col3"));
  }

  public void testUpdateByteIntByte() throws Exception {
    load(small);
    frs.next();
    frs.updateByte(1, (byte) 2);
    assertEquals((byte) 2, frs.getByte(1));
    frs.updateByte(3, (byte) 7);
    assertEquals((byte) 7, frs.getByte(3));
  }

  public void testUpdateByteStringByte() throws Exception {
    load(small);
    frs.next();
    frs.updateByte("Column 1", (byte) 2);
    assertEquals((byte) 2, frs.getByte("Column 1"));
    frs.updateByte("Col3", (byte) 7);
    assertEquals((byte) 7, frs.getByte("Col3"));
  }

  public void testUpdateBytesIntByteArray() throws Exception {
    load(small);
    frs.next();
    frs.updateBytes(1, new byte[] { 72, 101, 108, 108, 111, 32, 87, 111, 114,
        108, 100 });
    assertEquals(
        Base64.encode(new byte[] { 72, 101, 108, 108, 111, 32, 87, 111, 114,
            108, 100 }), Base64.encode(frs.getBytes(1)));
    frs.updateBytes(3, new byte[] { 102, 111, 111 });
    assertEquals(Base64.encode(new byte[] { 102, 111, 111 }),
        Base64.encode(frs.getBytes(3)));
  }

  public void testUpdateBytesStringByteArray() throws Exception {
    load(small);
    frs.next();
    frs.updateBytes("Column 1", new byte[] { 72, 101, 108, 108, 111, 32, 87,
        111, 114, 108, 100 });
    assertEquals(
        Base64.encode(new byte[] { 72, 101, 108, 108, 111, 32, 87, 111, 114,
            108, 100 }), Base64.encode(frs.getBytes("Column 1")));
    frs.updateBytes("Col3", new byte[] { 102, 111, 111 });
    assertEquals(Base64.encode(new byte[] { 102, 111, 111 }),
        Base64.encode(frs.getBytes("Col3")));
  }

  public void testUpdateDateIntDate() throws Exception {
    load(small);
    frs.next();
    frs.updateDate(1, Date.valueOf("2010-02-23"));
    assertEquals(Date.valueOf("2010-02-23"), frs.getDate(1));
    frs.updateDate(3, Date.valueOf("2011-02-08"));
    assertEquals(Date.valueOf("2011-02-08"), frs.getDate(3));
  }

  public void testUpdateDateStringDate() throws Exception {
    load(small);
    frs.next();
    frs.updateDate("Column 1", Date.valueOf("2010-02-23"));
    assertEquals(Date.valueOf("2010-02-23"), frs.getDate("Column 1"));
    frs.updateDate("Col3", Date.valueOf("2011-02-08"));
    assertEquals(Date.valueOf("2011-02-08"), frs.getDate("Col3"));
  }

  public void testUpdateDoubleIntDouble() throws Exception {
    load(small);
    frs.next();
    frs.updateDouble(1, 5.25d);
    assertEquals(5.25d, frs.getDouble(1));
    frs.updateDouble(3, 7.5d);
    assertEquals(7.5d, frs.getDouble(3));
  }

  public void testUpdateDoubleStringDouble() throws Exception {
    load(small);
    frs.next();
    frs.updateDouble("Column 1", 5.25d);
    assertEquals(5.25d, frs.getDouble("Column 1"));
    frs.updateDouble("Col3", 7.5d);
    assertEquals(7.5d, frs.getDouble("Col3"));
  }

  public void testUpdateFloatIntFloat() throws Exception {
    load(small);
    frs.next();
    frs.updateFloat(1, 5.25f);
    assertEquals(5.25f, frs.getFloat(1));
    frs.updateFloat(3, 7.5f);
    assertEquals(7.5f, frs.getFloat(3));
  }

  public void testUpdateFloatStringFloat() throws Exception {
    load(small);
    frs.next();
    frs.updateFloat("Column 1", 5.25f);
    assertEquals(5.25f, frs.getFloat("Column 1"));
    frs.updateFloat("Col3", 7.5f);
    assertEquals(7.5f, frs.getFloat("Col3"));
  }

  public void testUpdateIntIntString() throws Exception {
    load(small);
    frs.next();
    frs.updateInt(1, 5);
    assertEquals(5, frs.getInt(1));
    frs.updateInt(3, 7);
    assertEquals(7, frs.getInt(3));
  }

  public void testUpdateIntStringString() throws Exception {
    load(small);
    frs.next();
    frs.updateInt("Column 1", 5);
    assertEquals(5, frs.getInt("Column 1"));
    frs.updateInt("Col3", 7);
    assertEquals(7, frs.getInt("Col3"));
  }

  public void testUpdateLongIntLong() throws Exception {
    load(small);
    frs.next();
    frs.updateLong(1, 8L);
    assertEquals(8L, frs.getLong(1));
    frs.updateLong(3, 3L);
    assertEquals(3L, frs.getLong(3));
  }

  public void testUpdateLongStringLong() throws Exception {
    load(small);
    frs.next();
    frs.updateLong("Column 1", 8L);
    assertEquals(8L, frs.getLong("Column 1"));
    frs.updateLong("Col3", 3L);
    assertEquals(3L, frs.getLong("Col3"));
  }

  public void testUpdateNStringIntString() throws Exception {
    load(small);
    frs.next();
    frs.updateNString(1, "bar");
    assertEquals("bar", frs.getNString(1));
    frs.updateNString(3, "baz");
    assertEquals("baz", frs.getNString(3));
  }

  public void testUpdateNStringStringString() throws Exception {
    load(small);
    frs.next();
    frs.updateNString("Column 1", "bar");
    assertEquals("bar", frs.getNString("Column 1"));
    frs.updateNString("Col3", "baz");
    assertEquals("baz", frs.getNString("Col3"));
  }

  public void testUpdateObjectIntBigDecimal() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, new BigDecimal(
        "-873483493846.2139829348579339845679346"));
    assertEquals(new BigDecimal("-873483493846.2139829348579339845679346"),
        frs.getBigDecimal(1));
    frs.updateObject(3, new BigDecimal(Math.PI));
    assertEquals(new BigDecimal(Math.PI), frs.getBigDecimal(3));
  }

  public void testUpdateObjectStringBigDecimal() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", new BigDecimal(
        "-873483493846.2139829348579339845679346"));
    assertEquals(new BigDecimal("-873483493846.2139829348579339845679346"),
        frs.getBigDecimal("Column 1"));
    frs.updateObject("Col3", new BigDecimal(Math.PI));
    assertEquals(new BigDecimal(Math.PI), frs.getBigDecimal("Col3"));
  }

  public void testUpdateObjectIntBoolean() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, true);
    assertEquals(true, frs.getBoolean(1));
    frs.updateObject(3, true);
    assertEquals(true, frs.getBoolean(3));
  }

  public void testUpdateObjectStringBoolean() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", true);
    assertEquals(true, frs.getBoolean("Column 1"));
    frs.updateObject("Col3", true);
    assertEquals(true, frs.getBoolean("Col3"));
  }

  public void testUpdateObjectIntByte() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, (byte) 15);
    assertEquals((byte) 15, frs.getByte(1));
    frs.updateObject(3, (byte) 23);
    assertEquals((byte) 23, frs.getByte(3));
  }

  public void testUpdateObjectStringByte() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", (byte) 15);
    assertEquals((byte) 15, frs.getByte("Column 1"));
    frs.updateObject("Col3", (byte) 23);
    assertEquals((byte) 23, frs.getByte("Col3"));
  }

  public void testUpdateObjectIntBytes() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, new byte[] { 72, 108 });
    assertEquals(Base64.encode(new byte[] { 72, 108 }),
        Base64.encode(frs.getBytes(1)));
    frs.updateObject(3, new byte[] { 15, 37 });
    assertEquals(Base64.encode(new byte[] { 15, 37 }),
        Base64.encode(frs.getBytes(3)));
  }

  public void testUpdateObjectStringBytes() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", new byte[] { 72, 108 });
    assertEquals(Base64.encode(new byte[] { 72, 108 }),
        Base64.encode(frs.getBytes("Column 1")));
    frs.updateObject("Col3", new byte[] { 15, 37 });
    assertEquals(Base64.encode(new byte[] { 15, 37 }),
        Base64.encode(frs.getBytes("Col3")));
  }

  public void testUpdateObjectIntDate() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, Date.valueOf("2010-02-18"));
    assertEquals(Date.valueOf("2010-02-18"), frs.getDate(1));
    frs.updateObject(3, Date.valueOf("2010-02-27"));
    assertEquals(Date.valueOf("2010-02-27"), frs.getDate(3));
  }

  public void testUpdateObjectStringDate() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", Date.valueOf("2010-02-18"));
    assertEquals(Date.valueOf("2010-02-18"), frs.getDate("Column 1"));
    frs.updateObject("Col3", Date.valueOf("2010-02-27"));
    assertEquals(Date.valueOf("2010-02-27"), frs.getDate("Col3"));
  }

  public void testUpdateObjectIntFloat() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, 4.5f);
    assertEquals(4.5f, frs.getFloat(1));
    frs.updateObject(3, 7.5f);
    assertEquals(7.5f, frs.getFloat(3));
  }

  public void testUpdateObjectStringFloat() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", 3.5f);
    assertEquals(3.5f, frs.getFloat("Column 1"));
    frs.updateObject("Col3", 7.5f);
    assertEquals(7.5f, frs.getFloat("Col3"));
  }

  public void testUpdateObjectIntDouble() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, 4.5);
    assertEquals(4.5, frs.getDouble(1));
    frs.updateObject(3, 7.5);
    assertEquals(7.5, frs.getDouble(3));
  }

  public void testUpdateObjectStringDouble() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", 3.5);
    assertEquals(3.5, frs.getDouble("Column 1"));
    frs.updateObject("Col3", 7.5);
    assertEquals(7.5, frs.getDouble("Col3"));
  }

  public void testUpdateObjectIntInt() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, 4);
    assertEquals(4, frs.getInt(1));
    frs.updateObject(3, 7);
    assertEquals(7, frs.getInt(3));
  }

  public void testUpdateObjectStringInt() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", 3);
    assertEquals(3, frs.getInt("Column 1"));
    frs.updateObject("Col3", 7);
    assertEquals(7, frs.getInt("Col3"));
  }

  public void testUpdateObjectIntLong() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, 4L);
    assertEquals(4L, frs.getLong(1));
    frs.updateObject(3, 7L);
    assertEquals(7L, frs.getLong(3));
  }

  public void testUpdateObjectStringLong() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", 3L);
    assertEquals(3L, frs.getLong("Column 1"));
    frs.updateObject("Col3", 7L);
    assertEquals(7L, frs.getLong("Col3"));
  }

  public void testUpdateObjectIntString() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, "foo");
    assertEquals("foo", frs.getString(1));
    frs.updateObject(3, "bar");
    assertEquals("bar", frs.getString(3));
  }

  public void testUpdateObjectStringString() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", "foo");
    assertEquals("foo", frs.getString("Column 1"));
    frs.updateObject("Col3", "bar");
    assertEquals("bar", frs.getString("Col3"));
  }

  public void testUpdateObjectIntTime() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, Time.valueOf("12:36:14"));
    assertEquals(Time.valueOf("12:36:14"), frs.getTime(1));
    frs.updateObject(3, Time.valueOf("18:37:02"));
    assertEquals(Time.valueOf("18:37:02"), frs.getTime(3));
  }

  public void testUpdateObjectStringTime() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", Time.valueOf("12:36:14"));
    assertEquals(Time.valueOf("12:36:14"), frs.getTime("Column 1"));
    frs.updateObject("Col3", Time.valueOf("18:37:02"));
    assertEquals(Time.valueOf("18:37:02"), frs.getTime("Col3"));
  }

  public void testUpdateObjectIntTimestamp() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, Timestamp.valueOf("2010-03-14 12:36:14.123"));
    assertEquals(Timestamp.valueOf("2010-03-14 12:36:14.123"),
        frs.getTimestamp(1));
    frs.updateObject(3, Timestamp.valueOf("2011-01-01 18:37:02"));
    assertEquals(Timestamp.valueOf("2011-01-01 18:37:02"), frs.getTimestamp(3));
  }

  public void testUpdateObjectStringTimestamp() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", Timestamp.valueOf("2009-08-26 12:36:14"));
    assertEquals(Timestamp.valueOf("2009-08-26 12:36:14"),
        frs.getTimestamp("Column 1"));
    frs.updateObject("Col3", Timestamp.valueOf("2011-01-01 18:37:02.5"));
    assertEquals(Timestamp.valueOf("2011-01-01 18:37:02.5"),
        frs.getTimestamp("Col3"));
  }

  public void testUpdateObjectIntOtherObject() throws Exception {
    load(small);
    frs.next();
    frs.updateObject(1, new ParameterBlock("foo"));
    assertTrue(new ParameterBlock("foo")
        .compareTo((ParameterBlock) SerialisationUtils
            .deserializeFromB64String(frs.getString(1))) == 0);
    frs.updateObject(3, new ParameterBlock(Float.valueOf(3.5f), "foo"));
    assertTrue(new ParameterBlock(Float.valueOf(3.5f), "foo")
        .compareTo((ParameterBlock) SerialisationUtils
            .deserializeFromB64String(frs.getString(3))) == 0);
  }

  public void testUpdateObjectStringOtherObject() throws Exception {
    load(small);
    frs.next();
    frs.updateObject("Column 1", new ParameterBlock("foo"));

    assertTrue(new ParameterBlock("foo")
        .compareTo((ParameterBlock) SerialisationUtils
            .deserializeFromB64String(frs.getString("Column 1"))) == 0);
    frs.updateObject("Col3", new ParameterBlock(Float.valueOf(3.5f), "foo"));
    assertTrue(new ParameterBlock(Float.valueOf(3.5f), "foo")
        .compareTo((ParameterBlock) SerialisationUtils
            .deserializeFromB64String(frs.getString("Col3"))) == 0);
  }

  public void testUpdateShortIntShort() throws Exception {
    load(small);
    frs.next();
    frs.updateShort(1, (short) 5);
    assertEquals((short) 5, frs.getShort(1));
    frs.updateShort(3, (short) 7);
    assertEquals((short) 7, frs.getShort(3));
  }

  public void testUpdateShortStringShort() throws Exception {
    load(small);
    frs.next();
    frs.updateShort("Column 1", (short) 5);
    assertEquals((short) 5, frs.getShort("Column 1"));
    frs.updateShort("Col3", (short) 7);
    assertEquals((short) 7, frs.getShort("Col3"));
  }

  public void testUpdateStringIntString() throws Exception {
    load(small);
    frs.next();
    frs.updateString(1, "bar");
    assertEquals("bar", frs.getString(1));
    frs.updateString(3, "baz");
    assertEquals("baz", frs.getString(3));
  }

  public void testUpdateStringStringString() throws Exception {
    load(small);
    frs.next();
    frs.updateString("Column 1", "bar");
    assertEquals("bar", frs.getString("Column 1"));
    frs.updateString("Col3", "baz");
    assertEquals("baz", frs.getString("Col3"));
  }

  public void testUpdateTimeIntTime() throws Exception {
    load(small);
    frs.next();
    frs.updateTime(1, Time.valueOf("17:23:41"));
    assertEquals(Time.valueOf("17:23:41"), frs.getTime(1));
    frs.updateTime(3, Time.valueOf("09:00:05"));
    assertEquals(Time.valueOf("09:00:05"), frs.getTime(3));
  }

  public void testUpdateTimeStringTime() throws Exception {
    load(small);
    frs.next();
    frs.updateTime("Column 1", Time.valueOf("17:23:41"));
    assertEquals(Time.valueOf("17:23:41"), frs.getTime("Column 1"));
    frs.updateTime("Col3", Time.valueOf("09:00:05"));
    assertEquals(Time.valueOf("09:00:05"), frs.getTime("Col3"));
  }

  public void testUpdateTimestampIntTimestamp() throws Exception {
    load(small);
    frs.next();
    frs.updateTimestamp(1, Timestamp.valueOf("2010-02-23 17:23:41"));
    assertEquals(Timestamp.valueOf("2010-02-23 17:23:41"), frs.getTimestamp(1));
    frs.updateTimestamp(3, Timestamp.valueOf("2011-02-08 09:00:05.12345"));
    assertEquals(Timestamp.valueOf("2011-02-08 09:00:05.12345"),
        frs.getTimestamp(3));
  }

  public void testUpdateTimestampStringTimestamp() throws Exception {
    load(small);
    frs.next();
    frs.updateTimestamp("Column 1", Timestamp.valueOf("2010-02-23 17:23:41"));
    assertEquals(Timestamp.valueOf("2010-02-23 17:23:41"),
        frs.getTimestamp("Column 1"));
    frs.updateTimestamp("Col3", Timestamp.valueOf("2011-02-08 09:00:05.12345"));
    assertEquals(Timestamp.valueOf("2011-02-08 09:00:05.12345"),
        frs.getTimestamp("Col3"));
  }

  // ======================================================
  // Invoking getters before updaters on the insert row
  // ======================================================

  public void testGetBigDecimalIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getBigDecimal(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getBigDecimal("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateBigDecimal(1, BigDecimal.ONE);
    frs.getBigDecimal(1);
  }

  public void testGetBigDecimalStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateBigDecimal("Column 1", BigDecimal.ONE);
    frs.getBigDecimal("Column 1");
  }

  public void testGetBigDecimalIntIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getBigDecimal(1, 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalStringIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getBigDecimal("Column 1", 0);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBigDecimalIntIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateBigDecimal(1, BigDecimal.ONE);
    frs.getBigDecimal(1, 0);
  }

  public void testGetBigDecimalStringIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateBigDecimal("Column 1", BigDecimal.ONE);
    frs.getBigDecimal("Column 1", 0);
  }

  public void testGetBooleanIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getBoolean(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBooleanStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getBoolean("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBooleanIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateBoolean(1, true);
    frs.getBoolean(1);
  }

  public void testGetBooleanStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateBoolean("Column 1", true);
    frs.getBoolean("Column 1");
  }

  public void testGetByteIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getByte(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetByteStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getByte("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetByteIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateByte(1, (byte) 5);
    frs.getByte(1);
  }

  public void testGetByteStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateByte("Column 1", (byte) 5);
    frs.getByte("Column 1");
  }

  public void testGetBytesIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getBytes(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBytesStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getBytes("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetBytesIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateBytes(1, new byte[] { 17 });
    frs.getBytes(1);
  }

  public void testGetBytesStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateBytes("Column 1", new byte[] { 17 });
    frs.getBytes("Column 1");
  }

  public void testGetDateIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getDate(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getDate("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateDate(1, new Date(0));
    frs.getDate(1);
  }

  public void testGetDateStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateDate("Column 1", new Date(0));
    frs.getDate("Column 1");
  }

  public void testGetDateIntCalendarBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getDate(1, Calendar.getInstance());
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateStringCalendarBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getDate("Column 1", Calendar.getInstance());
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDateIntCalendarAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateDate(1, new Date(0));
    frs.getDate(1, Calendar.getInstance());
  }

  public void testGetDateStringCalendarAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateDate("Column 1", new Date(0));
    frs.getDate("Column 1", Calendar.getInstance());
  }

  public void testGetDoubleIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getDouble(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDoubleStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getDouble("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetDoubleIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateDouble(1, 1.4);
    frs.getDouble(1);
  }

  public void testGetDoubleStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateDouble("Column 1", 1.4);
    frs.getDouble("Column 1");
  }

  public void testGetFloatIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getFloat(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetFloatStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getFloat("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetFloatIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateFloat(1, 3.14f);
    frs.getFloat(1);
  }

  public void testGetFloatStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateFloat("Column 1", 3.14f);
    frs.getFloat("Column 1");
  }

  public void testGetIntIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getInt(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetIntStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getInt("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetIntIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateInt(1, 5);
    frs.getInt(1);
  }

  public void testGetIntStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateInt("Column 1", 5);
    frs.getInt("Column 1");
  }

  public void testGetLongIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getLong(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetLongStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getLong("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetLongIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateLong(1, 5);
    frs.getLong(1);
  }

  public void testGetLongStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateLong("Column 1", 5);
    frs.getLong("Column 1");
  }

  public void testGetNStringIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getNString(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNStringStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getNString("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetNStringIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateNString(1, "a");
    frs.getNString(1);
  }

  public void testGetNStringStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateNString("Column 1", "a");
    frs.getNString("Column 1");
  }

  public void testGetShortIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getShort(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetShortStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getShort("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetShortIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateShort(1, (short) 7);
    frs.getShort(1);
  }

  public void testGetShortStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateShort("Column 1", (short) 7);
    frs.getShort("Column 1");
  }

  public void testGetStringIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getString(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetStringStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getString("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetStringIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateString(1, "a");
    frs.getString(1);
  }

  public void testGetStringStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateString("Column 1", "a");
    frs.getString("Column 1");
  }

  public void testGetTimeIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getTime(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getTime("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateTime(1, new Time(0));
    frs.getTime(1);
  }

  public void testGetTimeStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateTime("Column 1", new Time(0));
    frs.getTime("Column 1");
  }

  public void testGetTimeIntCalendarBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getTime(1, Calendar.getInstance());
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeStringCalendarBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getTime("Column 1", Calendar.getInstance());
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimeIntCalendarAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateTime(1, new Time(0));
    frs.getTime(1, Calendar.getInstance());
  }

  public void testGetTimeStringCalendarAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateTime("Column 1", new Time(0));
    frs.getTime("Column 1", Calendar.getInstance());
  }

  public void testGetTimestampIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getTimestamp(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getTimestamp("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateTimestamp(1, new Timestamp(0));
    frs.getTimestamp(1);
  }

  public void testGetTimestampStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateTimestamp("Column 1", new Timestamp(0));
    frs.getTimestamp("Column 1");
  }

  public void testGetTimestampIntCalendarBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getTimestamp(1, Calendar.getInstance());
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampStringCalendarBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getTimestamp("Column 1", Calendar.getInstance());
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetTimestampIntCalendarAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateTimestamp(1, new Timestamp(0));
    frs.getTimestamp(1, Calendar.getInstance());
  }

  public void testGetTimestampStringCalendarAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateTimestamp("Column 1", new Timestamp(0));
    frs.getTimestamp("Column 1", Calendar.getInstance());
  }

  public void testGetURLIntBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getURL(1);
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetURLStringBeforeUpdateThrowsExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.next();
    frs.moveToInsertRow();
    try {
      frs.getURL("Column 1");
      fail();
    } catch (SQLException e) {
    }
  }

  public void testGetURLIntAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateString(1, "http://example.com");
    frs.getURL(1);
  }

  public void testGetURLStringAfterUpdateDoesNotThrowExceptionOnInsertRow()
      throws Exception {
    load(small);
    frs.moveToInsertRow();
    frs.updateString("Column 1", "http://example.com");
    frs.getURL("Column 1");
  }

}
