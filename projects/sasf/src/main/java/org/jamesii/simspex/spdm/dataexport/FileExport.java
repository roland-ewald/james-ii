/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.dataexport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;

/**
 * Simple utility class to store performance tuples in a format that can be
 * easily processed by other tools.
 * 
 * @author Roland Ewald
 * 
 */
public final class FileExport {

  /** The title of the performance column. */
  private static final String COLUMN_TITLE_PERFORMANCE = "performance";

  /**
   * Should not be instantiated.
   */
  private FileExport() {
  }

  /**
   * Write a data-set to a CSV file.
   * 
   * @param dataSet
   *          the data set
   * @param fileName
   *          the file name
   * @param nominalPerformance
   *          flag to determine whether the performance attributed is nominal
   *          (the number will be put in quotes if that is the case)
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void writeToCSVFile(
      PerformanceDataSet<? extends PerformanceTuple> dataSet, String fileName,
      boolean nominalPerformance) throws IOException {
    writeToFile(dataSet, fileName, ",", nominalPerformance);
  }

  /**
   * Write a data-set to a file with delimiter-separated values.
   * 
   * @param dataSet
   *          the data set
   * @param fileName
   *          the file name
   * @param delimiter
   *          the delimiter
   * @param nominalPerformance
   *          flag to determine whether the performance attributed is nominal
   *          (the number will be put in quotes if that is the case)
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void writeToFile(
      PerformanceDataSet<? extends PerformanceTuple> dataSet, String fileName,
      final String delimiter, boolean nominalPerformance) throws IOException {

    PerfTupleMetaData metaData = dataSet.getMetaData();

    List<String> attributes = metaData.getSortedAttributeList();

    try (FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw)) {

      writeHeader(delimiter, attributes, bw);
      for (PerformanceTuple perfTuple : dataSet.getInstances()) {
        bw.write(createLine(delimiter, nominalPerformance, attributes,
            perfTuple));
      }
    }
  }

  /**
   * Write the header to the file.
   * 
   * @param delimiter
   *          the delimiter
   * @param attributes
   *          the attributes
   * @param bw
   *          the bw
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private static void writeHeader(String delimiter, List<String> attributes,
      BufferedWriter bw) throws IOException {
    StringBuffer header = new StringBuffer();
    for (String attribute : attributes) {
      header.append(attribute);
      header.append(delimiter);
    }
    header.append(COLUMN_TITLE_PERFORMANCE + "\n");
    bw.write(header.toString());
  }

  /**
   * Creates the line corresponding to the performance tuple data.
   * 
   * @param delimiter
   *          the delimiter to be used
   * @param nominalPerformance
   *          the flag w.r.t. nominal performance
   * @param attributes
   *          the attributes
   * @param perfTuple
   *          the performance tuple
   * @return the line
   */
  private static String createLine(final String delimiter,
      boolean nominalPerformance, List<String> attributes,
      PerformanceTuple perfTuple) {
    Map<String, Object> perfTupleAttribs = perfTuple.getAllAttributes();
    StringBuilder perfTupleLine = new StringBuilder();
    for (String attribute : attributes) {
      perfTupleLine.append(perfTupleAttribs.get(attribute) + delimiter);
    }
    double perf = perfTuple.getPerformance();
    perfTupleLine.append(nominalPerformance ? "\"" + perf + "\"" : perf);
    perfTupleLine.append("\n");
    return perfTupleLine.toString();
  }
}
