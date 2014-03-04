/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.joone;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.simspex.spdm.PerformanceDataTest;
import org.jamesii.simspex.spdm.generators.joone.PerformanceTupleInputSynapse;


/**
 * Tests for the {@link PerformanceTupleInputSynapse}, which converts a list of
 * {@link PerformanceTuple} to a double array.
 * 
 * @author Roland Ewald
 * 
 */
public class TestPerfTupleInputSynapse extends PerformanceDataTest {

  /** Input synapse to be tested. */
  PerformanceTupleInputSynapse ptInSynapse = null;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    ptInSynapse =
        new PerformanceTupleInputSynapse(getDataSet().getInstances(),
            getDataSet().getMetaData());
  }

  /**
   * Tests conversion of input data.
   */
  public void testInputDataConversion() {

    // Basic checks
    assertEquals(344, ptInSynapse.getPerformances().length);
    assertEquals(ptInSynapse.getPerformances().length,
        ptInSynapse.getInputData().length);
    assertEquals(4, ptInSynapse.getInputData()[0].length);

    // Check missing value encoding
    assertEquals(-1.0, ptInSynapse.getInputData()[0][3]);
  }
}
