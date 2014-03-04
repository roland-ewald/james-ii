/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.wta;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Generates {@link WinnerTakesAllPredictorGenerator}.
 * 
 * @author Roland Ewald
 * 
 */
public class WTAPredictorGenFactory extends
    PerformancePredictorGeneratorFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5687553218162648202L;

  @Override
  public IPerformancePredictorGenerator createPredictorGenerator(
      ParameterBlock params, PerformanceTuple example) {
    return new WinnerTakesAllPredictorGenerator();
  }

}
