/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.RowSetMetaData;
import javax.sql.RowSetReader;

import org.jamesii.SimSystem;

/**
 * An implementation of the {@link RowSetReader} interface. This class reads
 * tabular data from a CSV file for further use in a {@link RowSet}. Column
 * headers are expected in the file and the default delimiter is the comma (
 * <code>,</code>).
 * 
 * @author Steffen Maas
 * @author Johannes Rössel
 */
public class RowSetCSVReader implements RowSetReader {

  /** Backing field for the field delimiter. */
  private CSVParser parser;

  public RowSetCSVReader() {
    this.parser = new CSVParser();
  }

  /**
   * Reads the data from a file and puts his column data in the metadata and his
   * data in the content object.
   * 
   * @param caller
   *          the implementation of the RowSet interface and the holder of the
   *          data and metadata
   * 
   * @throws SQLException
   *           the SQL exception
   * 
   * @see javax.sql.RowSetReader#readData(javax.sql.RowSetInternal)
   */
  @Override
  public void readData(RowSetInternal caller) throws SQLException {
    RowSet c = (RowSet) caller;
    File f = new File(c.getDataSourceName());
    BufferedReader f1;

    try {
      if (f.canRead()) {
        f1 = new BufferedReader(new FileReader(c.getDataSourceName()));
        try {
          setMetaData(f1.readLine(), c);
          setContent(f1, c);
        } finally {
          f1.close();
        }
      }
    } catch (IOException | SQLException ioE) {
      SimSystem.report(ioE);
    }
  }

  /**
   * Fills the content in the {@link javax.sql.RowSet} object.
   * 
   * @param r
   *          the BufferedReader witch is linked to the file
   * @param c
   *          RowSet implementation, it gets the Data
   * 
   * @throws SQLException
   *           the SQL exception
   */
  private void setContent(BufferedReader r, RowSet c) throws SQLException {

    String line;
    try {
      while ((line = r.readLine()) != null) {
        c.insertRow();
        c.moveToInsertRow();
        String[] sa = parser.parse(line);
        for (int j = 0; j < sa.length; j++) {
          c.updateString(j, sa[j]);
        }
      }
    } catch (IOException ioE) {
      SimSystem.report(ioE);
    }
  }

  /**
   * Fills the RowSetMetaData for the RowSet Columns.
   * 
   * @param line
   *          first line of the file, witch contains the columns
   * @param c
   *          RowSet implementation, it gets the RowSetMetaData
   * 
   * @throws SQLException
   *           if a database error occurs, or if this method is called on a
   *           closed result rest
   */
  private void setMetaData(String line, RowSet c) throws SQLException {
    if (line != null) {
      String columns[] = parser.parse(line);
      RowSetMetaData md = (RowSetMetaData) c.getMetaData();
      md.setColumnCount(columns.length);
      for (int i = 0; i < columns.length; i++) {
        md.setColumnName(i + 1, columns[i]);
      }
    }
  }

}
