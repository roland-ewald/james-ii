/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.mlj;

import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.simspex.spdm.generators.PredictorGeneratorTest;
import org.jamesii.simspex.spdm.generators.mlj.NaiveBayesPredictorGenerator;


/**
 * Tests {@link NaiveBayesPredictorGenerator}.
 * 
 * @author Roland Ewald
 */
public class NaiveBayesPredictorGenTest extends PredictorGeneratorTest {

  @Override
  protected IPerformancePredictorGenerator getPredictorGenerator() {
    return new NaiveBayesPredictorGenerator(NUM_PERF_CLASSES_TEST);
  }

}
