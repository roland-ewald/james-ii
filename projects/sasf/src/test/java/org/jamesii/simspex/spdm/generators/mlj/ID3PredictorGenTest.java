/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.mlj;

import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.simspex.spdm.generators.PredictorGeneratorTest;
import org.jamesii.simspex.spdm.generators.mlj.ID3PredictorGenerator;


/**
 * Tests {@link ID3PredictorGenerator}.
 * 
 * @author Roland Ewald
 */
public class ID3PredictorGenTest extends PredictorGeneratorTest {

  @Override
  protected IPerformancePredictorGenerator getPredictorGenerator() {
    return new ID3PredictorGenerator(NUM_PERF_CLASSES_TEST);
  }

}
