/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.overview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;

/**
 * Table model for the overview of simulation runs.
 * 
 * @author Roland Ewald
 */
public class SimRunTableModel extends DefaultTableModel {

  /** Array of column names. */
  static final String[] colNames = { "#", "Simulation Configuration", "Status",
      "Processing Time" };

  /** Serialisation ID. */
  private static final long serialVersionUID = -873666273844230235L;

  /** Maximum size of list. */
  private int maxSize = 10;

  /**
   * Factor by which the table will be shrunk once it exceeds its maximal size.
   */
  private double shrinkFactor = 0.5;

  /** List of simulation runs to be displayed. */
  private List<SimulationTableInformation> simTableInfos = new ArrayList<>();

  /**
   * Map from SRTIs to the rows in which the associated simulation table
   * information is held.
   */
  private Map<ComputationTaskRuntimeInformation, SimulationTableInformation> srtiTableRows =
      new HashMap<>();

  /** Counter for the simulation run. */
  private int simRunCounter = 0;

  /**
   * Adds the simulation run.
   * 
   * @param srti
   *          the srti
   * 
   * @return true, if successful
   */
  public boolean addSimulationRun(ComputationTaskRuntimeInformation srti) {
    boolean cleared = false;
    synchronized (this) {
      if (srtiTableRows.size() > maxSize) {
        clear();
        cleared = true;
      }
      SimulationTableInformation sti =
          new SimulationTableInformation(srti, ++simRunCounter);
      simTableInfos.add(0, sti);
      srtiTableRows.put(srti, sti);
      fireTableRowsInserted(0, 0);
    }
    return cleared;
  }

  /**
   * Clear table.
   */
  public void clear() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        synchronized (this) {
          int size = simTableInfos.size();
          int itemsToBeRemoved = (int) Math.min(size * shrinkFactor, size - 1);
          for (int i = 0; i < itemsToBeRemoved; i++) {
            srtiTableRows.remove(simTableInfos.get(size - 1 - i).getSrti());
          }
          simTableInfos.subList(size - itemsToBeRemoved, size).clear();
          fireTableRowsDeleted(size - itemsToBeRemoved, size - 1);
        }
      }
    });
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
    case 0:
      return Integer.class;
    default:
      return SimulationTableInformation.class;
    }
  }

  @Override
  public int getColumnCount() {
    return colNames.length;
  }

  @Override
  public String getColumnName(int columnIndex) {
    return colNames[columnIndex];
  }

  // Custom implementations of TableModel interface

  @Override
  public int getRowCount() {
    // SimRuns are null at first call (from superclass constructor)
    return simTableInfos == null ? 0 : simTableInfos.size();
  }

  /**
   * Gets the sim table information.
   * 
   * @param srti
   *          the srti
   * 
   * @return the sim table information
   */
  public SimulationTableInformation getSimTableInformation(
      ComputationTaskRuntimeInformation srti) {
    return srtiTableRows.get(srti);
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == 0) {
      return simTableInfos.get(rowIndex).getNumber();
    }
    return simTableInfos.get(rowIndex);
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex == 4;
  }

  /**
   * Propagate update.
   * 
   * @param srti
   *          the srti
   */
  public void propagateUpdate(ComputationTaskRuntimeInformation srti) {
    synchronized (this) {
      int row = simTableInfos.indexOf(srtiTableRows.get(srti));
      this.fireTableRowsUpdated(row, row);
    }
  }

  /**
   * Propagate update.
   * 
   * @param sti
   *          the sti
   */
  public void propagateUpdate(SimulationTableInformation sti) {
    propagateUpdate(sti.getSrti());
  }

  /**
   * Gets the sim table infos.
   * 
   * @return the sim table infos
   */
  public List<SimulationTableInformation> getSimTableInfos() {
    return simTableInfos;
  }

  /**
   * Gets the max size.
   * 
   * @return the max size
   */
  public int getMaxSize() {
    return maxSize;
  }

  /**
   * Sets the max size.
   * 
   * @param maxSize
   *          the new max size
   */
  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

}