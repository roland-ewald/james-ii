/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm;

import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.simspex.spdm.dataimport.FileImportManagerTest;

import junit.framework.TestCase;

/**
 * Super class for all tests relying on performance data.
 * 
 * @author Roland Ewald
 * 
 */
public class PerformanceDataTest extends TestCase {

  /** The number of instances expected in the test data. */
  public static final int NUMBER_OF_EXPECTED_INSTANCES = 344;

  /** Performance data set. */
  private PerformanceDataSet<PerformanceTuple> dataSet;

  @Override
  protected void setUp() throws Exception {
    IDMDataImportManager im =
        FileImportManagerTest.createTestFileImportManager();
    dataSet = im.getPerformanceData();
  }

  /**
   * Gets the data set used for testing.
   * 
   * @return the data set
   */
  protected PerformanceDataSet<PerformanceTuple> getDataSet() {
    return dataSet;
  }

  /**
   * Tests if all tuples were read.
   */
  public void testDataImportOK() {
    assertEquals(NUMBER_OF_EXPECTED_INSTANCES, dataSet.getInstances().size());
  }

}
