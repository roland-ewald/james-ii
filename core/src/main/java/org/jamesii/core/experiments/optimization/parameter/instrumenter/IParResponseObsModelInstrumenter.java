/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.instrumenter;

import java.util.Map;

import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.optimization.parameter.Configuration;
import org.jamesii.core.model.variables.BaseVariable;

/**
 * Interface for ModelInstrumenter in an parallel optimisation experiment.
 * 
 * @author Peter Sievert 08.08.2008
 */
public interface IParResponseObsModelInstrumenter extends
    IResponseObsModelInstrumenter {

  /**
   * Retrieves the responses from the given configuration.
   * 
   * @param configuration
   *          the parameter configuration
   * @param simConfig
   *          the simulation configuration
   * 
   * @return the observed responses
   */
  Map<? extends String, ? extends BaseVariable<?>> getObservedResponses(
      Configuration configuration, IComputationTaskConfiguration simConfig);

}
