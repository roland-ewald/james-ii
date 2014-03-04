/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.dataimport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Combines set of performance tuples with their meta-data.
 * 
 * @param <T>
 *          the type of the performance tuple
 * @author Roland Ewald
 */
public class PerformanceDataSet<T extends PerformanceTuple> {

  /** List of all instances belonging to this data set. */
  private List<T> instances;

  /** Meta data on set of performance tuples. */
  private PerfTupleMetaData metaData;

  /**
   * For bean compliance, only use when you know what you're doing.
   */
  public PerformanceDataSet() {
  }

  /**
   * Sets the meta data.
   * 
   * @param metaData
   *          the new meta data
   */
  public void setMetaData(PerfTupleMetaData metaData) {
    this.metaData = metaData;
  }

  /**
   * Default constructor, builds {@link PerfTupleMetaData} for provided
   * instances.
   * 
   * @param data
   *          list of available instances
   * @param maximise
   *          flag to decide whether this performance measure shall be maximised
   */
  public PerformanceDataSet(List<T> data, boolean maximise) {
    instances = data;
    retrieveMetaData(maximise);
  }

  /**
   * Creates new {@link PerfTupleMetaData} object from current list of
   * instances.
   * 
   * @param maxPerformance
   *          flag to decide whether this performance measure shall be maximised
   */
  protected final void retrieveMetaData(boolean maxPerformance) {

    metaData = new PerfTupleMetaData();
    metaData.setMaximizePerformance(maxPerformance);

    for (PerformanceTuple instance : instances) {
      metaData.checkForAttributes(instance.getConfiguration());
      metaData.checkForAttributes(instance.getFeatures());
    }
  }

  /**
   * Gets the instances.
   * 
   * @return the instances
   */
  public List<T> getInstances() {
    return instances;
  }

  /**
   * Sets the instances.
   * 
   * @param instances
   *          the new instances
   */
  public void setInstances(List<T> instances) {
    this.instances = instances;
  }

  /**
   * Gets the meta data.
   * 
   * @return the meta data
   */
  public PerfTupleMetaData getMetaData() {
    return metaData;
  }

  /**
   * Writes the given tuples to a CSV file. Names of attributes are taken from
   * the meta data.
   * 
   * @param tuples
   *          list of performance tuples to be written
   * @param metaData
   *          meta data of the performance tuples
   * @param fileName
   *          the name of the file to be written to
   * @throws IOException
   *           if writing the CSV file fails
   */
  public static void toCSV(List<PerformanceTuple> tuples,
      PerfTupleMetaData metaData, String fileName) throws IOException {

    try (FileWriter fw = new FileWriter(new File(fileName))) {

      // Write attributes
      List<String> attributeOrder = new ArrayList<>();
      attributeOrder.addAll(metaData.getNominalAttribs().keySet());
      attributeOrder.addAll(metaData.getNumericAttribs());

      for (String attribName : attributeOrder) {
        fw.write(attribName + ",");
      }
      fw.write("performance\n");

      // Write data
      for (PerformanceTuple tuple : tuples) {
        Map<String, Object> values = new HashMap<>();
        values.putAll(tuple.getFeatures());
        values.putAll(tuple.getConfiguration());
        for (String attrib : attributeOrder) {
          if (values.containsKey(attrib)) {
            fw.write(values.get(attrib).toString());
          }
          fw.write(',');
        }
        fw.write(tuple.getPerformance() + "\n");
      }
    }
  }
}
