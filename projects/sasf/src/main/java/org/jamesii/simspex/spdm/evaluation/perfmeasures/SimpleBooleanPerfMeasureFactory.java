/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.perfmeasures;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.plugintype.PredictorPerfMeasureFactory;


/**
 * Simple boolean performance measure factory.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleBooleanPerfMeasureFactory extends PredictorPerfMeasureFactory {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -5654240832009951171L;

  @Override
  public IPredictorPerformanceMeasure create(ParameterBlock params, Context context) {
    return new SimpleBooleanPerfMeasure();
  }

}
