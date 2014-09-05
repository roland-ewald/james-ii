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
 * @author Roland Ewald
 * 
 */
public class FeatureBestDiffMeasureFactory extends PredictorPerfMeasureFactory {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -7431636698486600719L;

  @Override
  public IPredictorPerformanceMeasure create(ParameterBlock params, Context context) {
    return new FeatureBestDiffMeasure();
  }

}
