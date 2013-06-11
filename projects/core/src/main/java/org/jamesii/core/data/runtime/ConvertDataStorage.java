/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.util.misc.CSVWriter;

/**
 * Utility class. Can be used to transform the normalized table layout to a more
 * common one. The result will be written to a CSV file.
 * 
 * Please note that this class is a rather simple conversion which does not care
 * about memory usage.
 * 
 * @author Jan Himmelspach
 * 
 */
public final class ConvertDataStorage {

  /**
   * Reads all data from the data storage passed and writes this data to a file
   * specified by the filename parameter.
   * 
   * @param factory
   * @param fileName
   */
  public final void execute(ParameterizedFactory<DataStorageFactory> factory,
      String fileName) {

    IDataStorage ds =
        factory.getFactoryInstance().create(factory.getParameter());

    this.execute(ds, fileName);
  }

  static final IWriteReadDataStorage<Long> getDSAs(IDataStorage ds) {
    return (IWriteReadDataStorage<Long>) ds;
  }

  /**
   * Convert the data from the datastorage given into a CSV file. Thereby all
   * values for each time step are grouped into a single row. If there is more
   * than one value for an object at a timestamp all those values will be
   * written to the CSV file. Please note that the order of these values must
   * not reflect the order these values have been computed. In such a case
   * brackets will appear around the values.
   * 
   * @param ds
   * @param fileName
   */
  public void execute(IDataStorage ds, String fileName) {

    List<Long> experiments = ds.readExperimentIDs();

    Map<String, Integer> ids = new LinkedHashMap<>();

    int column = 0;

    List<Entry> allData = new ArrayList<>();

    for (Long expid : experiments) {
      long runs = ds.getNumberOfComputations(expid, null);

      ds.setExperimentID(ds.getExperimentUID(expid));

      column = fillDataArray(ds, runs, expid, ids, column, allData);
    }
    if (!write(allData, column, ids, fileName)) {
      throw new IllegalStateException(
          "Transformation failed. Check the log for further details.");
    }
  }

  private int fillDataArray(IDataStorage<Long> ds, Long runs, Long expid,
      Map<String, Integer> ids, int column, List<Entry> allData) {
    for (Long taskid = 0l; taskid < runs; taskid++) {
      List<Long> dataids = getDSAs(ds).readDataIDs(expid, null, taskid);

      for (long dataid : dataids) {
        List<String> attributes =
            getDSAs(ds).readAttributes(expid, null, taskid, dataid);

        for (String attribute : attributes) {

          String id = dataid + "." + attribute;

          if (!ids.containsKey(id)) {
            ids.put(id, column++);
          }

          Map<String, Map<Double, List<Object>>> data =
              getDSAs(ds).readDataEntirely(null, taskid, dataid);

          for (Map.Entry<Double, List<Object>> ent : data.get(attribute)
              .entrySet()) {
            if (ent.getValue().size() == 1) {
              allData.add(new Entry(expid, taskid, dataid, attribute, ent
                  .getValue().get(0), ent.getKey()));
            } else {
              allData.add(new Entry(expid, taskid, dataid, attribute, ent
                  .getValue(), ent.getKey()));
            }

          }

        }
      }
    }
    return column;
  }

  /**
   * Write the data from allData to the file specified by fileName.
   * 
   * @param allData
   *          - the list of data to be written, will be cleared
   * @param column
   *          - the number of columns to be written
   * @param ids
   *          - the mapping between dataid.attribute and column
   * @param fileName
   *          - the file to be written to
   */
  private boolean write(List<Entry> allData, int column,
      Map<String, Integer> ids, String fileName) {

    // sort the data
    Collections.sort(allData, new EntryComp());

    // create an intermediate dynamic result set
    List<Object[]> result = new ArrayList<>();
    final int numFixedCols = 3; // number of leading columns, no magic

    Object[] newLine = new Object[column + numFixedCols];

    newLine[0] = "Experiment";
    newLine[1] = "Task";
    newLine[2] = "Time";

    int c = 0;

    // for (Map.Entry<String, Integer> entry : ids.entrySet()) {
    // for (Map.Entry<String, Integer> e : ids.entrySet()) {
    // if (e.getValue() == c) {
    // newLine[c + numFixedCols] = e.getKey();
    // c++;
    // }
    // }
    //
    // }

    for (String name : ids.keySet()) {
      newLine[c + 3] = name;
      c++;
    }

    result.add(newLine);

    Iterator<Entry> entryIt = allData.iterator();

    Entry entry = entryIt.next();

    while (entry != null) {

      newLine = new Object[column + numFixedCols];

      Long expid = entry.expid;
      Long taskid = entry.taskid;
      Double time = entry.time;

      newLine[0] = expid;
      newLine[1] = taskid;
      newLine[2] = time;

      int col = ids.get(entry.dataid + "." + entry.attribute);
      newLine[col + numFixedCols] = entry.entry;

      boolean oneMore = false;

      while (entryIt.hasNext()) {
        entry = entryIt.next();

        if (entry.time.compareTo(time) == 0
            && entry.expid.compareTo(expid) == 0
            && entry.taskid.compareTo(taskid) == 0) {
          col = ids.get(entry.dataid + "." + entry.attribute);
          newLine[col + numFixedCols] = entry.entry;
        } else {
          oneMore = true;
          break;
        }

      }

      result.add(newLine);

      if (!oneMore && !entryIt.hasNext()) {
        break;
      }

    }

    allData.clear();

    try {
      CSVWriter.writeResult(
          result.toArray(new Object[result.size()][result.get(0).length]),
          fileName, ';');
      return true;
    } catch (IOException e) {
      SimSystem.report(e);
    }

    return false;
  }

  /**
   * 
   * @author Jan
   * 
   */
  private static final class Entry {

    private final Long expid;

    private final Long taskid;

    private final Long dataid;

    private final String attribute;

    private final Object entry;

    private final Double time;

    public Entry(Long expid, Long taskid, Long dataId, String attribute,
        Object entry, Double time) {
      super();
      this.expid = expid;
      this.taskid = taskid;
      this.dataid = dataId;
      this.attribute = attribute;
      this.entry = entry;
      this.time = time;
    }

    @Override
    // for debugging the above
    public String toString() {
      return expid + "-" + taskid + "@" + time + ": " + dataid + "."
          + attribute + "=" + entry;
    }
  }

  /**
   * Helper class to sort the entries read from the datastorage.
   * 
   * @author Jan Himmelspach
   * 
   */
  private static final class EntryComp implements Comparator<Entry> {

    @Override
    public int compare(Entry o1, Entry o2) {

      int result = o1.expid.compareTo(o2.expid);
      if (result != 0) {
        return result;
      }

      result = o1.taskid.compareTo(o2.taskid);
      if (result != 0) {
        return result;
      }
      result = o1.time.compareTo(o2.time);
      if (result != 0) {
        return result;
      }

      result = o1.dataid.compareTo(o2.dataid);
      if (result != 0) {
        return result;
      }

      result = o1.attribute.compareTo(o2.attribute);
      if (result != 0) {
        return result;
      }

      throw new IllegalStateException(
          "Invalid data - more than one state stored for " + o1.dataid + "."
              + o1.attribute + " at " + o1.time);
    }
  }

}
