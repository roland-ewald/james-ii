/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation;

import org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy;
import org.jamesii.simspex.spdm.evaluation.bootstrapping.BootStrapping;

/**
 * Tests {@link BootStrapping}.
 * 
 * @author Roland Ewald
 * 
 */
public class BootStrappingTest extends PredictionGenEvaluationStrategyTest {

  @Override
  protected IPredictorGeneratorEvaluationStrategy getEvaluationStrategy() {
    return new BootStrapping(10);
  }

}
