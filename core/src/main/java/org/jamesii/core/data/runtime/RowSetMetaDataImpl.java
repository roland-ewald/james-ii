/**
 * org/jamesii/core/data/RowSetMetaDataImpl.java
 *
 * $Date$ 
 * $Rev$
 * $Author$  
 */
package org.jamesii.core.data.runtime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.RowSetMetaData;

/**
 * This class implements some methods from the RowSetMetaData interface.
 * org/jamesii/core/data/RowSetMetaDataImpl.java
 * 
 * @author Steffen Maas
 * @author Thomas Nösinger
 * 
 * 
 *         $Date$ $Rev$ $Author$
 */
public class RowSetMetaDataImpl implements RowSetMetaData {

  // saves the columncount
  /** The colcount. */
  private int colcount = 0;

  // saves the columnnames
  /** The colnames. */
  private List<String> colnames = new ArrayList<>(colcount);

  /** The dummyexc. */
  private String dummyexc =
      "The called method isn't excecutable for the given dummy database.";

  /** The exc. */
  private String exc = "The columnIndex doesn't exist.";

  // ArrayList for setAutoIncrement
  /** The setautoincr. */
  private List<Boolean> setautoincr = new ArrayList<>(colcount);

  // ArrayList for setCaseSensitive
  /** The setcasesens. */
  private List<Boolean> setcasesens = new ArrayList<>(colcount);

  // ArrayList for setCatalogName
  /** The setcatname. */
  private List<String> setcatname = new ArrayList<>(colcount);

  // ArrayList for setColumnDisplaySize
  /** The setcoldissize. */
  private List<Integer> setcoldissize = new ArrayList<>(colcount);

  // ArrayList for setColumnLabel
  /** The setcollab. */
  private List<String> setcollab = new ArrayList<>(colcount);

  // ArrayList for setColumnType
  /** The setcoltype. */
  private List<Integer> setcoltype = new ArrayList<>(colcount);

  // ArrayList for setColumnTypeName
  /** The setcoltypename. */
  private List<String> setcoltypename = new ArrayList<>(colcount);

  // ArrayList for setCurrency
  /** The setcurr. */
  private List<Boolean> setcurr = new ArrayList<>(colcount);

  // ArrayList for setNullable
  /** The setnull. */
  private List<Integer> setnull = new ArrayList<>(colcount);

  // ArrayList for setPrecision
  /** The setprec. */
  private List<Integer> setprec = new ArrayList<>(colcount);

  // ArrayList for setScale
  /** The setscal. */
  private List<Integer> setscal = new ArrayList<>(colcount);

  // ArrayList for setSchemaName
  /** The setschemname. */
  private List<String> setschemname = new ArrayList<>(colcount);

  // ArrayList for setSearchable
  /** The setsearch. */
  private List<Boolean> setsearch = new ArrayList<>(colcount);

  // ArrayList for setSigned
  /** The setsig. */
  private List<Boolean> setsig = new ArrayList<>(colcount);

  // ArrayList for setTableName
  /** The settabname. */
  private List<String> settabname = new ArrayList<>(colcount);

  @Override
  public String getCatalogName(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setcatname.get(column - 1);
  }

  @Override
  public String getColumnClassName(int column) throws SQLException {
    throw new SQLException(dummyexc);
  }

  @Override
  public int getColumnCount() throws SQLException {
    return colcount;
  }

  @Override
  public int getColumnDisplaySize(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setcoldissize.get(column - 1);
  }

  @Override
  public String getColumnLabel(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setcollab.get(column - 1);
  }

  @Override
  public String getColumnName(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return colnames.get(column - 1);
  }

  @Override
  public int getColumnType(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setcoltype.get(column - 1);
  }

  @Override
  public String getColumnTypeName(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setcoltypename.get(column - 1);
  }

  @Override
  public int getPrecision(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setprec.get(column - 1);
  }

  @Override
  public int getScale(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setscal.get(column - 1);
  }

  @Override
  public String getSchemaName(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setschemname.get(column - 1);
  }

  @Override
  public String getTableName(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return settabname.get(column - 1);
  }

  @Override
  public boolean isAutoIncrement(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setautoincr.get(column - 1);
  }

  @Override
  public boolean isCaseSensitive(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setcasesens.get(column - 1);
  }

  @Override
  public boolean isCurrency(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setcurr.get(column - 1);
  }

  @Override
  public boolean isDefinitelyWritable(int column) throws SQLException {
    throw new SQLException(dummyexc);

  }

  @Override
  public int isNullable(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setnull.get(column - 1);
  }

  @Override
  public boolean isReadOnly(int column) throws SQLException {
    throw new SQLException(dummyexc);

  }

  @Override
  public boolean isSearchable(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setsearch.get(column - 1);
  }

  @Override
  public boolean isSigned(int column) throws SQLException {
    if ((column < 1) || (column > colcount)) {
      throw new IndexOutOfBoundsException(exc);
    }
    return setsig.get(column - 1);
  }

  @Override
  public boolean isWrapperFor(Class<?> arg0) throws SQLException {
    return false;
  }

  @Override
  public boolean isWritable(int column) throws SQLException {
    throw new SQLException(dummyexc);

  }

  @Override
  public void setAutoIncrement(int columnIndex, boolean property)
      throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setautoincr.set(columnIndex - 1, property);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setCaseSensitive(int columnIndex, boolean property)
      throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setcasesens.set(columnIndex - 1, property);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setCatalogName(int columnIndex, String catalogName)
      throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setcatname.set(columnIndex - 1, catalogName);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  private <T> void resizeList(List<T> list, int newSize) {
    if (list.size() == newSize) {
      return;
    }

    if (list.size() < newSize) {
      for (int i = list.size(); i < newSize; i++) {
        list.add(null);
      }
    } else {
      for (int i = list.size(); i > newSize; i--) {
        list.remove(i);
      }
    }
  }

  @Override
  public void setColumnCount(int columnCount) throws SQLException {
    if (columnCount >= 1) {
      colcount = columnCount;
      resizeList(colnames, colcount);
      resizeList(setautoincr, colcount);
      resizeList(setcasesens, colcount);
      resizeList(setsearch, colcount);
      resizeList(setcurr, colcount);
      resizeList(setnull, colcount);
      resizeList(setsig, colcount);
      resizeList(setcoldissize, colcount);
      resizeList(setcollab, colcount);
      resizeList(setschemname, colcount);
      resizeList(setprec, colcount);
      resizeList(setscal, colcount);
      resizeList(settabname, colcount);
      resizeList(setcatname, colcount);
      resizeList(setcoltype, colcount);
      resizeList(setcoltypename, colcount);
      for (int i = 0; i < columnCount; i++) {
        setautoincr.set(i, false);
        setcasesens.set(i, false);
        setsearch.set(i, false);
        setcurr.set(i, false);
        setnull.set(i, 3);
        setsig.set(i, false);
      }
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setColumnDisplaySize(int columnIndex, int size)
      throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setcoldissize.set(columnIndex - 1, size);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setColumnLabel(int columnIndex, String label) throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setcollab.set(columnIndex - 1, label);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setColumnName(int columnIndex, String columnName)
      throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      colnames.set(columnIndex - 1, columnName);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setColumnType(int columnIndex, int SQLType) throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setcoltype.set(columnIndex - 1, SQLType);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setColumnTypeName(int columnIndex, String typeName)
      throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setcoltypename.set(columnIndex - 1, typeName);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setCurrency(int columnIndex, boolean property)
      throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setcurr.set(columnIndex - 1, property);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setNullable(int columnIndex, int property) throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      if ((property >= 1) && (property < 4)) {
        setnull.set(columnIndex - 1, property);
      } else {
        throw new SQLException("The given property-Value doesn't exist.");
      }
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setPrecision(int columnIndex, int precision) throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setprec.set(columnIndex - 1, precision);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setScale(int columnIndex, int scale) throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setscal.set(columnIndex - 1, scale);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setSchemaName(int columnIndex, String schemaName)
      throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setschemname.set(columnIndex - 1, schemaName);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setSearchable(int columnIndex, boolean property)
      throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setsearch.set(columnIndex - 1, property);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setSigned(int columnIndex, boolean property) throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      setsig.set(columnIndex - 1, property);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public void setTableName(int columnIndex, String tableName)
      throws SQLException {
    if ((columnIndex > 0) && (columnIndex <= colcount)) {
      settabname.set(columnIndex - 1, tableName);
    } else {
      throw new IndexOutOfBoundsException(exc);
    }
  }

  @Override
  public <T> T unwrap(Class<T> arg0) throws SQLException {
    return null;
  }

}
