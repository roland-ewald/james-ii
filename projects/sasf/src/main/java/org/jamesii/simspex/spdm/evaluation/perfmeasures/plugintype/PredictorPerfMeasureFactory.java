/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.perfmeasures.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.IPredictorPerformanceMeasure;


/**
 * Base factory for creating predictor performance measures.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PredictorPerfMeasureFactory extends
    Factory<IPredictorPerformanceMeasure> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -7050285209693249297L;

  /**
   * Creates performance measurement for predictor.
 * @param params
   *          parameters for initialisation
 * @return predictor performance measure
   */
  @Override
  public abstract IPredictorPerformanceMeasure create(ParameterBlock params, Context context);

}
