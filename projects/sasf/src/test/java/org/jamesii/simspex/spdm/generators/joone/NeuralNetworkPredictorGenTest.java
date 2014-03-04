/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.joone;

import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.simspex.spdm.generators.PredictorGeneratorTest;


/**
 * Tests predictor based on neural networks.
 * 
 * @author Roland Ewald
 * 
 */
public class NeuralNetworkPredictorGenTest extends PredictorGeneratorTest {

  @Override
  protected IPerformancePredictorGenerator getPredictorGenerator() {
    return new NeuralNetworkPredictorGenerator(NNLearner.BATCH_LEARNER,
        NNLayer.SIGMOID, NNSynapse.FULL, 10, 2, 100, 0.1, 0.9, false);
  }

}
