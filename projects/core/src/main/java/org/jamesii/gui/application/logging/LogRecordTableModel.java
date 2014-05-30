/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.logging;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.table.AbstractTableModel;

/**
 * Custom {@link javax.swing.table.TableModel} for the log table
 * 
 * @author Stefan Rybacki
 */
class LogRecordTableModel extends AbstractTableModel {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4610791423075472608L;

  /**
   * The records.
   */
  private List<LogRecord> records;

  /**
   * Creates a new model instance
   */
  public LogRecordTableModel() {
    super();
    records = new ArrayList<>();
  }

  /**
   * @param rowIndex
   *          the row index
   * @return the log record at a specific row
   */
  public final LogRecord getRecord(int rowIndex) {
    if (rowIndex < 0 || rowIndex >= getRowCount()) {
      return null;
    }

    return records.get(rowIndex);
  }

  @Override
  public int getColumnCount() {
    return 4;
  }

  /**
   * Adds a new log record to the table
   * 
   * @param record
   *          the record to add
   */
  public final synchronized void addRecord(LogRecord record) {
    records.add(record);
    fireTableRowsInserted(records.size() - 2, records.size() - 1);
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  @Override
  public String getColumnName(int column) {
    switch (column) {
    case 1:
      return "Date";
    case 2:
      return "Time";
    case 3:
      return "Log message";
    }

    return null;
  }

  /**
   * Clears the log records list
   */
  public void clear() {
    records.clear();
    fireTableDataChanged();
  }

  @Override
  public int getRowCount() {
    return records.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    LogRecord record = records.get(rowIndex);
    switch (columnIndex) {
    case 0:
      return record.getLevel();
    case 1:
      return DateFormat.getDateInstance(DateFormat.MEDIUM).format(
          new Date(record.getMillis()));
    case 2:
      return DateFormat.getTimeInstance(DateFormat.MEDIUM).format(
          new Date(record.getMillis()));
    case 3:
      return record.getMessage();
    }
    return null;
  }

}
