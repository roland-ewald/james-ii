/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.ensemble;

import org.jamesii.asf.spdm.generators.ensemble.EnsemblePredictorGenerator;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.generators.PredictorGeneratorTest;
import org.jamesii.simspex.spdm.generators.joone.NeuralNetworkPredGenFactory;
import org.jamesii.simspex.spdm.generators.joone.NeuralNetworkPredictorGenerator;


/**
 * Tests {@link NeuralNetworkPredictorGenerator} in Ensemble-mode.
 * 
 * @author Roland Ewald
 */
public class NeuralNetworkEnsemblePredictorGenTest extends
    PredictorGeneratorTest {

  @Override
  protected IPerformancePredictorGenerator getPredictorGenerator() {
    return new EnsemblePredictorGenerator(new NeuralNetworkPredGenFactory(),
        new ParameterBlock());
  }
}
