/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetInternal;
import javax.sql.RowSetListener;
import javax.sql.RowSetMetaData;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.ListenerSupport;

/**
 * Provides transparent access to tabular data that should be retrieved/stored
 * from/to a file or database. <br>
 * 
 * @author Steffen Maas
 * @author Thomas Nösinger,
 * @author last change by $Author$
 * @version 0.$Rev$ <br>
 *          $Date$
 */
public class RowSetImpl implements RowSet, RowSetInternal {

  /** The EXC. */
  private static final String EXC =
      "The called method isn't executable for the given dummy database.";

  /** The column index buffer. */
  private int columnIndexBuffer = 0; // remember the last read column

  /** The content. */
  private Vector<Object[]> content = new Vector<>(0);

  /** The cursor. */
  private int cursor = -1; // the sql cursor

  /** The cursor buffer. */
  private int cursorBuffer = -1; // remember the cursor position

  /** The datasourcename. */
  private String datasourcename;

  /** The depexc. */
  private String depexc = "The called method was deprecated.";

  /** The insert row. */
  private Object[] insertRow;

  /** The isreadonly. */
  private boolean isreadonly = false; // false: updates possible / true: only

  /** The rsconcurlevel. */
  private int rsconcurlevel = 2; // 1: CONCUR_READ_ONLY, 2: CONCUR_UPDATABLE

  /** The rsmd. */
  private RowSetMetaData rsmd = new RowSetMetaDataImpl();

  // reads allowed

  /** The rstype. */
  private int rstype = 2; // 1: TYPE_FORWARD_ONLY, 2: TYPE_SCROLL_INSENSITIVE,

  // 3: TYPE_SCROLL_SENSITIVE

  /** The tolongexc. */
  private String tolongexc =
      "The length is greater then the max integervalue. A lossless processing isn't possible.";

  /** the list of registered listeners */
  private ListenerSupport<RowSetListener> listeners = new ListenerSupport<>();

  /** the event sent around when the row set has changed */
  private RowSetEvent rowSetEvent = new RowSetEvent(this);

  @Override
  public boolean absolute(int row) throws SQLException {
    if (rstype == 1) {
      throw new SQLException("The type is TYPE_FORWARD_ONLY.");
    }
    if (java.lang.Math.abs(row) > getContent().size() || row == 0) {
      if (row <= 0) {
        beforeFirst();
      } else {
        if (row > getContent().size()) {
          afterLast();
        } else {
          last();
        }
      }
      return false;
    }
    if (row < 0) {
      cursor = getContent().size() + row;
    } else {
      cursor = row - 1;
    }
    return true;

  }

  @Override
  public void addRowSetListener(RowSetListener listener) {
    listeners.add(listener);
  }

  @Override
  public void afterLast() throws SQLException {
    cursor = getContent().size();
  }

  /**
   * Sets the cursor before the first row (-1).
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#beforeFirst()
   */
  @Override
  public void beforeFirst() throws SQLException {
    cursor = -1;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#cancelRowUpdates()
   */
  @Override
  public void cancelRowUpdates() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSet#clearParameters()
   */

  @Override
  public void clearParameters() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#clearWarnings()
   */
  @Override
  public void clearWarnings() throws SQLException {

  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#close()
   */
  @Override
  public void close() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * Deletes the current row from this ResultSet object.
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#deleteRow()
   */
  @Override
  public void deleteRow() throws SQLException {
    if (rsconcurlevel == 1) {
      throw new SQLException("The concurrency is READ_ONLY");
    }
    getContent().removeElementAt(cursor);
    cursor--;
    notifyRowSetChange();
  }

  /**
   * Calls the readData method to fill this object with data.
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSet#execute()
   */

  @Override
  public void execute() throws SQLException {
    RowSetCSVReader reader = new RowSetCSVReader();
    reader.readData(this);
  }

  /**
   * Returns the columnindex of the a given columnname.
   * 
   * @param columnName
   *          the column name
   * 
   * @return the columnindex of the columnname, if the columnName doesn't exist
   *         a value greater than the existing number of columnindizes is
   *         returned
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#findColumn(java.lang.String)
   */
  @Override
  public int findColumn(String columnName) throws SQLException {
    for (int i = 0; i < rsmd.getColumnCount(); i++) {
      if ((rsmd.getColumnName(i + 1)).equals(columnName)) {
        return (i + 1);
      }
    }
    return -1;
  }

  /**
   * Sets the cursor on the first position (= 0).
   * 
   * @return true if the new value of the cursor is 0 and false if not
   * 
   * @throws SQLException
   *           if the type is TYPE_FORWARD_ONLY
   * 
   * @see java.sql.ResultSet#first()
   */
  @Override
  public boolean first() throws SQLException {
    if (rstype == 1) {
      throw new SQLException("The type is TYPE_FORWARD_ONLY.");
    }
    if (getContent() == null) {
      return false;
    }
    cursor = 0;
    return true;

  }

  @Override
  public Array getArray(int columnIndex) throws SQLException {
    return getColumnInCurrentRow(columnIndex);
  }

  @Override
  public Array getArray(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    return getArray(columnIndex);
  }

  @Override
  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    return getColumnInCurrentRow(columnIndex);

  }

  @Override
  public InputStream getAsciiStream(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);

    return getAsciiStream(columnIndex);
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    BigDecimal obj = getColumnInCurrentRow(columnIndex);

    return obj;
  }

  @Override
  @Deprecated
  public BigDecimal getBigDecimal(int columnIndex, int scale)
      throws SQLException {
    throw new SQLException(depexc);
  }

  @Override
  public BigDecimal getBigDecimal(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);

    return getBigDecimal(columnIndex);
  }

  @Override
  @Deprecated
  public BigDecimal getBigDecimal(String columnName, int scale)
      throws SQLException {

    throw new SQLException(depexc);
  }

  @Override
  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    return getColumnInCurrentRow(columnIndex);

  }

  @Override
  public InputStream getBinaryStream(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);

    return getBinaryStream(columnIndex);
  }

  @Override
  public Blob getBlob(int columnIndex) throws SQLException {
    return getColumnInCurrentRow(columnIndex);
  }

  @Override
  public Blob getBlob(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    return getBlob(columnIndex);
  }

  @Override
  public boolean getBoolean(int columnIndex) throws SQLException {
    Integer obj = getColumnInCurrentRow(columnIndex);

    if (obj == 1) {
      return true;
    }
    return false;

  }

  @Override
  public boolean getBoolean(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);

    return getBoolean(columnIndex);
  }

  @Override
  public byte getByte(int columnIndex) throws SQLException {
    Byte obj = getColumnInCurrentRow(columnIndex);

    if (obj == null) {
      return 0;
    }
    return obj;

  }

  @Override
  public byte getByte(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);

    return getByte(columnIndex);

  }

  @Override
  public byte[] getBytes(int columnIndex) throws SQLException {
    return getColumnInCurrentRow(columnIndex);

  }

  @Override
  public byte[] getBytes(String columnName) throws SQLException {
    return getColumnInCurrentRow(columnName);
  }

  @Override
  public Reader getCharacterStream(int columnIndex) throws SQLException {
    return getColumnInCurrentRow(columnIndex);
  }

  @Override
  public Reader getCharacterStream(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    return getCharacterStream(columnIndex);
  }

  @Override
  public Clob getClob(int columnIndex) throws SQLException {
    return getColumnInCurrentRow(columnIndex);
  }

  @Override
  public Clob getClob(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    return getClob(columnIndex);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return NULL
   * 
   * @see javax.sql.RowSet#getCommand()
   */
  @Override
  public String getCommand() {
    return null;
  }

  @Override
  public int getConcurrency() throws SQLException {
    return rsconcurlevel;
  }

  @Override
  public Connection getConnection() throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public String getCursorName() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * Return the data of the given columnIndex.
   * 
   * @param <D>
   *          the type the return value shal be converted to
   * 
   * @param columnIndex
   *          the column index in the current row
   * 
   * @return value of the column index of the current row (converted to type D)
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @SuppressWarnings("unchecked")
  public <D> D getData(int columnIndex) throws SQLException {
    // suppress warnings:unchecked - type conversion to generic return type
    return (D) getObject(columnIndex);
  }

  @Override
  public String getDataSourceName() {
    return datasourcename;
  }

  @Override
  public Date getDate(int columnIndex) throws SQLException {
    return getColumnInCurrentRow(columnIndex);
  }

  @Override
  public Date getDate(int columnIndex, Calendar cal) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public Date getDate(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);

    return getDate(columnIndex);

  }

  @Override
  public Date getDate(String columnName, Calendar cal) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public double getDouble(int columnIndex) throws SQLException {
    Double obj = getColumnInCurrentRow(columnIndex);

    if (obj == null) {
      return 0;
    }
    return obj;

  }

  @Override
  public double getDouble(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);

    return getDouble(columnIndex);

  }

  @Override
  public boolean getEscapeProcessing() throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public int getFetchDirection() throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public int getFetchSize() throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public float getFloat(int columnIndex) throws SQLException {

    Float obj = getColumnInCurrentRow(columnIndex);

    if (obj == null) {
      return 0;
    }
    return obj;

  }

  @Override
  public float getFloat(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);

    return getFloat(columnIndex);

  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return 0
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public int getHoldability() throws SQLException {
    return 0;
  }

  @Override
  public int getInt(int columnIndex) throws SQLException {
    Integer obj = getColumnInCurrentRow(columnIndex);

    if (obj == null) {
      return 0;
    }
    return obj;

  }

  @Override
  public int getInt(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);

    return getInt(columnIndex);
  }

  @Override
  public long getLong(int columnIndex) throws SQLException {
    Long obj = getColumnInCurrentRow(columnIndex);

    if (obj == null) {
      return 0;
    }
    return obj;

  }

  @Override
  public long getLong(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);

    Long obj = getColumnInCurrentRow(columnIndex);

    if (obj == null) {
      return 0;
    }
    return obj;

  }

  @Override
  public int getMaxFieldSize() throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public int getMaxRows() throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return rsmd;
  }

  @Override
  public Reader getNCharacterStream(int arg0) throws SQLException {
    return null;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * 
   * @return NULL
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public Reader getNCharacterStream(String arg0) throws SQLException {
    return null;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * 
   * @return NULL
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public NClob getNClob(int arg0) throws SQLException {
    return null;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * 
   * @return NULL
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public NClob getNClob(String arg0) throws SQLException {
    return null;
  }

  /**
   * The called method isn't ex ecutable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * 
   * @return NULL
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public String getNString(int arg0) throws SQLException {
    return null;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * 
   * @return NULL
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public String getNString(String arg0) throws SQLException {
    return null;
  }

  @Override
  public Object getObject(int columnIndex) throws SQLException {
    columnIndexBuffer = columnIndex;

    if (columnIndex > rsmd.getColumnCount() || columnIndex < 1) {
      throw new SQLException("The called columnindex doesn't exist.");
    }

    Object[] row = getContent().get(cursor);
    Object obj = row[columnIndex - 1];

    return obj;

  }

  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map)
      throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public Object getObject(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    return getObject(columnIndex);
  }

  @Override
  public Object getObject(String columnName, Map<String, Class<?>> map)
      throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return the original
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSetInternal#getOriginal()
   */
  @Override
  public ResultSet getOriginal() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return the original row
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSetInternal#getOriginalRow()
   */
  @Override
  public ResultSet getOriginalRow() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return the params
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSetInternal#getParams()
   */
  @Override
  public Object[] getParams() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return NULL
   * 
   * @see javax.sql.RowSet#getPassword()
   */
  @Override
  public String getPassword() {
    return null;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return the query timeout
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSet#getQueryTimeout()
   */
  @Override
  public int getQueryTimeout() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param columnIndex
   *          the column index
   * 
   * @return the ref
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getRef(int)
   */

  @Override
  public Ref getRef(int columnIndex) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param columnName
   *          the column name
   * 
   * @return the ref
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getRef(java.lang.String)
   */

  @Override
  public Ref getRef(String columnName) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * Retrieves the current row number.
   * 
   * @return cursorposition
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getRow()
   */
  @Override
  public int getRow() throws SQLException {
    return cursor;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * 
   * @return NULL
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public RowId getRowId(int arg0) throws SQLException {
    return null;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * 
   * @return NULL
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public RowId getRowId(String arg0) throws SQLException {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * ResultSet object.
   * 
   * @param columnIndex
   *          the column index
   * 
   * @return value of the columnindex in the current row; if the value is NULL,
   *         the value returned is 0
   * 
   * @throws SQLException
   *           if the columnindex doesn't exist
   * 
   * @see java.sql.ResultSet#getShort(int)
   */
  @Override
  public short getShort(int columnIndex) throws SQLException {
    columnIndexBuffer = columnIndex;

    if (columnIndex > rsmd.getColumnCount() || columnIndex < 1) {
      throw new SQLException("The called columnindex doesn't exist.");
    }

    Object[] row = getContent().get(cursor);
    Short obj = (Short) row[columnIndex - 1];

    if (obj == null) {
      return 0;
    }
    return obj;

  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * ResultSet object.
   * 
   * @param columnName
   *          the column name
   * 
   * @return value of the columnname in the current row; if the value is NULL,
   *         the value returned is 0
   * 
   * @throws SQLException
   *           if the columnname doesn't exist
   * 
   * @see java.sql.ResultSet#getShort(java.lang.String)
   */
  @Override
  public short getShort(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    columnIndexBuffer = columnIndex;

    if (columnIndex > rsmd.getColumnCount() || columnIndex < 1) {
      throw new SQLException("The called columnname doesn't exist.");
    }

    Object[] row = getContent().get(cursor);
    Short obj = (Short) row[columnIndex - 1];

    if (obj == null) {
      return 0;
    }
    return obj;

  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * 
   * @return NULL
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public SQLXML getSQLXML(int arg0) throws SQLException {
    return null;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * 
   * @return NULL
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public SQLXML getSQLXML(String arg0) throws SQLException {
    return null;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return the statement
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getStatement()
   */
  @Override
  public Statement getStatement() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * ResultSet object as a String.
   * 
   * @param columnIndex
   *          the column index
   * 
   * @return value of the columnindex in the current row
   * 
   * @throws SQLException
   *           if the columnindex doesn't exist
   * 
   * @see java.sql.ResultSet#getString(int)
   */
  @Override
  public String getString(int columnIndex) throws SQLException {
    columnIndexBuffer = columnIndex;

    if (columnIndex > rsmd.getColumnCount() || columnIndex < 1) {
      throw new SQLException("The called columnindex doesn't exist.");
    }

    Object[] row = getContent().get(cursor);
    String obj = (String) row[columnIndex - 1];

    if (obj == null) {
      return null;
    }
    return obj;

  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * ResultSet object as a String.
   * 
   * @param columnName
   *          the column name
   * 
   * @return value of the columnname in the current row
   * 
   * @throws SQLException
   *           if the columnname doesn't exist
   * 
   * @see java.sql.ResultSet#getString(java.lang.String)
   */
  @Override
  public String getString(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    columnIndexBuffer = columnIndex;

    if (columnIndex > rsmd.getColumnCount() || columnIndex < 1) {
      throw new SQLException("The called columnname doesn't exist.");
    }

    Object[] row = getContent().get(cursor);
    String obj = (String) row[columnIndex - 1];

    if (obj == null) {
      return null;
    }
    return obj;

  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * ResultSet object.
   * 
   * @param columnIndex
   *          the column index
   * 
   * @return value of the columnindex in the current row
   * 
   * @throws SQLException
   *           if the columnindex doesn't exist
   * 
   * @see java.sql.ResultSet#getTime(int)
   */
  @Override
  public Time getTime(int columnIndex) throws SQLException {
    columnIndexBuffer = columnIndex;

    if (columnIndex > rsmd.getColumnCount() || columnIndex < 1) {
      throw new SQLException("The called columnindex doesn't exist.");
    }

    Object[] row = getContent().get(cursor);
    Time obj = (Time) row[columnIndex - 1];

    if (obj == null) {
      return null;
    }
    return obj;

  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param columnIndex
   *          the column index
   * @param cal
   *          the cal
   * 
   * @return the time
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
   */

  @Override
  public Time getTime(int columnIndex, Calendar cal) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * ResultSet object.
   * 
   * @param columnName
   *          the column name
   * 
   * @return value of the columnname in the current row
   * 
   * @throws SQLException
   *           if the columnname doesn't exist
   * 
   * @see java.sql.ResultSet#getTime(java.lang.String)
   */
  @Override
  public Time getTime(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    columnIndexBuffer = columnIndex;

    if (columnIndex > rsmd.getColumnCount() || columnIndex < 1) {
      throw new SQLException("The called columnname doesn't exist.");
    }

    Object[] row = getContent().get(cursor);
    Time obj = (Time) row[columnIndex - 1];

    if (obj == null) {
      return null;
    }
    return obj;

  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param columnName
   *          the column name
   * @param cal
   *          the cal
   * 
   * @return the time
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
   */

  @Override
  public Time getTime(String columnName, Calendar cal) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * ResultSet object.
   * 
   * @param columnIndex
   *          the column index
   * 
   * @return value of the columnindex in the current row
   * 
   * @throws SQLException
   *           if the columnindex doesn't exist
   * 
   * @see java.sql.ResultSet#getTimestamp(int)
   */
  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    columnIndexBuffer = columnIndex;

    if (columnIndex > rsmd.getColumnCount() || columnIndex < 1) {
      throw new SQLException("The called columnindex doesn't exist.");
    }

    Object[] row = getContent().get(cursor);
    Timestamp obj = (Timestamp) row[columnIndex - 1];

    if (obj == null) {
      return null;
    }
    return obj;

  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param columnIndex
   *          the column index
   * @param cal
   *          the cal
   * 
   * @return the timestamp
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
   */

  @Override
  public Timestamp getTimestamp(int columnIndex, Calendar cal)
      throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public Timestamp getTimestamp(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    return getTimestamp(columnIndex);

  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param columnName
   *          the column name
   * @param cal
   *          the cal
   * 
   * @return the timestamp
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
   */

  @Override
  public Timestamp getTimestamp(String columnName, Calendar cal)
      throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return 0
   * 
   * @see javax.sql.RowSet#getTransactionIsolation()
   */
  @Override
  public int getTransactionIsolation() {
    return 0;
  }

  /**
   * Returns the type of this RowSet object.
   * 
   * @return 1 - ResultSet.TYPE_FORWARD_ONLY; 2 -
   *         ResultSet.TYPE_SCROLL_INSENSITIVE; 3 -
   *         ResultSet.TYPE_SCROLL_SENSITIVE
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getType()
   */
  @Override
  public int getType() throws SQLException {
    return rstype;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return the type map
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSet#getTypeMap()
   */
  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The method is deprecated.
   * 
   * @param columnIndex
   *          the column index
   * 
   * @return the unicode stream
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getUnicodeStream(int)
   * @deprecated
   */
  @Override
  @Deprecated
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    throw new SQLException(depexc);
  }

  /**
   * The Method is deprecated.
   * 
   * @param columnName
   *          the column name
   * 
   * @return the unicode stream
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
   * @deprecated
   */
  @Override
  @Deprecated
  public InputStream getUnicodeStream(String columnName) throws SQLException {

    throw new SQLException(depexc);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return the url
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSet#getUrl()
   */
  @Override
  public String getUrl() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param columnIndex
   *          the column index
   * 
   * @return the URL
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getURL(int)
   */

  @Override
  public URL getURL(int columnIndex) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param columnName
   *          the column name
   * 
   * @return the URL
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getURL(java.lang.String)
   */

  @Override
  public URL getURL(String columnName) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return NULL
   * 
   * @see javax.sql.RowSet#getUsername()
   */
  @Override
  public String getUsername() {
    return null;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return the warnings
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#getWarnings()
   */
  @Override
  public SQLWarning getWarnings() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * Inserts the contents of the insert row into this ResultSet object.
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#insertRow()
   */
  @Override
  public void insertRow() throws SQLException {
    if (rsconcurlevel == 1) {
      throw new SQLException("The concurrency is READ_ONLY");
    }
    if (insertRow.length != 0) {
      getContent().add(insertRow);
      last();
    }
    insertRow = null;
    notifyRowSetChange();
  }

  /**
   * Checks if the cursor is after the last row.
   * 
   * @return true if the value of the cursor >= content.size() and false
   *         otherwise
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#isAfterLast()
   */
  @Override
  public boolean isAfterLast() throws SQLException {
    if (cursor >= getContent().size()) {
      return true;
    }
    return false;
  }

  /**
   * Checks if the cursor is before the first row.
   * 
   * @return true if the value of the cursor < 0 and false otherwise
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#isBeforeFirst()
   */
  @Override
  public boolean isBeforeFirst() throws SQLException {
    if (cursor < 0) {
      return true;
    }
    return false;
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return false
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public boolean isClosed() throws SQLException {
    return false;
  }

  /**
   * Checks if the cursor is on the first row.
   * 
   * @return true if the value of the cursor = 0 and false otherwise
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#isFirst()
   */
  @Override
  public boolean isFirst() throws SQLException {
    if (cursor == 0) {
      return true;
    }
    return false;
  }

  /**
   * Checks if the cursor is on the last row.
   * 
   * @return true if the value of the cursor = content.size()-1 and false
   *         otherwise
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#isLast()
   */
  @Override
  public boolean isLast() throws SQLException {
    if (cursor == getContent().size() - 1) {
      return true;
    }
    return false;
  }

  /**
   * Retrieves whether this RowSet object is read-only. The default is to be
   * updateable (false).
   * 
   * @return true if this RowSet object is read-only; false if it is updatable
   * 
   *         Note: the method isn't available for the given dummy database
   * @see javax.sql.RowSet#isReadOnly()
   */
  @Override
  public boolean isReadOnly() {
    return isreadonly;
  }

  /**
   * The method isn't implemented.
   * 
   * @param arg0
   *          the arg0
   * 
   * @return false
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public boolean isWrapperFor(Class<?> arg0) throws SQLException {
    return false;
  }

  /**
   * Sets the cursor on last row position (content.size()-1).
   * 
   * @return true if the new value of the cursor is content.size()-1 and false
   *         if not
   * 
   * @throws SQLException
   *           if the type is TYPE_FORWARD_ONLY
   * 
   * @see java.sql.ResultSet#last()
   */
  @Override
  public boolean last() throws SQLException {
    if (rstype == 1) {
      throw new SQLException("The type is TYPE_FORWARD_ONLY.");
    }
    if (getContent() == null) {
      return false;
    }
    cursor = getContent().size() - 1;
    return true;

  }

  /**
   * Moves the cursor to the remembered cursor position, usually the current
   * row.
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#moveToCurrentRow()
   */
  @Override
  public void moveToCurrentRow() throws SQLException {
    if (rsconcurlevel == 1) {
      throw new SQLException("The concurrency is READ_ONLY");
    }
    cursor = cursorBuffer;

  }

  /**
   * Moves the cursor to the insert row (afterLast()). The current cursor
   * position is remembered while the cursor is positioned on the insert row.
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#moveToInsertRow()
   */
  @Override
  public void moveToInsertRow() throws SQLException {
    if (rsconcurlevel == 1) {
      throw new SQLException("The concurrency is READ_ONLY");
    }
    if (!isAfterLast()) {
      cursorBuffer = cursor;
    }
    if (insertRow == null) {
      int count = rsmd.getColumnCount();
      insertRow = new Object[count];
    }
    this.afterLast();

  }

  /**
   * Moves the cursor to the next row in this ResultSet object.
   * 
   * @return true if the new current row is valid; false if there are no more
   *         rows
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#next()
   */
  @Override
  public boolean next() throws SQLException {
    if (isAfterLast()) {
      return false;
    }
    cursor++;
    return true;

  }

  /**
   * Moves the cursor to the previous row in this ResultSet object. If false is
   * returned the method beforeFirst() is called.
   * 
   * @return false if cursor is on or before the first row; true otherwise
   * 
   * @throws SQLException
   *           if the type is TYPE_FORWARD_ONLY
   * 
   * @see java.sql.ResultSet#previous()
   */
  @Override
  public boolean previous() throws SQLException {
    if (rstype == 1) {
      beforeFirst();
      throw new SQLException("The type is TYPE_FORWARD_ONLY.");
    }
    if (this.isFirst() || this.isBeforeFirst()) {
      beforeFirst();
      return false;
    }
    cursor = cursor - 1;
    return true;

  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#refreshRow()
   */
  @Override
  public void refreshRow() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * Moves the cursor a relative number of rows. If the number is negative and
   * greater than the max. number of allowed rows the method beforeFirst() is
   * called, if the number is positive and greater than the max. number of
   * allowed rows, the method afterLast() is called.
   * 
   * @param rows
   *          the rows
   * 
   * @return true if the cursor is on a row; false otherwise
   * 
   * @throws SQLException
   *           if the type is TYPE_FORWARD_ONLY
   * 
   * @see java.sql.ResultSet#relative(int)
   */
  @Override
  public boolean relative(int rows) throws SQLException {
    if (rstype == 1) {
      throw new SQLException("The type is TYPE_FORWARD_ONLY.");
    }
    int position = cursor + rows;
    if (position < 0) {
      beforeFirst();
      return false;
    }
    if (position > (getContent().size() - 1)) {
      afterLast();
      return false;
    }
    cursor = position;
    return true;

  }

  /**
   * The method isn't implemented.
   * 
   * @param listener
   *          the listener
   * 
   * @see javax.sql.RowSet#removeRowSetListener(javax.sql.RowSetListener)
   */

  @Override
  public void removeRowSetListener(RowSetListener listener) {

  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return true, if row deleted
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#rowDeleted()
   */
  @Override
  public boolean rowDeleted() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return true, if row inserted
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#rowInserted()
   */
  @Override
  public boolean rowInserted() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @return true, if row updated
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#rowUpdated()
   */
  @Override
  public boolean rowUpdated() throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setArray(int i, Array x) throws SQLException {
    this.updateArray(i, x);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x)
      throws SQLException {
    this.updateAsciiStream(parameterIndex, x);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length)
      throws SQLException {
    this.updateAsciiStream(parameterIndex, x, length);
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x)
      throws SQLException {
    this.updateAsciiStream(parameterName, x);
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, int length)
      throws SQLException {
    this.updateAsciiStream(parameterName, x, length);
  }

  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x)
      throws SQLException {
    this.updateBigDecimal(parameterIndex, x);
  }

  @Override
  public void setBigDecimal(String parameterName, BigDecimal x)
      throws SQLException {
    this.updateBigDecimal(parameterName, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x)
      throws SQLException {
    this.updateBinaryStream(parameterIndex, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length)
      throws SQLException {
    this.updateBinaryStream(parameterIndex, x, length);
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x)
      throws SQLException {
    this.updateBinaryStream(parameterName, x);
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, int length)
      throws SQLException {
    this.updateBinaryStream(parameterName, x, length);
  }

  @Override
  public void setBlob(int i, Blob x) throws SQLException {
    this.updateBlob(i, x);
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream)
      throws SQLException {
    this.updateBlob(parameterIndex, inputStream);
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length)
      throws SQLException {
    this.updateBlob(parameterIndex, inputStream, length);
  }

  @Override
  public void setBlob(String parameterName, Blob x) throws SQLException {
    this.updateBlob(parameterName, x);
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream)
      throws SQLException {
    this.updateBlob(parameterName, inputStream);
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream, long length)
      throws SQLException {
    this.updateBlob(parameterName, inputStream, length);
  }

  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    this.updateBoolean(parameterIndex, x);
  }

  @Override
  public void setBoolean(String parameterName, boolean x) throws SQLException {
    this.updateBoolean(parameterName, x);
  }

  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    this.updateByte(parameterIndex, x);
  }

  @Override
  public void setByte(String parameterName, byte x) throws SQLException {
    this.updateByte(parameterName, x);
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    this.updateBytes(parameterIndex, x);
  }

  @Override
  public void setBytes(String parameterName, byte[] x) throws SQLException {
    this.updateBytes(parameterName, x);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader)
      throws SQLException {
    this.updateCharacterStream(parameterIndex, reader);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length)
      throws SQLException {
    this.updateCharacterStream(parameterIndex, reader, length);
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader)
      throws SQLException {
    this.updateCharacterStream(parameterName, reader);
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader, int length)
      throws SQLException {
    this.updateCharacterStream(parameterName, reader, length);
  }

  @Override
  public void setClob(int i, Clob x) throws SQLException {
    this.updateClob(i, x);
  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    this.updateClob(parameterIndex, reader);
  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length)
      throws SQLException {
    this.updateClob(parameterIndex, reader, length);
  }

  @Override
  public void setClob(String parameterName, Clob x) throws SQLException {
    this.updateClob(parameterName, x);
  }

  @Override
  public void setClob(String parameterName, Reader reader) throws SQLException {
    this.updateClob(parameterName, reader);
  }

  @Override
  public void setClob(String parameterName, Reader reader, long length)
      throws SQLException {
    this.updateClob(parameterName, reader, length);
  }

  @Override
  public void setCommand(String cmd) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setConcurrency(int concurrency) throws SQLException {
    if (concurrency > 0 && concurrency < 3) {
      rsconcurlevel = concurrency;
    } else {
      throw new SQLException("Unknown type!");
    }
  }

  @Override
  public void setDataSourceName(String name) throws SQLException {
    datasourcename = name;
  }

  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal)
      throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setDate(String parameterName, Date x) throws SQLException {
    throw new UnsupportedOperationException("not supported");

  }

  @Override
  public void setDate(String parameterName, Date x, Calendar cal)
      throws SQLException {
    throw new UnsupportedOperationException("not supported");
  }

  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    this.updateDouble(parameterIndex, x);
  }

  @Override
  public void setDouble(String parameterName, double x) throws SQLException {
    this.updateDouble(parameterName, x);
  }

  @Override
  public void setEscapeProcessing(boolean enable) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    this.updateFloat(parameterIndex, x);
  }

  @Override
  public void setFloat(String parameterName, float x) throws SQLException {
    this.updateFloat(parameterName, x);
  }

  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    this.updateInt(parameterIndex, x);
  }

  @Override
  public void setInt(String parameterName, int x) throws SQLException {
    this.updateInt(parameterName, x);
  }

  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    this.updateLong(parameterIndex, x);
  }

  @Override
  public void setLong(String parameterName, long x) throws SQLException {
    this.updateLong(parameterName, x);
  }

  @Override
  public void setMaxFieldSize(int max) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param max
   *          the max
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSet#setMaxRows(int)
   */

  @Override
  public void setMaxRows(int max) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * Sets the given RowSetMetaData object as the RowSetMetaData object for this
   * RowSet object.
   * 
   * @param md
   *          the md
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSetInternal#setMetaData(javax.sql.RowSetMetaData)
   */
  @Override
  public void setMetaData(RowSetMetaData md) throws SQLException {
    rsmd = md;
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value)
      throws SQLException {
    this.updateNCharacterStream(parameterIndex, value);
  }

  @Override
  public void setNCharacterStream(int arg0, Reader arg1, long arg2)
      throws SQLException {
    this.updateNCharacterStream(arg0, arg1, arg2);
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value)
      throws SQLException {
    this.updateNCharacterStream(parameterName, value);
  }

  @Override
  public void setNCharacterStream(String arg0, Reader arg1, long arg2)
      throws SQLException {
    this.updateNCharacterStream(arg0, arg1, arg2);
  }

  @Override
  public void setNClob(int arg0, NClob arg1) throws SQLException {
    this.updateNClob(arg0, arg1);
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    this.updateNClob(parameterIndex, reader);
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length)
      throws SQLException {
    this.updateNClob(parameterIndex, reader, length);
  }

  @Override
  public void setNClob(String arg0, NClob arg1) throws SQLException {
    this.updateNClob(arg0, arg1);
  }

  @Override
  public void setNClob(String parameterName, Reader reader) throws SQLException {
    this.updateNClob(parameterName, reader);
  }

  @Override
  public void setNClob(String parameterName, Reader reader, long length)
      throws SQLException {
    this.updateNClob(parameterName, reader, length);
  }

  @Override
  public void setNString(int arg0, String arg1) throws SQLException {
    this.updateNString(arg0, arg1);
  }

  @Override
  public void setNString(String arg0, String arg1) throws SQLException {
    this.updateNString(arg0, arg1);
  }

  @Override
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setNull(int paramIndex, int sqlType, String typeName)
      throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setNull(String parameterName, int sqlType) throws SQLException {
    throw new UnsupportedOperationException("not supported");

  }

  @Override
  public void setNull(String parameterName, int sqlType, String typeName)
      throws SQLException {
    throw new UnsupportedOperationException("not supported");

  }

  @Override
  public void setObject(int parameterIndex, Object x) throws SQLException {
    this.updateObject(parameterIndex, x);
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType)
      throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType,
      int scale) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setObject(String parameterName, Object x) throws SQLException {
    this.updateObject(parameterName, x);

  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType)
      throws SQLException {
    throw new UnsupportedOperationException("not supported");
  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType,
      int scale) throws SQLException {
    throw new UnsupportedOperationException("not supported");
  }

  @Override
  public void setPassword(String password) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setQueryTimeout(int seconds) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setReadOnly(boolean value) throws SQLException {
    isreadonly = value;
  }

  @Override
  public void setRef(int i, Ref x) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setRowId(int arg0, RowId arg1) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setRowId(String arg0, RowId arg1) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    this.updateShort(parameterIndex, x);
  }

  @Override
  public void setShort(String parameterName, short x) throws SQLException {
    this.updateShort(parameterName, x);
  }

  @Override
  public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
    this.updateSQLXML(arg0, arg1);
  }

  @Override
  public void setSQLXML(String arg0, SQLXML arg1) throws SQLException {
    this.updateSQLXML(arg0, arg1);
  }

  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    this.updateString(parameterIndex, x);
  }

  @Override
  public void setString(String parameterName, String x) throws SQLException {
    this.updateString(parameterName, x);
  }

  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    this.updateTime(parameterIndex, x);
  }

  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal)
      throws SQLException {
    throw new UnsupportedOperationException("not supported");
  }

  @Override
  public void setTime(String parameterName, Time x) throws SQLException {
    this.updateTime(parameterName, x);
  }

  @Override
  public void setTime(String parameterName, Time x, Calendar cal)
      throws SQLException {
    throw new UnsupportedOperationException("not supported");

  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    this.updateTimestamp(parameterIndex, x);
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
      throws SQLException {
    // this.updateTimestamp(columnIndex, x);
    throw new UnsupportedOperationException(EXC);

  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x)
      throws SQLException {
    this.updateTimestamp(parameterName, x);

  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
      throws SQLException {
    throw new UnsupportedOperationException("not supported");
  }

  @Override
  public void setTransactionIsolation(int level) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setType(int type) throws SQLException {
    if (type > 0 && type < 4) {
      rstype = type;
    } else {
      throw new SQLException("Unknown type!");
    }
  }

  @Override
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    throw new SQLException(EXC);
  }

  @Override
  public void setUrl(String url) throws SQLException {
    throw new SQLException(EXC + url);
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {
    throw new UnsupportedOperationException(EXC);

  }

  @Override
  public void setUsername(String name) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * Build a String containing the columnNames and all values of the RowSet
   * object.
   * 
   * @return the string
   */
  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer();

    try {
      int columnCount = rsmd.getColumnCount();
      for (int i = 0; i < columnCount; i++) {
        buf.append(rsmd.getColumnName(i + 1) + ", ");
      }
      buf.append("\n--------------------------------\n");

      cursorBuffer = cursor;
      first();
      while (!isAfterLast()) {
        for (int i = 0; i < columnCount; i++) {
          buf.append(getObject(i + 1));
          buf.append("; ");
        }
        buf.append("\n");
        next();
      }
      cursor = cursorBuffer;
    } catch (SQLException e) {
      SimSystem.report(e);
    }
    return buf.toString();
  }

  /**
   * The method isn't implemented.
   * 
   * @param <T>
   * 
   * @param arg0
   *          the arg0
   * 
   * @return NULL
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public <T> T unwrap(Class<T> arg0) throws SQLException {
    return null;
  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
   */
  @Override
  public void updateArray(int columnIndex, Array x) throws SQLException {
    updateValue(columnIndex, x);

  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
   */
  @Override
  public void updateArray(String columnName, Array x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);

  }

  /**
   * Update ascii stream.
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
   */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length)
      throws SQLException {
    byte update[] = new byte[length];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, length);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    ByteArrayInputStream stream =
        new ByteArrayInputStream(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the length is greater than the max integer value
   */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, long length)
      throws SQLException {
    if (length > Integer.MAX_VALUE) {
      throw new SQLException(tolongexc);
    }
    int intlength = (int) length;
    byte update[] = new byte[intlength];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, intlength);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    ByteArrayInputStream stream =
        new ByteArrayInputStream(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Update ascii stream.
   * 
   * @param columnLabel
   *          the column label
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateAsciiStream(String columnLabel, InputStream x)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateAsciiStream(java.lang.String,
   *      java.io.InputStream, int)
   */
  @Override
  public void updateAsciiStream(String columnName, InputStream x, int length)
      throws SQLException {
    int columnIndex = this.findColumn(columnName);
    byte update[] = new byte[length];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, length);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    ByteArrayInputStream stream =
        new ByteArrayInputStream(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the length is greater than the max integer value
   */
  @Override
  public void updateAsciiStream(String columnName, InputStream x, long length)
      throws SQLException {
    if (length > Integer.MAX_VALUE) {
      throw new SQLException(tolongexc);
    }
    int intlength = (int) length;
    int columnIndex = this.findColumn(columnName);
    byte update[] = new byte[intlength];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, intlength);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    ByteArrayInputStream stream =
        new ByteArrayInputStream(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Updates the designated column with a BigDecimal value. The updater methods
   * are used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
   */
  @Override
  public void updateBigDecimal(int columnIndex, BigDecimal x)
      throws SQLException {
    updateValue(columnIndex, x);

  }

  /**
   * Updates the designated column with a BigDecimal value. The updater methods
   * are used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateBigDecimal(java.lang.String,
   *      java.math.BigDecimal)
   */
  @Override
  public void updateBigDecimal(String columnName, BigDecimal x)
      throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);
  }

  /**
   * Update binary stream.
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
   */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, int length)
      throws SQLException {
    byte update[] = new byte[length];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, length);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    ByteArrayInputStream stream =
        new ByteArrayInputStream(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the length is greater than the max integer value
   */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, long length)
      throws SQLException {
    if (length > Integer.MAX_VALUE) {
      throw new SQLException(tolongexc);
    }
    int intlength = (int) length;
    byte update[] = new byte[intlength];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, intlength);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    ByteArrayInputStream stream =
        new ByteArrayInputStream(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Update binary stream.
   * 
   * @param columnLabel
   *          the column label
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateBinaryStream(String columnLabel, InputStream x)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateBinaryStream(java.lang.String,
   *      java.io.InputStream, int)
   */
  @Override
  public void updateBinaryStream(String columnName, InputStream x, int length)
      throws SQLException {
    int columnIndex = this.findColumn(columnName);
    byte update[] = new byte[length];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, length);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    ByteArrayInputStream stream =
        new ByteArrayInputStream(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the length is greater than the max integer value
   */
  @Override
  public void updateBinaryStream(String columnName, InputStream x, long length)
      throws SQLException {
    if (length > Integer.MAX_VALUE) {
      throw new SQLException(tolongexc);
    }
    int intlength = (int) length;
    int columnIndex = this.findColumn(columnName);
    byte update[] = new byte[intlength];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, intlength);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    ByteArrayInputStream stream =
        new ByteArrayInputStream(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
   */
  @Override
  public void updateBlob(int columnIndex, Blob x) throws SQLException {
    updateValue(columnIndex, x);
  }

  /**
   * Update blob.
   * 
   * @param columnIndex
   *          the column index
   * @param inputStream
   *          the input stream
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateBlob(int columnIndex, InputStream inputStream)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the length is greater than the max integer value
   */
  @Override
  public void updateBlob(int columnIndex, InputStream x, long length)
      throws SQLException {
    if (length > Integer.MAX_VALUE) {
      throw new SQLException(tolongexc);
    }
    int intlength = (int) length;
    byte update[] = new byte[intlength];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, intlength);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    ByteArrayInputStream stream =
        new ByteArrayInputStream(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
   */
  @Override
  public void updateBlob(String columnName, Blob x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);

  }

  /**
   * Update blob.
   * 
   * @param columnLabel
   *          the column label
   * @param inputStream
   *          the input stream
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateBlob(String columnLabel, InputStream inputStream)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the length is greater than the max integer value
   */
  @Override
  public void updateBlob(String columnName, InputStream x, long length)
      throws SQLException {
    if (length > Integer.MAX_VALUE) {
      throw new SQLException(tolongexc);
    }
    int intlength = (int) length;
    int columnIndex = this.findColumn(columnName);
    byte update[] = new byte[intlength];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, intlength);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    ByteArrayInputStream stream =
        new ByteArrayInputStream(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Updates the designated column with a boolean value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateBoolean(int, boolean)
   */
  @Override
  public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    updateValue(columnIndex, Boolean.valueOf(x));
  }

  /**
   * Updates the designated column with a boolean value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
   */
  @Override
  public void updateBoolean(String columnName, boolean x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);

  }

  /**
   * Updates the designated column with a byte value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateByte(int, byte)
   */
  @Override
  public void updateByte(int columnIndex, byte x) throws SQLException {
    updateValue(columnIndex, Byte.valueOf(x));

  }

  /**
   * Updates the designated column with a byte value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
   */
  @Override
  public void updateByte(String columnName, byte x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);

  }

  /**
   * Updates the designated column with a byte[] value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateBytes(int, byte[])
   */
  @Override
  public void updateBytes(int columnIndex, byte[] x) throws SQLException {
    updateValue(columnIndex, x);

  }

  /**
   * Updates the designated column with a byte[] value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
   */
  @Override
  public void updateBytes(String columnName, byte[] x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);

  }

  /**
   * Update character stream.
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
   */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x, int length)
      throws SQLException {
    char[] update = new char[length];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, length);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    CharArrayReader stream = new CharArrayReader(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the length is greater than the max integer value
   */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x, long length)
      throws SQLException {
    if (length > Integer.MAX_VALUE) {
      throw new SQLException(tolongexc);
    }
    int intlength = (int) length;
    char[] update = new char[intlength];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, intlength);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    CharArrayReader stream = new CharArrayReader(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Update character stream.
   * 
   * @param columnLabel
   *          the column label
   * @param reader
   *          the reader
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateCharacterStream(String columnLabel, Reader reader)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateCharacterStream(java.lang.String,
   *      java.io.Reader, int)
   */
  @Override
  public void updateCharacterStream(String columnName, Reader x, int length)
      throws SQLException {
    int columnIndex = this.findColumn(columnName);
    char[] update = new char[length];
    int actuallength = 0;
    try {
      actuallength = x.read(update, 0, length);
    } catch (IOException e) {
      SimSystem.report(e);
    }
    CharArrayReader stream = new CharArrayReader(update, 0, actuallength);
    updateValue(columnIndex, stream);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the length is greater than the max integer value
   */
  @Override
  public void updateCharacterStream(String columnName, Reader x, long length)
      throws SQLException {
    if (length > Integer.MAX_VALUE) {
      throw new SQLException(tolongexc);
    }
    int intlength = (int) length;
    updateCharacterStream(columnName, x, intlength);
  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
   */
  @Override
  public void updateClob(int columnIndex, Clob x) throws SQLException {
    updateValue(columnIndex, x);
  }

  /**
   * Update clob.
   * 
   * @param columnIndex
   *          the column index
   * @param reader
   *          the reader
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the length is greater than the max integer value
   */
  @Override
  public void updateClob(int columnIndex, Reader x, long length)
      throws SQLException {

    updateCharacterStream(getMetaData().getColumnClassName(columnIndex), x,
        length);

  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
   */
  @Override
  public void updateClob(String columnName, Clob x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);
  }

  /**
   * Update clob.
   * 
   * @param columnLabel
   *          the column label
   * @param reader
   *          the reader
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with the values of an InputStream. The
   * updater methods are used to update column values in the current row or the
   * insert row (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * @param length
   *          the length
   * 
   * @throws SQLException
   *           if the length is greater than the max integer value
   */
  @Override
  public void updateClob(String columnName, Reader x, long length)
      throws SQLException {

    updateCharacterStream(columnName, x, length);
  }

  /**
   * Updates the designated column with a Date value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
   */
  @Override
  public void updateDate(int columnIndex, Date x) throws SQLException {
    updateValue(columnIndex, x);
  }

  /**
   * Updates the designated column with a Date value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
   */
  @Override
  public void updateDate(String columnName, Date x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);
  }

  /**
   * Updates the designated column with a double value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateDouble(int, double)
   */
  @Override
  public void updateDouble(int columnIndex, double x) throws SQLException {
    updateValue(columnIndex, Double.valueOf(x));
  }

  /**
   * Updates the designated column with a double value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
   */
  @Override
  public void updateDouble(String columnName, double x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);
  }

  /**
   * Updates the designated column with a float value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateFloat(int, float)
   */
  @Override
  public void updateFloat(int columnIndex, float x) throws SQLException {
    updateValue(columnIndex, Float.valueOf(x));
  }

  /**
   * Updates the designated column with a float value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
   */
  @Override
  public void updateFloat(String columnName, float x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);
  }

  /**
   * Updates the designated column with a integer value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateInt(int, int)
   */
  @Override
  public void updateInt(int columnIndex, int x) throws SQLException {
    updateValue(columnIndex, Integer.valueOf(x));
  }

  /**
   * Updates the designated column with a integer value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateInt(java.lang.String, int)
   */
  @Override
  public void updateInt(String columnName, int x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);
  }

  /**
   * Updates the designated column with a long value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateLong(int, long)
   */
  @Override
  public void updateLong(int columnIndex, long x) throws SQLException {
    updateValue(columnIndex, Long.valueOf(x));
  }

  /**
   * Updates the designated column with a long value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateLong(java.lang.String, long)
   */
  @Override
  public void updateLong(String columnName, long x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);
  }

  /**
   * Update n character stream.
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateNCharacterStream(int columnIndex, Reader x)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * @param arg2
   *          the arg2
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateNCharacterStream(int arg0, Reader arg1, long arg2)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Update n character stream.
   * 
   * @param columnLabel
   *          the column label
   * @param reader
   *          the reader
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * @param arg2
   *          the arg2
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateNCharacterStream(String arg0, Reader arg1, long arg2)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateNClob(int arg0, NClob arg1) throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Update n clob.
   * 
   * @param columnIndex
   *          the column index
   * @param reader
   *          the reader
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * @param arg2
   *          the arg2
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateNClob(int arg0, Reader arg1, long arg2) throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateNClob(String arg0, NClob arg1) throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Update n clob.
   * 
   * @param columnLabel
   *          the column label
   * @param reader
   *          the reader
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateNClob(String columnLabel, Reader reader)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * @param arg2
   *          the arg2
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateNClob(String arg0, Reader arg1, long arg2)
      throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateNString(int arg0, String arg1) throws SQLException {
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateNString(String arg0, String arg1) throws SQLException {
    throw new UnsupportedOperationException(EXC);
  }

  /**
   * Updates the designated column with a null value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateNull(int)
   */
  @Override
  public void updateNull(int columnIndex) throws SQLException {
    updateValue(columnIndex, null);
  }

  /**
   * Updates the designated column with a null value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateNull(java.lang.String)
   */
  @Override
  public void updateNull(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, null);
  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
   */
  @Override
  public void updateObject(int columnIndex, Object x) throws SQLException {
    updateValue(columnIndex, x);
  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * @param scale
   *          the scale
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   *           <b>Note</b> scale: For an object of java.math.BigDecimal , this
   *           is the number of digits after the decimal point. For Java Object
   *           types InputStream and Reader, this is the length of the data in
   *           the stream or reader. For all other types, this value will be
   *           ignored.
   * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
   */
  @Override
  public void updateObject(int columnIndex, Object x, int scale)
      throws SQLException {
    Class<?> type = x.getClass();
    String name = type.getName();

    boolean boolreader = name.contains("Reader");
    boolean boolinputstream = name.contains("InputStream");
    boolean boolbigdecimal = name.contains("BigDecimal");

    if (!boolreader && !boolinputstream && !boolbigdecimal) {
      updateValue(columnIndex, x);
    } else {
      if (boolbigdecimal) {
        BigDecimal value = (BigDecimal) x;
        updateValue(columnIndex, value.setScale(scale));
      }
      if (boolreader) {
        Reader value = (Reader) x;
        char[] update = new char[scale];
        int actuallength = 0;
        try {
          actuallength = value.read(update, 0, scale);
        } catch (IOException e) {
          SimSystem.report(e);
        }
        CharArrayReader stream = new CharArrayReader(update, 0, actuallength);
        updateValue(columnIndex, stream);
      }
      if (boolinputstream) {
        InputStream value = (InputStream) x;
        byte update[] = new byte[scale];
        int actuallength = 0;
        try {
          actuallength = value.read(update, 0, scale);
        } catch (IOException e) {
          SimSystem.report(e);
        }
        ByteArrayInputStream stream =
            new ByteArrayInputStream(update, 0, actuallength);
        updateValue(columnIndex, stream);
      }
    }

  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
   */
  @Override
  public void updateObject(String columnName, Object x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);
  }

  /**
   * Updates the designated column with the given value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * @param scale
   *          the scale
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   *           <b>Note</b> scale: For an object of java.math.BigDecimal , this
   *           is the number of digits after the decimal point. For Java Object
   *           types InputStream and Reader, this is the length of the data in
   *           the stream or reader. For all other types, this value will be
   *           ignored.
   * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object,
   *      int)
   */
  @Override
  public void updateObject(String columnName, Object x, int scale)
      throws SQLException {

    int columnIndex = this.findColumn(columnName);

    if (x instanceof BigDecimal) {
      BigDecimal value = ((BigDecimal) x).setScale(scale);
      updateValue(columnIndex, value);
    } else if (x instanceof Reader) {
      Reader value = (Reader) x;
      String valueasstring = null;
      valueasstring = value.toString().substring(0, scale);
      updateValue(columnIndex, valueasstring);
    } else if (x instanceof InputStream) {
      InputStream value = (InputStream) x;
      byte[] update = new byte[scale];
      int actuallength = 0;
      try {
        actuallength = value.read(update);
      } catch (IOException e) {
        SimSystem.report(e);
      }
      if (actuallength == scale) {
        updateValue(columnIndex, update);
      } else {
        throw new SQLException(
            "The update isn't completed, the InputStream couldn't be processed");
      }
    } else {
      updateValue(columnIndex, x);
    }

    /*
     * Class<?> type = x.getClass(); String name = type.getName();
     * 
     * if (name != "java.math.BigDecimal" && name != "java.io.Reader" && name !=
     * "java.io.java.io.InputStream") { updateValue(columnIndex, x); } else { if
     * (name == "java.math.BigDecimal") { BigDecimal value = (BigDecimal) x;
     * value.setScale(scale); updateValue(columnIndex, value); } if (name ==
     * "java.io.Reader") { Reader value = (Reader) x; String valueasstring =
     * null; valueasstring = value.toString().substring(0, scale);
     * updateValue(columnIndex, valueasstring); } if (name ==
     * "java.io.java.io.InputStream") { InputStream value = (InputStream) x;
     * byte[] update = new byte[scale]; int actuallength = 0; try { actuallength
     * = value.read(update); } catch (IOException e) { SimSystem.report (e); }
     * if (actuallength == scale) { updateValue(columnIndex, update); } else {
     * throw new SQLException( "The update isn't completed, the InputStream
     * couldn't be processed"); } } }
     */

  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
   */

  @Override
  public void updateRef(int columnIndex, Ref x) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
   */

  @Override
  public void updateRef(String columnName, Ref x) throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see java.sql.ResultSet#updateRow()
   */
  @Override
  public void updateRow() throws SQLException {
    throw new SQLException(EXC);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateRowId(int arg0, RowId arg1) throws SQLException {
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateRowId(String arg0, RowId arg1) throws SQLException {
  }

  /**
   * Updates the designated column with a short value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateShort(int, short)
   */
  @Override
  public void updateShort(int columnIndex, short x) throws SQLException {
    updateValue(columnIndex, Short.valueOf(x));
  }

  /**
   * Updates the designated column with a short value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateShort(java.lang.String, short)
   */
  @Override
  public void updateShort(String columnName, short x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateShort(columnIndex, x);
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * 
   * @throws SQLException
   *           the SQL exception
   */

  @Override
  public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
  }

  /**
   * The called method isn't executable for the given dummy database.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   * 
   * @throws SQLException
   *           the SQL exception
   */
  @Override
  public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException {
  }

  /**
   * Updates the designated column with a String value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateString(int, java.lang.String)
   */
  @Override
  public void updateString(int columnIndex, String x) throws SQLException {
    updateValue(columnIndex, x);
  }

  /**
   * Updates the designated column with a value. The updater methods are used to
   * update column values in the current row or the insert row (isAfterLast() ==
   * true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
   */
  @Override
  public void updateString(String columnName, String x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateString(columnIndex, x);
  }

  /**
   * Updates the designated column with a Time value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
   */
  @Override
  public void updateTime(int columnIndex, Time x) throws SQLException {
    updateValue(columnIndex, x);
  }

  /**
   * Updates the designated column with a Time value. The updater methods are
   * used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
   */
  @Override
  public void updateTime(String columnName, Time x) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateTime(columnIndex, x);
  }

  /**
   * Updates the designated column with a Timestamp value. The updater methods
   * are used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
   */
  @Override
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    updateValue(columnIndex, x);
  }

  /**
   * Updates the designated column with a Timestamp value. The updater methods
   * are used to update column values in the current row or the insert row
   * (isAfterLast() == true or isBeforeFirst() == true).
   * 
   * @param columnName
   *          the column name
   * @param x
   *          the x
   * 
   * @throws SQLException
   *           if the concurrency is READ_ONLY
   * 
   * @see java.sql.ResultSet#updateTimestamp(java.lang.String,
   *      java.sql.Timestamp)
   */
  @Override
  public void updateTimestamp(String columnName, Timestamp x)
      throws SQLException {
    int columnIndex = this.findColumn(columnName);
    updateValue(columnIndex, x);
  }

  /**
   * This method is called from every update-method and is used to update column
   * values in the current row or the insert row (isAfterLast() == true or
   * isBeforeFirst() == true).
   * 
   * @param columnIndex
   *          the column index
   * @param value
   *          the value
   * 
   * @throws SQLException
   *           if the columnIndex doesn't exist
   */
  private void updateValue(int columnIndex, Object value) throws SQLException {
    if (rsconcurlevel == 1) {
      throw new SQLException("The concurrency is READ_ONLY");
    }
    if (columnIndex > rsmd.getColumnCount()) {
      throw new SQLException("The called columnindex doesn't exist.");
    }
    if (isAfterLast() || isBeforeFirst()) {
      if (insertRow == null) {
        int count = rsmd.getColumnCount();
        insertRow = new Object[count];
      }
      insertRow[columnIndex - 1] = value;
    } else {
      Object[] curRow = getContent().get(cursor);
      curRow[columnIndex - 1] = value;
    }
    notifyRowChange();
  }

  /**
   * Reports whether the last column read had a value of NULL or not.
   * 
   * @return true if the last column value read was NULL and false otherwise
   * 
   * @throws SQLException
   *           if the last columnindex hasn't existed
   * 
   * @see java.sql.ResultSet#wasNull()
   */
  @Override
  public boolean wasNull() throws SQLException {

    Object obj = getColumnInCurrentRow(columnIndexBuffer);

    if (obj == null) {
      return true;
    }

    return false;
  }

  /**
   * Inform all listeners about the change in the rowset.
   * 
   */
  private void notifyRowSetChange() {
    for (RowSetListener rsl : listeners) {
      rsl.rowSetChanged(rowSetEvent);
    }

  }

  /**
   * Inform all listeners about the change in the rowset.
   * 
   */
  private void notifyRowChange() {
    for (RowSetListener rsl : listeners) {
      rsl.rowChanged(rowSetEvent);
    }

  }

  /**
   * Get the object in the current row
   * 
   * @return an array of all object in the current row
   */
  private Object[] getCurrentRow() {
    return getContent().get(cursor);
  }

  /**
   * Fetch the value at the given column index and return the value. Sets the
   * {@link #columnIndexBuffer} to the column index passed.
   * 
   * @param <D>
   * @param columnIndex
   *          the index of the column the value shall be retrieved from
   * @return the value of the column specified column index in the current row.
   * @throws SQLException
   */
  @SuppressWarnings("unchecked")
  private <D> D getColumnInCurrentRow(int columnIndex) throws SQLException {
    Object[] row = getCurrentRow();
    columnIndexBuffer = columnIndex;

    if (columnIndex > rsmd.getColumnCount() || columnIndex < 1) {
      throw new SQLException(
          "Column does not exist. Columns have to be in between 1 and "
              + rsmd.getColumnCount() + ". There is no column with index "
              + columnIndex + ".");
    }

    return (D) row[columnIndex - 1];

  }

  /**
   * Same as {@link #getColumnInCurrentRow(int)}. But columns is specified using
   * its name.
   * 
   * @param <D>
   * @param columnName
   * @return the value of the column specified by the column name in the current
   *         row.
   * @throws SQLException
   */
  private <D> D getColumnInCurrentRow(String columnName) throws SQLException {
    int columnIndex = this.findColumn(columnName);
    return getColumnInCurrentRow(columnIndex);
  }

  // TODO: Compatibility issue Java 6 vs. 7, add override tag after transition
  @Override
  public <T> T getObject(int arg0, Class<T> arg1) throws SQLException {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  // TODO: Compatibility issue Java 6 vs. 7, add override tag after transition
  @Override
  public <T> T getObject(String arg0, Class<T> arg1) throws SQLException {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  /**
   * @return the content
   */
  protected Vector<Object[]> getContent() {
    return content;
  }

}
