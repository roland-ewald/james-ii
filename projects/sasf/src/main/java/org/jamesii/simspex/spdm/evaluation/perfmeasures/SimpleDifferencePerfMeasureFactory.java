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
 * Factory for performance measure that adds up prediction differences. Only
 * works for instances of {@link org.jamesii.asf.spdm.generators.IPerfComparisonPredictor}
 * that also implement {@link org.jamesii.asf.spdm.generators.IPerformancePredictor}.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleDifferencePerfMeasureFactory extends PredictorPerfMeasureFactory {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -2974155935027551328L;

  @Override
  public IPredictorPerformanceMeasure create(ParameterBlock params, Context context) {
    return new SimpleDifferencePerfMeasure();
  }

}
