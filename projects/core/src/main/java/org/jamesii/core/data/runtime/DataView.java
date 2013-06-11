/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import javax.sql.RowSet;
import javax.sql.RowSetMetaData;

import org.jamesii.SimSystem;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.ProcessorStateObserver;
import org.jamesii.core.observe.ProcessorTriggeredListener;

/**
 * View for the data created by observers.
 * 
 * $Date$ $Rev$ $Author: re027 $.
 */
public class DataView extends ProcessorTriggeredListener {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -5829049779184227529L;

  /** The all data observers. */
  private final List<DataObserver> allDataObservers = new Vector<>();

  /** The cur offset. */
  private int curOffset = 2;

  /** The data. */
  private final RowSet data;

  /** Is debugging mode active?. */
  private boolean debug = true;

  /** The first. */
  private boolean first = true;

  /** The meta data. */
  private RowSetMetaData metaData;

  /** The offsets. */
  private final Map<DataObserver, Integer> offsets = new HashMap<>();

  /** Flag denoting whether the history shall be stored.. */
  private final boolean storeHistory;

  /**
   * Creates a new <code>DataView</code> instance.
   * 
   * @param data
   *          The <code>ResultSet</code> may be empty or already filled.
   * @param mainObserver
   *          every time this observer is updated a new row is inserted into the
   *          data
   * @param multipleRows
   *          If set to <code>true</code> this observer will add a new row for
   *          every single simultion time. If set to <code>false</code> only one
   *          row will be used that becomes updated every sim time (old values
   *          will be overwritten)
   */
  public DataView(RowSet data, ProcessorStateObserver<?> mainObserver,
      boolean multipleRows) {
    super(mainObserver);
    storeHistory = multipleRows;
    this.data = data;
    try {
      metaData = (RowSetMetaData) data.getMetaData();
      metaData.setColumnCount(1); // one column for sim time
      metaData.setColumnName(1, "time");

      data.moveToInsertRow();
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  /**
   * Adds the observer.
   * 
   * @param o
   *          the o
   */
  public void addObserver(DataObserver o) {
    super.addObserver(o);

    allDataObservers.add(o);

    // offsets.put(o, new Integer(curOffset));
    String[] columnNames = o.getColumnNames();

    // update column count and column names
    try {
      metaData.setColumnCount(columnNames.length + metaData.getColumnCount());
      for (int i = 0; i < columnNames.length; i++) {
        metaData.setColumnName(i + curOffset, columnNames[i]);
      }
    } catch (SQLException e) {
      SimSystem.report(e);
    }

    // provide observer with table where data should be stored
    offsets.put(o, curOffset);
    curOffset += o.getColumnCount();

    // update first row according to new column count
    try {
      data.insertRow();
      data.deleteRow();
      data.moveToInsertRow();
    } catch (SQLException e) {
      SimSystem.report(e);
    }
  }

  @Override
  public void draw() {

    if (first && debug) {
      first = false;
      StringBuilder buf = new StringBuilder();
      try {
        for (int i = 0; i < curOffset - 1; i++) {
          buf.append(metaData.getColumnName(i + 1) + "; ");
        }
        buf.append("\n--------------------------------\n");
        SimSystem.report(Level.FINEST, buf.toString());
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }

    try {
      if (storeHistory) {
        // check whether this row contains a data value

        for (DataObserver curObserver : allDataObservers) {
          int columnOffset = offsets.get(curObserver);
          curObserver.store(data, columnOffset);
        }

        data.insertRow();

        boolean containsData = false;
        StringBuffer buf = new StringBuffer();
        buf.append(data.getObject(1)); // time
        for (int i = 2; i < curOffset; i++) {
          Object curData = data.getObject(i);
          buf.append("; ");
          if (curData != null) {
            buf.append(curData);
            containsData = true;
            // break;
          }
          /*
           * else { String columnName = metaData.getColumnName(i); for (int k=0;
           * k<columnName.length(); k++) { buf.append(' '); } }
           */
        }

        if (!containsData) {
          // this row contains no entry
          data.deleteRow();
        } else if (debug) {
          SimSystem.report(Level.FINEST, buf.toString());
        }

        data.moveToInsertRow();
      }

      // first column: simulation time
      double t = getMainObserver().getSimTime();
      data.updateDouble(1, t);
    } catch (SQLException e) {
      SimSystem.report(e);
    }
  }

  /**
   * Gets the data results.
   * 
   * @return the data
   */
  public ResultSet getData() {
    return data;
  }

  /**
   * Sets the debug flag.
   * 
   * @param doDebugging
   *          the flag
   */
  public final void setDebug(boolean doDebugging) {
    debug = doDebugging;
  }

  @Override
  public void update(IObservable e) {
    SimSystem.report(Level.FINEST, "updated for: " + e);
  }

}
