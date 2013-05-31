/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

/**
 * A table model for {@link BrowseFSDialogViaFactories} based dialogs.
 * 
 * @author Valerius Weigandt
 */
public class BrowseFSDialogTableModel extends AbstractTableModel {
  /**
   * Serialization
   */
  private static final long serialVersionUID = -2870573302423069932L;

  /**
   * A map for managing the connection between segments and its values in the
   * table
   * 
   * @author Valerius Weigandt
   */
  private class TableMap {

    /** the data */
    private IBrowseFSDialogEntry data;

    /** Hashmap for mapping between segments and values in data */
    private Map<Integer, Integer> hashMap;

    /**
     * The constructor
     * 
     * @param data
     *          to store
     * @param map
     *          for the connection between segments and data
     */
    public TableMap(IBrowseFSDialogEntry data, Map<Integer, Integer> map) {
      this.data = data;
      hashMap = map;
    }
  }

  /** The column names */
  private List<String> columnNames = new ArrayList<>();

  /** Data content */
  private List<TableMap> content = new LinkedList<>();

  /**
   * @param names
   *          The columnNames for the table
   * @return A HashMap with the connections between columnNames and names <br>
   *         Key = Column index of the Table <br>
   *         Value = index of the fitting name in names
   */
  private Map<Integer, Integer> setColumnNames(String[] names) {
    Map<Integer, Integer> map = new HashMap<>();
    boolean update = false;
    for (int i = 0; i < names.length; i++) {
      int index = columnNames.indexOf(names[i]);
      if (index > -1) {
        map.put(index, i);
      } else {
        columnNames.add(names[i]);
        map.put(columnNames.size() - 1, i);
        update = true;
      }
    }

    if (update) {
      fireTableStructureChanged();
    }

    return map;
  }

  @Override
  public int getColumnCount() {
    return columnNames.size();
  }

  @Override
  public String getColumnName(int num) {
    return columnNames.get(num);
  }

  @Override
  public int getRowCount() {
    return content.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    TableMap object = content.get(row);
    Map<Integer, Integer> map = object.hashMap;
    IBrowseFSDialogEntry data = object.data;

    Integer key = map.get(col);
    if (key != null) {
      return data.getValue(key);
    }

    return null;
  }

  /**
   * Adds an row to the table.
   * 
   * @param data
   *          The data which will be added.
   */
  public void addRow(IBrowseFSDialogEntry data) {
    Map<Integer, Integer> map = setColumnNames(data.getColumnNames());
    content.add(new TableMap(data, map));
    fireTableRowsInserted(content.size() - 1, content.size() - 1);
  }

  /**
   * Clears the table.
   */
  public void clearTable() {
    columnNames.clear();
    content.clear();
    this.fireTableDataChanged();
  }

  /**
   * @param x
   *          Returns the model at x-position
   * @return C saved in the table
   */
  public IBrowseFSDialogEntry getData(int x) {
    return content.get(x).data;
  }

  /**
   * Check if data is available.
   * 
   * @return True if data is available, else false.
   */
  public boolean isDataAvailable() {
    if (content.size() > 0) {
      return true;
    }
    return false;
  }
}
