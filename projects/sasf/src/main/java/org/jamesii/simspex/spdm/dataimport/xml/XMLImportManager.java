/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.dataimport.xml;


import java.io.FileNotFoundException;

import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.core.util.misc.Files;


/**
 * This is a very simple import manager that merely reads formerly imported
 * {@link PerformanceDataSet} instances from an XML file that was generated via
 * {@link Files#save(Object, String)}.
 * 
 * @author Roland Ewald
 * 
 */
public class XMLImportManager implements IDMDataImportManager<PerformanceTuple> {

  /** Location of the file holding the results. */
  private final String resultFile;

  /**
   * Default constructor.
   * 
   * @param file
   *          the file from which the {@link PerformanceDataSet} shall be read
   */
  public XMLImportManager(String file) {
    resultFile = file;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PerformanceDataSet<PerformanceTuple> getPerformanceData() {
    PerformanceDataSet<PerformanceTuple> dataSet = null;
    try {
      Object result = Files.load(resultFile);
      if (!(result instanceof PerformanceDataSet)) {
        throw new IllegalArgumentException("Wrong file format: "
            + PerformanceDataSet.class + " required, but found object of type "
            + result.getClass() + "instead.");
      }
      dataSet = (PerformanceDataSet<PerformanceTuple>) result;
    } catch (FileNotFoundException ex) {
      throw new IllegalArgumentException(ex);
    }
    return dataSet;
  }
}
