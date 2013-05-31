/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import javax.sql.RowSet;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.BaseRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;

import org.jamesii.SimSystem;
import org.jamesii.core.data.runtime.CSVFormatter;
import org.jamesii.core.data.runtime.CSVParser;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.misc.Base64;

/**
 * An implementation of the {@link RowSet} that operates on large files by only
 * keeping a portion of the file in memory. This enables cached access to almost
 * arbitrarily sized files.
 * <p>
 * In the file, fields are stored as CSV.
 * <p>
 * This {@link RowSet} does not use the {@code command}, {@code username} or
 * {@code password} properties. The {@code dataSource} property is used to
 * denote the file name to use.
 * <p>
 * Since the {@code command} property is not used, all methods setting
 * parameters for the command are unavailable as well and will throw an
 * {@link UnsupportedOperationException}.
 * 
 * @see CSVParser
 * 
 * @author Johannes Rössel
 */
public class FileRowSet extends BaseRowSet implements RowSet {

  /**
   * Constant for {@link #currentIndex} that indicates that the current position
   * is before the first record.
   */
  private static final int BEFORE_FIRST = 0;

  /**
   * Constant for {@link #currentIndex} that indicates that the current position
   * is after the last record.
   */
  private static final int AFTER_LAST = -1;

  /** Serialisation ID. */
  private static final long serialVersionUID = -1274363674308388913L;

  /** Exception message for methods that have no meaning in this implementation. */
  private static final String EX_NOT_APPLICABLE =
      "This method is not applicable in this implementation.";

  /** The name of the file to operate on. */
  private String fileName;

  /**
   * Iterator to move line-wise through the file. It's transient as there is no
   * guarantee that the file we write to / read from is available on the target
   * machine.
   */
  private transient FileLineIterator lineIterator;

  /** The {@link CSVParser} to parse individual lines. */
  private CSVParser parser = new CSVParser();

  /** This {@link RowSet}'s metadata. */
  private transient RowSetMetaData metadata;

  /** The current row. */
  private String[] currentRow;

  /** The number of rows in the row set */
  private int rowCount = -1;

  /**
   * Supplementary information to {@link #currentRow}, indicating which columns
   * were set already.
   */
  private boolean[] insertRowWritten;

  /** The current record index. */
  private int currentIndex;

  /**
   * Flag to signal that the current row is not specified by
   * {@link #currentIndex} and that instead the insert row is the current row.
   */
  private boolean isOnInsertRow;

  /** Flag determining whether the file has rows. */
  private boolean hasRows;

  /**
   * Local buffer for the encoding as long as the {@link FileLineIterator}
   * instance doesn't yet exist.
   */
  private Charset encoding;

  /**
   * A value determining whether the file specified by {@link #fileName} has
   * already been opened or not. In the former case some properties are locked
   * for editing in this implementation.
   */
  private boolean fileOpened;

  /** A value indicating that this RowSet was closed. */
  private boolean closed;

  /** The buffer size to use for accessing the file. 32 MiB by default. */
  private int bufferSize = 32 * 1024 * 1024;

  /** Verification helper object. */
  private Verification verify = new Verification();

  /**
   * Initialises a new instance of the {@link FileRowSet} class with its default
   * values. The character encoding is UTF-8.
   * 
   * @throws RuntimeException
   *           wrapping an {@link SQLException} if an error occurs.
   */
  public FileRowSet() {
    closed = false;
    fileOpened = false;
    setEncoding(Charset.forName("UTF-8"));
    try {
      setType(TYPE_SCROLL_SENSITIVE);
      setConcurrency(CONCUR_UPDATABLE);
      setFetchDirection(FETCH_UNKNOWN);
    } catch (SQLException e) {
      throw new FileRowSetException(e);
    }
  }

  /**
   * Creates a new, empty file and a {@link FileRowSet} on that file with a
   * given set of column headers.
   * 
   * @param file
   *          The file to create and use.
   * @param encoding
   *          The character encoding to use in the file.
   * @param columnHeaders
   *          A list of column headers. Must not be empty.
   * @return A new {@link FileRowSet} operating on the newly-created file.
   * @throws SQLException
   *           if creating the {@link FileRowSet} fails.
   * @throws IOException
   *           if creating the file fails.
   */
  public static FileRowSet create(File file, Charset encoding,
      String... columnHeaders) throws SQLException, IOException {
    if (file == null) {
      throw new IOException(
          "File parameter is null. Cannot read / write to a null file.");
    }
    if (columnHeaders == null) {
      throw new SQLException("No column headers are given (null is passed).");
    }
    if (columnHeaders.length == 0) {
      throw new IllegalArgumentException("columnHeaders may not be empty.");
    }
    try (OutputStreamWriter w =
        new OutputStreamWriter(new FileOutputStream(file), encoding)) {
      for (int i = 0; i < columnHeaders.length; i++) {
        if (i != 0) {
          w.write(',');
        }
        w.write(columnHeaders[i]);
      }
      w.write("\r\n");
    }

    FileRowSet frs = new FileRowSet();
    frs.setFileName(file.getAbsolutePath());
    frs.setEncoding(encoding);
    frs.execute();

    return frs;
  }

  /**
   * Creates a new, empty file and a {@link FileRowSet} on that file with a
   * given set of column headers.
   * 
   * @param fileName
   *          The name of the file to create and use.
   * @param encoding
   *          The character encoding to use in the file.
   * @param columnHeaders
   *          A list of column headers.
   * @return A new {@link FileRowSet} operating on the newly-created file.
   * @throws SQLException
   *           if creating the {@link FileRowSet} fails.
   * @throws IOException
   *           if creating the file fails.
   */
  public static FileRowSet create(String fileName, Charset encoding,
      String... columnHeaders) throws SQLException, IOException {
    return create(new File(fileName), encoding, columnHeaders);
  }

  /**
   * Creates a new, empty file and a {@link FileRowSet} on that file with a
   * given set of column headers. The character encoding in the file defaults to
   * UTF-8.
   * 
   * @param file
   *          The file to create and use.
   * @param columnHeaders
   *          A list of column headers.
   * @return A new {@link FileRowSet} operating on the newly-created file.
   * @throws SQLException
   *           if creating the {@link FileRowSet} fails.
   * @throws IOException
   *           if creating the file fails.
   */
  public static FileRowSet create(File file, String... columnHeaders)
      throws SQLException, IOException {
    return create(file, Charset.forName("UTF-8"), columnHeaders);
  }

  /**
   * Creates a new, empty file and a {@link FileRowSet} on that file with a
   * given set of column headers. The character encoding in the file defaults to
   * UTF-8.
   * 
   * @param fileName
   *          The name of the file to create and use.
   * @param columnHeaders
   *          A list of column headers.
   * @return A new {@link FileRowSet} operating on the newly-created file.
   * @throws SQLException
   *           if creating the {@link FileRowSet} fails.
   * @throws IOException
   *           if creating the file fails.
   */
  public static FileRowSet create(String fileName, String... columnHeaders)
      throws SQLException, IOException {
    return create(new File(fileName), Charset.forName("UTF-8"), columnHeaders);
  }

  @Override
  public void close() throws SQLException {
    if (isClosed()) {
      return;
    }

    closed = true;
    fileOpened = false;

    if (lineIterator != null) {
      try {
        lineIterator.close();
      } catch (IOException e) {
        throw new SQLException(e);
      }
    }

    currentRow = null;
    insertRowWritten = null;
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    verify.notClosed();

    // create new metadata object to avoid someone tampering with the internal
    // one
    RowSetMetaData md = new RowSetMetaDataImpl();

    md.setColumnCount(metadata.getColumnCount());

    for (int i = 1; i <= metadata.getColumnCount(); i++) {
      md.setColumnLabel(i, metadata.getColumnLabel(i));
      md.setColumnName(i, metadata.getColumnName(i));
    }

    return md;
  }

  /**
   * Lightweight alternative to {@link #getMetaData()} for internal use. This
   * method can simply hand out the actual metadata object instead of creating a
   * copy trusting that the caller of this method does not modify the metadata.
   * 
   * @return This {@link RowSet}'s metadata.
   */
  private RowSetMetaData getMetaDataInternal() {
    return metadata;
  }

  /**
   * @return the file name of the underlying file.
   */
  @Override
  public String getDataSourceName() {
    return getFileName();
  }

  /**
   * Retrieves the file name of the underlying file.
   * 
   * @return the file name of the underlying file.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Sets the file name for this RowSet. This is only valid as long as the file
   * hasn't yet been opened. This method is equivalent to
   * {@link #setFileName(String)}.
   * 
   * @param name
   *          the new file name to use.
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           if the file was opened already.
   * @throws NullPointerException
   *           if the file name was {@code null}.
   * 
   * @see #setFileName(String)
   */
  @Override
  public void setDataSourceName(String name) throws SQLException {
    setFileName(name);
  }

  /**
   * Sets the file name for this RowSet. This is only valid as long as the file
   * hasn't yet been opened.
   * 
   * @param newFileName
   *          The new file name to use.
   * @throws UnsupportedOperationException
   *           if the file was opened already.
   * @throws NullPointerException
   *           if the file name was {@code null}.
   */
  public void setFileName(String newFileName) {
    verify.fileOpened().notNull(newFileName, "The file name may not be null.");

    this.fileName = newFileName;
  }

  @Override
  public boolean next() throws SQLException {
    verify.notClosed().notInsertRow();

    if (isBeforeFirst()) {
      currentIndex = 1;
    } else if (isAfterLast()) {
      return false;
    } else {
      currentIndex++;
    }

    CharSequence line = lineIterator.next();

    if (line == null) {
      currentIndex = AFTER_LAST;
      return false;
    }

    currentRow = parser.parse(line);
    return true;
  }

  @Override
  public boolean wasNull() throws SQLException {
    verify.notClosed();

    return false;
  }

  /**
   * Retrieves the value of the field at the given column as a string.
   * 
   * @param columnIndex
   *          The column index. Must be between 1 and the number of columns.
   * @return The value at the given column index.
   */
  private String get(int columnIndex) {
    return currentRow[columnIndex - 1];
  }

  /**
   * Sets the value of a field to the given value.
   * 
   * @param columnIndex
   *          The column index. Must be between 1 and the number of columns.
   * @param value
   *          The new value for that field.
   */
  private void set(int columnIndex, String value) {
    currentRow[columnIndex - 1] = value;
    if (isOnInsertRow) {
      insertRowWritten[columnIndex - 1] = true;
    }
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public String getString(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast()
        .getterOnInsertRow(columnIndex);

    return get(columnIndex);
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public boolean getBoolean(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast()
        .getterOnInsertRow(columnIndex);

    String s = get(columnIndex);
    if ("true".equalsIgnoreCase(s)) {
      return true;
    } else if ("false".equalsIgnoreCase(s)) {
      return false;
    } else {
      // try treating the string as a number
      try {
        long x = Long.parseLong(s);
        return (x != 0L);
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public byte getByte(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast()
        .getterOnInsertRow(columnIndex);

    return Byte.parseByte(get(columnIndex));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public short getShort(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast()
        .getterOnInsertRow(columnIndex);

    return Short.parseShort(get(columnIndex));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public int getInt(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast()
        .getterOnInsertRow(columnIndex);

    return Integer.parseInt(get(columnIndex));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public long getLong(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast()
        .getterOnInsertRow(columnIndex);

    return Long.parseLong(get(columnIndex));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public float getFloat(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast()
        .getterOnInsertRow(columnIndex);

    return Float.parseFloat(get(columnIndex));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public double getDouble(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast()
        .getterOnInsertRow(columnIndex);

    return Double.parseDouble(get(columnIndex));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public BigDecimal getBigDecimal(int columnIndex, int scale)
      throws SQLException {
    return getBigDecimal(columnIndex).setScale(scale);
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public byte[] getBytes(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast()
        .getterOnInsertRow(columnIndex);

    return Base64.decode(get(columnIndex));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public Date getDate(int columnIndex) throws SQLException {
    return Date.valueOf(getString(columnIndex));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public Time getTime(int columnIndex) throws SQLException {
    return Time.valueOf(getString(columnIndex));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    return Timestamp.valueOf(getString(columnIndex));
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #getAsciiStream(int)} method is not supported.
   */
  @Override
  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #getUnicodeStream(int)} method is not
   *           supported.
   */
  @Override
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #getBinaryStream(int)} method is not
   *           supported.
   */
  @Override
  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public String getString(String columnLabel) throws SQLException {
    return getString(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public boolean getBoolean(String columnLabel) throws SQLException {
    return getBoolean(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public byte getByte(String columnLabel) throws SQLException {
    return getByte(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public short getShort(String columnLabel) throws SQLException {
    return getShort(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public int getInt(String columnLabel) throws SQLException {
    return getInt(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public long getLong(String columnLabel) throws SQLException {
    return getLong(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public float getFloat(String columnLabel) throws SQLException {
    return getFloat(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public double getDouble(String columnLabel) throws SQLException {
    return getDouble(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public BigDecimal getBigDecimal(String columnLabel, int scale)
      throws SQLException {
    return getBigDecimal(findColumn(columnLabel), scale);
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public byte[] getBytes(String columnLabel) throws SQLException {
    return getBytes(findColumn(columnLabel));
  }

  @Override
  public Date getDate(String columnLabel) throws SQLException {
    return getDate(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public Time getTime(String columnLabel) throws SQLException {
    return getTime(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public Timestamp getTimestamp(String columnLabel) throws SQLException {
    return getTimestamp(findColumn(columnLabel));
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #getAsciiStream(String)} method is not
   *           supported.
   */
  @Override
  public InputStream getAsciiStream(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #getUnicodeStream(String)} method is not
   *           supported.
   */
  @Override
  public InputStream getUnicodeStream(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #getBinaryStream(String)} method is not
   *           supported.
   */
  @Override
  public InputStream getBinaryStream(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @return always {@code null} since this {@link RowSet} does not support
   *         warnings.
   */
  @Override
  public SQLWarning getWarnings() throws SQLException {
    verify.notClosed();
    return null;
  }

  /**
   * Does nothing since this {@link RowSet} does not support warnings.
   */
  @Override
  public void clearWarnings() throws SQLException {
    verify.notClosed();
  }

  /**
   * @return always the empty string.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public String getCursorName() throws SQLException {
    verify.notClosed();

    return "";
  }

  /**
   * Equivalent to {@link #getString(int)}.
   */
  @Override
  public Object getObject(int columnIndex) throws SQLException {
    return getString(columnIndex);
  }

  /**
   * Equivalent to {@link #getString(String)}.
   */
  @Override
  public Object getObject(String columnLabel) throws SQLException {
    return getString(columnLabel);
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   */
  @Override
  public int findColumn(String columnLabel) throws SQLException {
    verify.notClosed().notNull(columnLabel,
        "The column label must not be null.");

    for (int i = 1; i <= metadata.getColumnCount(); i++) {
      if (columnLabel.equals(metadata.getColumnLabel(i))) {
        return i;
      }
    }

    throw new SQLException(new IllegalArgumentException(
        "The column label is not valid"));
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #getCharacterStream(int)} method is not
   *           supported.
   */
  @Override
  public Reader getCharacterStream(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #getCharacterStream(String)} method is not
   *           supported.
   */
  @Override
  public Reader getCharacterStream(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   */
  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    return new BigDecimal(getString(columnIndex));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}..
   */
  @Override
  public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    return getBigDecimal(findColumn(columnLabel));
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public boolean isBeforeFirst() throws SQLException {
    verify.notClosed();

    return hasRows && currentIndex == BEFORE_FIRST;
  }

  /**
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public boolean isAfterLast() throws SQLException {
    verify.notClosed();

    return hasRows && currentIndex == AFTER_LAST;
  }

  @Override
  public boolean isFirst() throws SQLException {
    verify.notClosed();

    return !isOnInsertRow && currentIndex == 1;
  }

  @Override
  public boolean isLast() throws SQLException {
    verify.notClosed();

    return !isOnInsertRow && currentIndex != BEFORE_FIRST
        && currentIndex != AFTER_LAST && lineIterator.isLast();
  }

  @Override
  public void beforeFirst() throws SQLException {
    verify.notClosed().notForwardOnly().notInsertRow();

    lineIterator.absolute(0);
    currentRow = null;
    currentIndex = BEFORE_FIRST;
  }

  @Override
  public void afterLast() throws SQLException {
    verify.notClosed().notForwardOnly().notInsertRow();

    lineIterator.last();
    currentRow = null;
    currentIndex = AFTER_LAST;
  }

  @Override
  public boolean first() throws SQLException {
    verify.notClosed().notForwardOnly().notInsertRow();

    if (!hasRows) {
      return false;
    }

    currentRow = parser.parse(lineIterator.absolute(1));
    currentIndex = 1;
    return true;
  }

  @Override
  public boolean last() throws SQLException {
    verify.notClosed().notForwardOnly().notInsertRow();

    try {
      lineIterator.last();
    } catch (Exception e) {
      SimSystem.report(e);
      throw new SQLException("File backend failed.", e);
    }

    if (lineIterator.index() == 0) {
      return false;
    }

    currentRow = parser.parse(lineIterator.current());
    currentIndex = lineIterator.index();

    // if we did not know so far how many lines we had we at least know from now
    // on and thus we can use this value which may speedup some methods using
    // this class
    if (rowCount == -1) {
      rowCount = currentIndex;
    }
    return true;
  }

  @Override
  public int getRow() throws SQLException {
    verify.notClosed();

    if (isBeforeFirst() || isAfterLast()) {
      return 0;
    }

    return currentIndex;
  }

  /**
   * @throws IllegalArgumentException
   *           if {@code row} was {@code 0}.
   */
  @Override
  public boolean absolute(int row) throws SQLException {
    verify.notClosed().notForwardOnly().notInsertRow();
    if (row == 0) {
      throw new IllegalArgumentException(
          "The row must be either positive or negative.");
    }

    CharSequence line;

    if (row > 0) {
      line = lineIterator.absolute(row);

      if (line == null) {
        afterLast();
        return false;
      }
    } else {
      lineIterator.last();
      line = lineIterator.relative(row + 1);

      if (line == null) {
        beforeFirst();
        return false;
      }
    }

    currentRow = parser.parse(line);
    currentIndex = lineIterator.index();
    return true;
  }

  @Override
  public boolean relative(int rows) throws SQLException {
    verify.notClosed().notForwardOnly();

    int target = getRow() + rows;

    if (target <= 0) {
      beforeFirst();
      return false;
    }

    return absolute(target);
  }

  @Override
  public boolean previous() throws SQLException {
    verify.notClosed().notForwardOnly().notInsertRow();

    if (isBeforeFirst()) {
      return false;
    } else if (isAfterLast()) {
      return last();
    } else if (getRow() == 1) {
      beforeFirst();
      return false;
    }

    currentIndex--;
    currentRow = parser.parse(lineIterator.previous());
    return true;
  }

  /**
   * @return always {@code false}. This RowSet cannot detect updates.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public boolean rowUpdated() throws SQLException {
    verify.notClosed();

    return false;
  }

  /**
   * @return always {@code false}. This RowSet cannot detect insertions.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public boolean rowInserted() throws SQLException {
    verify.notClosed();

    return false;
  }

  /**
   * @return always {@code false}. This RowSet cannot detect deletions.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public boolean rowDeleted() throws SQLException {
    verify.notClosed();

    return false;
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. Nullable columns are not supported.
   */
  @Override
  public void updateNull(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    verify.notClosed().notReadOnly().column(columnIndex)
        .notBeforeFirstAfterLast();

    set(columnIndex, Boolean.toString(x));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateByte(int columnIndex, byte x) throws SQLException {
    verify.notClosed().notReadOnly().column(columnIndex)
        .notBeforeFirstAfterLast();

    set(columnIndex, Byte.toString(x));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateShort(int columnIndex, short x) throws SQLException {
    verify.notClosed().notReadOnly().column(columnIndex)
        .notBeforeFirstAfterLast();

    set(columnIndex, Short.toString(x));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateInt(int columnIndex, int x) throws SQLException {
    verify.notClosed().notReadOnly().column(columnIndex)
        .notBeforeFirstAfterLast();

    set(columnIndex, Integer.toString(x));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateLong(int columnIndex, long x) throws SQLException {
    verify.notClosed().notReadOnly().column(columnIndex)
        .notBeforeFirstAfterLast();

    set(columnIndex, Long.toString(x));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateFloat(int columnIndex, float x) throws SQLException {
    verify.notClosed().notReadOnly().column(columnIndex)
        .notBeforeFirstAfterLast();

    set(columnIndex, Float.toString(x));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateDouble(int columnIndex, double x) throws SQLException {
    verify.notClosed().notReadOnly().column(columnIndex)
        .notBeforeFirstAfterLast();

    set(columnIndex, Double.toString(x));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateBigDecimal(int columnIndex, BigDecimal x)
      throws SQLException {
    verify.notClosed().notReadOnly().column(columnIndex)
        .notBeforeFirstAfterLast().notNull(x, "The new value may not be null.");

    updateString(columnIndex, x.toString());
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateString(int columnIndex, String x) throws SQLException {
    verify.notClosed().notReadOnly().column(columnIndex)
        .notBeforeFirstAfterLast().notNull(x, "The new value may not be null.");

    set(columnIndex, x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateBytes(int columnIndex, byte[] x) throws SQLException {
    updateString(columnIndex, Base64.encode(x));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateDate(int columnIndex, Date x) throws SQLException {
    updateString(columnIndex, x.toString());
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateTime(int columnIndex, Time x) throws SQLException {
    updateString(columnIndex, x.toString());
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    updateString(columnIndex, x.toString());
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateAsciiStream(int, InputStream, int)}
   *           method is not supported.
   */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateBinaryStream(int, InputStream, int)}
   *           method is not supported.
   */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, int length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateCharacterStream(int, Reader, int)}
   *           method is not supported.
   */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x, int length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  @Override
  public void updateObject(int columnIndex, Object x, int scaleOrLength)
      throws SQLException {
    updateObject(columnIndex, x);
  }

  @Override
  public void updateObject(int columnIndex, Object x) throws SQLException {
    if (x instanceof BigDecimal) {
      updateBigDecimal(columnIndex, (BigDecimal) x);
    } else if (x instanceof Boolean) {
      updateBoolean(columnIndex, (Boolean) x);
    } else if (x instanceof Byte) {
      updateByte(columnIndex, (Byte) x);
    } else if (x instanceof Date) {
      updateDate(columnIndex, (Date) x);
    } else if (x instanceof Double) {
      updateDouble(columnIndex, (Double) x);
    } else if (x instanceof Float) {
      updateFloat(columnIndex, (Float) x);
    } else if (x instanceof Integer) {
      updateInt(columnIndex, (Integer) x);
    } else if (x instanceof Long) {
      updateLong(columnIndex, (Long) x);
    } else if (x instanceof String) {
      updateString(columnIndex, (String) x);
    } else if (x instanceof Time) {
      updateTime(columnIndex, (Time) x);
    } else if (x instanceof Timestamp) {
      updateTimestamp(columnIndex, (Timestamp) x);
    } else if (x instanceof byte[]) {
      updateBytes(columnIndex, (byte[]) x);
    } else {
      // serialize object into byte array and store that
      try {
        updateBytes(columnIndex, SerialisationUtils.serialize((Serializable) x));
      } catch (IOException e) {
        throw new SQLException(e);
      }
    }
    // TODO: byte[]?
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. Nullable columns are not supported.
   */
  @Override
  public void updateNull(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateBoolean(String columnLabel, boolean x) throws SQLException {
    updateBoolean(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateByte(String columnLabel, byte x) throws SQLException {
    updateByte(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateShort(String columnLabel, short x) throws SQLException {
    updateShort(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateInt(String columnLabel, int x) throws SQLException {
    updateInt(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateLong(String columnLabel, long x) throws SQLException {
    updateLong(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateFloat(String columnLabel, float x) throws SQLException {
    updateFloat(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateDouble(String columnLabel, double x) throws SQLException {
    updateDouble(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateBigDecimal(String columnLabel, BigDecimal x)
      throws SQLException {
    updateBigDecimal(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateString(String columnLabel, String x) throws SQLException {
    updateString(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateBytes(String columnLabel, byte[] x) throws SQLException {
    updateBytes(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateDate(String columnLabel, Date x) throws SQLException {
    updateDate(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateTime(String columnLabel, Time x) throws SQLException {
    updateTime(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateTimestamp(String columnLabel, Timestamp x)
      throws SQLException {
    updateTimestamp(findColumn(columnLabel), x);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateAsciiStream(String, InputStream, int)}
   *           method is not supported.
   */
  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, int length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateBinaryStream(String, InputStream, int)}
   *           method is not supported.
   */
  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, int length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateCharacterStream(String, Reader, int)}
   *           method is not supported.
   */
  @Override
  public void updateCharacterStream(String columnLabel, Reader reader,
      int length) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  @Override
  public void updateObject(String columnLabel, Object x, int scaleOrLength)
      throws SQLException {
    updateObject(findColumn(columnLabel), x);
  }

  @Override
  public void updateObject(String columnLabel, Object x) throws SQLException {
    updateObject(findColumn(columnLabel), x);
  }

  @Override
  public void insertRow() throws SQLException {
    verify.notClosed().notReadOnly().insertRow();

    // find out whether all columns have a value
    for (int i = 0; i < insertRowWritten.length; i++) {
      if (!insertRowWritten[i]) {
        throw new SQLException("Not all columns have a value, column "
            + (i + 1) + " is still empty.");
      }
    }

    String line = CSVFormatter.formatCSV(currentRow, ',');

    synchronized (lineIterator) {
      lineIterator.write(line);
      // only update the row count if we are counting the rows in here
      if (rowCount != -1) {
        rowCount++;
      }
    }

    hasRows = true;
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. This implementation does not support row updates.
   */
  @Override
  public void updateRow() throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. This implementation does not support row deletions.
   */
  @Override
  public void deleteRow() throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  @Override
  public void refreshRow() throws SQLException {
    verify.notClosed().notForwardOnly().notInsertRow();

    CharSequence line = lineIterator.current();

    if (line != null) {
      currentRow = parser.parse(line);
    }
  }

  @Override
  public void cancelRowUpdates() throws SQLException {
    verify.notClosed().notReadOnly().notInsertRow();
    refreshRow();
  }

  /**
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void moveToInsertRow() throws SQLException {
    verify.notClosed().notReadOnly();

    isOnInsertRow = true;
    currentRow = new String[metadata.getColumnCount()];
    insertRowWritten = new boolean[metadata.getColumnCount()];
  }

  /**
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void moveToCurrentRow() throws SQLException {
    verify.notClosed().notReadOnly();

    if (isOnInsertRow) {
      isOnInsertRow = false;
      refreshRow();
    }
  }

  /**
   * @return always {@code null}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public Statement getStatement() throws SQLException {
    verify.notClosed();

    return null;
  }

  /**
   * Equivalent to {@link #getString(int)}.
   */
  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map)
      throws SQLException {
    return getString(columnIndex);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Ref} type is not supported.
   */
  @Override
  public Ref getRef(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Blob} type is not supported.
   */
  @Override
  public Blob getBlob(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Clob} type is not supported.
   */
  @Override
  public Clob getClob(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Array} type is not supported.
   */
  @Override
  public Array getArray(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * Equivalent to {@link #getString(String)}.
   */
  @Override
  public Object getObject(String columnLabel, Map<String, Class<?>> map)
      throws SQLException {
    return getString(columnLabel);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Ref} type is not supported.
   */
  @Override
  public Ref getRef(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Blob} type is not supported.
   */
  @Override
  public Blob getBlob(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Clob} type is not supported.
   */
  @Override
  public Clob getClob(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Array} type is not supported.
   */
  @Override
  public Array getArray(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public Date getDate(int columnIndex, Calendar cal) throws SQLException {
    Date d = getDate(columnIndex);
    return new Date(d.getTime() + cal.getTimeZone().getOffset(d.getTime()));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public Date getDate(String columnLabel, Calendar cal) throws SQLException {
    return getDate(findColumn(columnLabel), cal);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public Time getTime(int columnIndex, Calendar cal) throws SQLException {
    Time t = getTime(columnIndex);
    return new Time(t.getTime() + cal.getTimeZone().getOffset(t.getTime()));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public Time getTime(String columnLabel, Calendar cal) throws SQLException {
    return getTime(findColumn(columnLabel), cal);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public Timestamp getTimestamp(int columnIndex, Calendar cal)
      throws SQLException {
    Timestamp t = getTimestamp(columnIndex);
    return new Timestamp(t.getTime() + cal.getTimeZone().getOffset(t.getTime()));
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public Timestamp getTimestamp(String columnLabel, Calendar cal)
      throws SQLException {
    return getTimestamp(findColumn(columnLabel), cal);
  }

  /**
   * @throws SQLException
   *           wrapping a {@link MalformedURLException} if the URL uses an
   *           unknown protocol.
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public java.net.URL getURL(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast()
        .getterOnInsertRow(columnIndex).getterOnInsertRow(columnIndex);

    try {
      return new java.net.URL(getString(columnIndex));
    } catch (MalformedURLException e) {
      throw new SQLException(e);
    }
  }

  /**
   * @throws SQLException
   *           wrapping a {@link MalformedURLException} if the URL uses an
   *           unknown protocol.
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public java.net.URL getURL(String columnLabel) throws SQLException {
    return getURL(findColumn(columnLabel));
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Ref} type is not supported.
   */
  @Override
  public void updateRef(int columnIndex, Ref x) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Ref} type is not supported.
   */
  @Override
  public void updateRef(String columnLabel, Ref x) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Blob} type is not supported.
   */
  @Override
  public void updateBlob(int columnIndex, Blob x) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Blob} type is not supported.
   */
  @Override
  public void updateBlob(String columnLabel, Blob x) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Clob} type is not supported.
   */
  @Override
  public void updateClob(int columnIndex, Clob x) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Clob} type is not supported.
   */
  @Override
  public void updateClob(String columnLabel, Clob x) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Array} type is not supported.
   */
  @Override
  public void updateArray(int columnIndex, Array x) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Array} type is not supported.
   */
  @Override
  public void updateArray(String columnLabel, Array x) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link RowId} type is not supported.
   */
  @Override
  public RowId getRowId(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link RowId} type is not supported.
   */
  @Override
  public RowId getRowId(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link RowId} type is not supported.
   */
  @Override
  public void updateRowId(int columnIndex, RowId x) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link RowId} type is not supported.
   */
  @Override
  public void updateRowId(String columnLabel, RowId x) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   */
  @Override
  public boolean isClosed() throws SQLException {
    return closed;
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateNString(int columnIndex, String nString)
      throws SQLException {
    verify.notClosed().notReadOnly().column(columnIndex)
        .notBeforeFirstAfterLast()
        .notNull(nString, "The string may not be null.");

    updateString(columnIndex, nString);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this {@link RowSet} is set to
   *           {@link ResultSet#CONCUR_READ_ONLY}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public void updateNString(String columnLabel, String nString)
      throws SQLException {
    updateString(columnLabel, nString);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link NClob} type is not supported.
   */
  @Override
  public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link NClob} type is not supported.
   */
  @Override
  public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link NClob} type is not supported.
   */
  @Override
  public NClob getNClob(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link NClob} type is not supported.
   */
  @Override
  public NClob getNClob(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link SQLXML} type is not supported.
   */
  @Override
  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link SQLXML} type is not supported.
   */
  @Override
  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link SQLXML} type is not supported.
   */
  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link SQLXML} type is not supported.
   */
  @Override
  public void updateSQLXML(String columnLabel, SQLXML xmlObject)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IndexOutOfBoundsException} if the column index
   *           is outside the valid range.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public String getNString(int columnIndex) throws SQLException {
    verify.notClosed().column(columnIndex).notBeforeFirstAfterLast();

    return getString(columnIndex);
  }

  /**
   * @throws SQLException
   *           wrapping an {@link IllegalArgumentException} if no column with
   *           that label exists in this {@link RowSet}.
   * @throws SQLException
   *           if this method is called once this {@link RowSet} is closed.
   */
  @Override
  public String getNString(String columnLabel) throws SQLException {
    return getString(columnLabel);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #getNCharacterStream(int)} method is not
   *           supported.
   */
  @Override
  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #getNCharacterStream(String)} method is not
   *           supported.
   */
  @Override
  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateNCharacterStream(int, Reader, long)}
   *           method is not supported.
   */
  @Override
  public void updateNCharacterStream(int columnIndex, Reader x, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateNCharacterStream(String, Reader, long)}
   *           method is not supported.
   */
  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader,
      long length) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateAsciiStream(int, InputStream, long)}
   *           method is not supported.
   */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateBinaryStream(int, InputStream, long)}
   *           method is not supported.
   */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateCharacterStream(int, Reader, long)}
   *           method is not supported.
   */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateAsciiStream(String, InputStream, long)}
   *           method is not supported.
   */
  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The
   *           {@link #updateBinaryStream(String, InputStream, long)} method is
   *           not supported.
   */
  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateCharacterStream(String, Reader, long)}
   *           method is not supported.
   */
  @Override
  public void updateCharacterStream(String columnLabel, Reader reader,
      long length) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Blob} type is not supported.
   */
  @Override
  public void updateBlob(int columnIndex, InputStream inputStream, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Blob} type is not supported.
   */
  @Override
  public void updateBlob(String columnLabel, InputStream inputStream,
      long length) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Clob} type is not supported.
   */
  @Override
  public void updateClob(int columnIndex, Reader reader, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Clob} type is not supported.
   */
  @Override
  public void updateClob(String columnLabel, Reader reader, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link NClob} type is not supported.
   */
  @Override
  public void updateNClob(int columnIndex, Reader reader, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link NClob} type is not supported.
   */
  @Override
  public void updateNClob(String columnLabel, Reader reader, long length)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateNCharacterStream(int, Reader)} method
   *           is not supported.
   */
  @Override
  public void updateNCharacterStream(int columnIndex, Reader x)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateNCharacterStream(String, Reader)}
   *           method is not supported.
   */
  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateAsciiStream(int, InputStream)} method
   *           is not supported.
   */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateBinaryStream(int, InputStream)} method
   *           is not supported.
   */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateCharacterStream(int, Reader)} method is
   *           not supported.
   */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateAsciiStream(String, InputStream)}
   *           method is not supported.
   */
  @Override
  public void updateAsciiStream(String columnLabel, InputStream x)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateBinaryStream(String, InputStream)}
   *           method is not supported.
   */
  @Override
  public void updateBinaryStream(String columnLabel, InputStream x)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link #updateCharacterStream(String, Reader)} method
   *           is not supported.
   */
  @Override
  public void updateCharacterStream(String columnLabel, Reader reader)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Blob} type is not supported.
   */
  @Override
  public void updateBlob(int columnIndex, InputStream inputStream)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Blob} type is not supported.
   */

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Clob} type is not supported.
   */

  @Override
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link Clob} type is not supported.
   */

  @Override
  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link NClob} type is not supported.
   */

  @Override
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLFeatureNotSupportedException
   *           always. The {@link NClob} type is not supported.
   */
  @Override
  public void updateNClob(String columnLabel, Reader reader)
      throws SQLException {
    throw new SQLFeatureNotSupportedException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           if no file name was set by either {@link #setFileName(String)} or
   *           {@link #setDataSourceName(String)}.
   * @throws SQLException
   *           wrapping a {@link FileNotFoundException} if the file could not be
   *           opened.
   * @throws SQLException
   *           wrapping a {@link IOException} if an error occurs during
   *           memory-mapping the file.
   */
  @Override
  public void execute() throws SQLException {
    if (getFileName() == null) {
      throw new SQLException(
          "The file name must be set before calling execute().");
    }

    try {
      lineIterator = new FileLineIterator(getFileName());
      lineIterator.setEncoding(encoding);
      lineIterator.setBufferSize(bufferSize);
    } catch (IOException e) {
      throw new SQLException(e);
    }

    fileOpened = true;
    closed = false;

    // read headers and set metadata
    metadata = new RowSetMetaDataImpl();

    CharSequence line = lineIterator.current();
    String[] fields = parser.parse(line);

    metadata.setColumnCount(fields.length);
    for (int i = 0; i < fields.length; i++) {
      metadata.setColumnName(i + 1, fields[i]);
      metadata.setColumnLabel(i + 1, fields[i]);
    }

    hasRows = lineIterator.next() != null;

    if (!hasRows) {
      rowCount = 0;
    }

    lineIterator.previous();

    currentIndex = BEFORE_FIRST;
  }

  /**
   * Sets the encoding to use for the file's contents. Defaults to UTF-8.
   * 
   * @param newEncoding
   *          The new encoding to use.
   * @throws UnsupportedOperationException
   *           if the file was already opened.
   * @throws NullPointerException
   *           if the new encoding was {@code null}.
   */
  public void setEncoding(Charset newEncoding) {
    verify.fileOpened().notNull(newEncoding, "The encoding may not be null.");

    encoding = newEncoding;
  }

  /**
   * Retrieves the currently selected character encoding for the file's
   * contents.
   * 
   * @return The current character encoding for the file.
   */
  public Charset getEncoding() {
    return lineIterator != null ? lineIterator.getEncoding() : encoding;
  }

  /**
   * Set the buffer size of the FileLineIterator used. See
   * {@link FileLineIterator#setBufferSize(int)} for some more details on the
   * buffer. The larger the buffer the less frequent accesses to the disc might
   * occur - but the more memory is consumed.
   * 
   * @param newBufferSize
   */
  public void setBufferSize(int newBufferSize) {
    if (lineIterator != null) {
      lineIterator.setBufferSize(newBufferSize);
    }
  }

  // ==========================================================================
  // Inapplicable methods, dealing with properties that are unused or parameter
  // handling in the command.
  // ==========================================================================

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setArray(int parameterIndex, Array array) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setAsciiStream(int parameterIndex, InputStream x)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setAsciiStream(String parameterName, InputStream x)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setAsciiStream(String parameterName, InputStream x, int length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBigDecimal(String parameterName, BigDecimal x)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBinaryStream(int parameterIndex, InputStream x)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBinaryStream(String parameterName, InputStream x)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBinaryStream(String parameterName, InputStream x, int length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBlob(int parameterIndex, Blob x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBlob(int parameterIndex, InputStream inputStream)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBlob(String parameterName, Blob x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBlob(String parameterName, InputStream inputStream)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBlob(String parameterName, InputStream inputStream, long length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBoolean(String parameterName, boolean x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setByte(String parameterName, byte x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setBytes(String parameterName, byte[] x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setCharacterStream(int parameterIndex, Reader reader)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setCharacterStream(String parameterName, Reader reader)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setCharacterStream(String parameterName, Reader reader, int length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setClob(int parameterIndex, Clob x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setClob(int parameterIndex, Reader reader, long length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setClob(String parameterName, Clob x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setClob(String parameterName, Reader reader) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setClob(String parameterName, Reader reader, long length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setDate(String parameterName, Date x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setDate(String parameterName, Date x, Calendar cal)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setDouble(String parameterName, double x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setFloat(String parameterName, float x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setInt(String parameterName, int x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setLong(String parameterName, long x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNCharacterStream(int parameterIndex, Reader value)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNCharacterStream(int parameterIndex, Reader value, long length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNCharacterStream(String parameterName, Reader value)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNCharacterStream(String parameterName, Reader value,
      long length) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNClob(int parameterIndex, Reader reader, long length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNClob(String parameterName, NClob value) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNClob(String parameterName, Reader reader) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNClob(String parameterName, Reader reader, long length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNString(int parameterIndex, String value) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNString(String parameterName, String value)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNull(int parameterIndex, int sqlType, String typeName)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNull(String parameterName, int sqlType) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setNull(String parameterName, int sqlType, String typeName)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setObject(int parameterIndex, Object x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType,
      int scale) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setObject(String parameterName, Object x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setObject(String parameterName, Object x, int targetSqlType)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setObject(String parameterName, Object x, int targetSqlType,
      int scale) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setRef(int parameterIndex, Ref ref) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setShort(String parameterName, short x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setRowId(String parameterName, RowId x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setSQLXML(String parameterName, SQLXML xmlObject)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setString(String parameterName, String x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setTime(String parameterName, Time x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setTime(String parameterName, Time x, Calendar cal)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setTimestamp(String parameterName, Timestamp x)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setURL(int parameterIndex, java.net.URL x) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setUnicodeStream(int parameterIndex, InputStream x, int length)
      throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void clearParameters() throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @return always {@code null} in this implementation.
   */
  @Override
  public String getUsername() {
    return null;
  }

  /**
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setUsername(String name) {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @return always {@code null} in this implementation.
   */
  @Override
  public String getPassword() {
    return null;
  }

  /**
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setPassword(String password) {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @return always {@code null} in this implementation.
   */
  @Override
  public String getCommand() {
    return null;
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setCommand(String cmd) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @return Always {@code null} in this implementation.
   * @throws SQLException
   *           never in this implementation.
   */
  @Override
  public String getUrl() throws SQLException {
    return null;
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public void setUrl(String url) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * @throws SQLException
   *           always.
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLException(EX_NOT_APPLICABLE);
  }

  /**
   * @return always {@code false}.
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }

  /**
   * @throws SQLException
   *           never in this implementation.
   * @throws UnsupportedOperationException
   *           always. This method is not applicable.
   */
  @Override
  public int getHoldability() throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  // ==========================================================================
  // Helper class
  // ==========================================================================

  /**
   * Verification helper class to enable a more fluent style of checking
   * preconditions of methods.
   * <p>
   * Method chaining is used to enable checking common preconditions to methods
   * in a single line of code:
   * 
   * <pre>
   * Verification verify = new Verification();
   * verify.fileOpened().forwardOnly().columnIndex(columnIndex);
   * </pre>
   * <p>
   * The individual methods will automatically throw exceptions if the stated
   * conditions do not hold.
   * 
   * @author Johannes Rössel
   */
  private class Verification implements Serializable {
    /**
     * The constant serial version uid.
     */
    private static final long serialVersionUID = -2853419795994906049L;

    /**
     * Checks whether the file has been opened already. Methods that try setting
     * properties that are important to set <em>before</em> that should fail.
     * E.g. setting the file encoding or the file name as soon as the file has
     * been opened is not a good idea.
     * 
     * @return This {@link Verification} object.
     * @throws UnsupportedOperationException
     *           if the file already has been opened.
     */
    public Verification fileOpened() {
      if (fileOpened) {
        throw new UnsupportedOperationException(
            "This method cannot be called once the file was opened.");
      }
      return this;
    }

    /**
     * Checks that an argument is not {@code null}. Otherwise a
     * {@link NullPointerException} is thrown.
     * 
     * @param o
     *          The object to check for being {@code null}.
     * @param message
     *          The exception message to use if the given object is {@code null}
     *          .
     * @return This {@link Verification} object.
     * @throws NullPointerException
     *           if the given argument was {@code null}.
     */
    public Verification notNull(Object o, String message) {
      if (o == null) {
        throw new FileRowSetException(message);
      }
      return this;
    }

    /**
     * Checks whether the {@link RowSet} was closed already in which case nearly
     * all methods are no longer applicable.
     * 
     * @return This {@link Verification} object.
     * @throws SQLException
     *           if the {@link RowSet} was closed already.
     */
    public Verification notClosed() throws SQLException {
      if (isClosed()) {
        throw new SQLException(
            "This method cannot be called on a closed RowSet.");
      }
      return this;
    }

    /**
     * Checks whether this {@link RowSet} is of type
     * {@link ResultSet#TYPE_FORWARD_ONLY}. In this case the cursor cannot be
     * iterated backwards and random access to rows is not possible which causes
     * some methods, such as {@code next()} to fail.
     * 
     * @return This {@link Verification} object.
     * @throws SQLException
     *           if the {@link RowSet} is of type
     *           {@link ResultSet#TYPE_FORWARD_ONLY}.
     */
    public Verification notForwardOnly() throws SQLException {
      if (getType() == ResultSet.TYPE_FORWARD_ONLY) {
        throw new SQLException("This method cannot be called when the type of "
            + "this RowSet is TYPE_FORWARD_ONLY.");
      }
      return this;
    }

    /**
     * Checks whether this {@link RowSet} is read-only (
     * {@link ResultSet#CONCUR_READ_ONLY}). This causes all methods to fail that
     * update values.
     * 
     * @return This {@link Verification} object.
     * @throws SQLException
     *           if the {@link RowSet} has its concurrency set to
     *           {@link ResultSet#CONCUR_READ_ONLY}.
     */
    public Verification notReadOnly() throws SQLException {
      if (getConcurrency() == ResultSet.CONCUR_READ_ONLY) {
        throw new SQLException(
            "This method cannot be called when the concurrency "
                + "of this RowSet is CONCUR_READ_ONLY.");
      }
      return this;
    }

    /**
     * Checks whether the given column index is valid. Columns are numbered
     * starting with 1, so any index outside the range of
     * {@code 1..getMetaData().getColumnCount()} is invalid.
     * 
     * @param columnIndex
     *          The column index to verify.
     * @return This {@link Verification} object.
     * @throws SQLException
     *           wrapping an {@link IndexOutOfBoundsException} if the column
     *           index was outside the valid range.
     */
    public Verification column(int columnIndex) throws SQLException {
      if (columnIndex < 1
          || columnIndex > getMetaDataInternal().getColumnCount()) {
        throw new SQLException(new IndexOutOfBoundsException(
            "The column index is invalid."));
      }
      return this;
    }

    /**
     * Checks whether the cursor currently is on the insert row and throws an
     * exception if otherwise.
     * 
     * @return This {@link Verification} object.
     * @throws SQLException
     *           if the cursor is currently not on the insert row.
     */
    public Verification insertRow() throws SQLException {
      if (!isOnInsertRow) {
        throw new SQLException(
            "This method may only be called when the cursor is "
                + "on the insert row.");
      }
      return this;
    }

    /**
     * Checks whether the cursor currently is not on the insert row and throws
     * an exception if it is.
     * 
     * @return This {@link Verification} object.
     * @throws SQLException
     *           if the cursor is currently on the insert row.
     */
    public Verification notInsertRow() throws SQLException {
      if (isOnInsertRow) {
        throw new SQLException(
            "This method may not be called when the cursor is "
                + "on the insert row.");
      }
      return this;
    }

    /**
     * Checks whether the cursor currently is on the insert row and an updater
     * method was called already for this column. If not, an exception is
     * thrown.
     * 
     * @param columnIndex
     *          The column index, as this is needed to check whether there
     *          already was a value written in that column.
     * 
     * @return This {@link Verification} object.
     * @throws SQLException
     *           if the cursor is currently on the insert row.
     */
    public Verification getterOnInsertRow(int columnIndex) throws SQLException {
      if (isOnInsertRow && !insertRowWritten[columnIndex - 1]) {
        throw new SQLException("Getter methods may be called only after "
            + "an updater method was called for that column.");
      }
      return this;
    }

    /**
     * Checks that the current row is not before the first or after the last row
     * and throws an exception otherwise.
     * 
     * @return This {@link Verification} object.
     * @throws SQLException
     *           if the cursor is currently either before the first or after the
     *           last row.
     */
    public Verification notBeforeFirstAfterLast() throws SQLException {
      if (!isOnInsertRow && (isBeforeFirst() || isAfterLast())) {
        throw new SQLException(
            "This method may not be called when before the first "
                + "or after the last row.");
      }
      return this;
    }
  }

  // TODO: Compatibility issue Java 6 vs. 7, add override tag after transition
  @Override
  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  // TODO: Compatibility issue Java 6 vs. 7, add override tag after transition
  @Override
  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    throw new UnsupportedOperationException(EX_NOT_APPLICABLE);
  }

  /**
   * Fixed size line cache. Not finished and currently not used.
   * 
   * @author Jan Himmelspach
   * 
   */
  @SuppressWarnings("unused")
  private class Cache {

    private int absoluteRowOfFirstEntry;

    private int firstEntry;

    private int current;

    private String[][] lines;

    public Cache(int size) {
      lines = new String[size][];
      absoluteRowOfFirstEntry = firstEntry = current = 0;
    }

    public void add(String[] data) {
      current++;

      if (current == lines.length) {
        current = 0;
      }

      // if full we need to flush
      if (current == firstEntry) {
        // do a partial flush, i.e., free a pre-defined number of entries in the
        // cache
        partialFlush();
      }
      lines[current] = data;
    }

    public void partialFlush() {

      long free = Math.round(lines.length * .25);

      for (int count = 0; count < free; count++) {

        write(firstEntry + count);

        // remove the data
        lines[firstEntry + count] = null;

        // new firstRow
        firstEntry++;

        absoluteRowOfFirstEntry++;

        // wrap around
        if (firstEntry == lines.length) {
          firstEntry = 0;
        }
      }

    }

    public void flush() {
      int written = 0;
      if (current >= firstEntry) {
        for (int i = firstEntry; i <= current; i++) {
          write(i);
          lines[i] = null;
          written++;
        }
      } else {
        for (int i = firstEntry; i < lines.length; i++) {
          write(i);
          lines[i] = null;
          written++;
        }
        for (int i = 0; i <= current; i++) {
          write(i);
          lines[i] = null;
          written++;
        }
      }
      absoluteRowOfFirstEntry += written;
      current = firstEntry = 0;
    }

    /**
     * Write the given line to the line iterator.
     * 
     * @param index
     */
    private void write(int index) {
      String line = CSVFormatter.formatCSV(lines[index], ',');
    }

    public String[] read(int index) {
      int relative = index - absoluteRowOfFirstEntry;

      // move to the slot in the buffer
      relative += firstEntry;

      // if we are beyond the limits we have to wrap
      if (relative > lines.length) {
        relative -= lines.length;
        if (relative > firstEntry) {
          return null;
        }
      }

      return lines[relative];
    }

  }

  /**
   * Return the row count value.
   * 
   * @return number of rows or -1 if row counting is not supported.
   */
  public int getRowCount() {
    return rowCount;
  }

}
