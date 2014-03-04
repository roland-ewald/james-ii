/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.util;

import java.util.List;
import java.util.Map;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.util.PerformanceTuples;
import org.jamesii.simspex.spdm.PerformanceDataTest;


/**
 * Tests auxiliary functions from {@link PerformanceTuples}.
 * 
 * @author Roland Ewald
 * 
 */
public class PerformanceTuplesTest extends PerformanceDataTest {

  /**
   * Test sorting to feature map.
   */
  public void testSortToFeatures() {
    testMapping(PerformanceTuples.sortToFeatureMap(getDataSet().getInstances()));
  }

  /**
   * Test sorting to configuration map.
   */
  public void testSortToConfig() {
    testMapping(PerformanceTuples.sortToConfigMap(getDataSet().getInstances()));
  }

  /**
   * Tests if the number of tuples stored in the mapping equals its original
   * number. No tuple shall be lost.
   * 
   * @param mapping
   *          the mapping
   */
  protected void testMapping(Map<?, List<PerformanceTuple>> mapping) {
    int tupleCounter = 0;
    for (List<PerformanceTuple> tuples : mapping.values()) {
      tupleCounter += tuples.size();
      for (PerformanceTuple tuple : tuples) {
        assertNotNull(tuple);
      }
    }
    assertEquals(getDataSet().getInstances().size(), tupleCounter);
  }

}
