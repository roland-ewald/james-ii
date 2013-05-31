/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter;

import java.io.Serializable;

import org.jamesii.core.experiments.optimization.parameter.instrumenter.IParResponseObsModelInstrumenter;
import org.jamesii.core.experiments.optimization.parameter.instrumenter.IParResponseObsSimInstrumenter;
import org.jamesii.core.model.variables.BaseVariable;

/**
 * Base class for all parallel optimisation problem definitions. The base class
 * is extended for some functionalities that became necessary due to the need to
 * determine to which configuration the arriving responses from the experiment
 * can be assigned to.
 * 
 * @author Peter Sievert Date: 06.08.2008
 */

public abstract class ParallelOptimizationProblemDefinition extends
    OptimizationProblemDefinition implements Serializable {

  /** generated serialVersionUID. */
  private static final long serialVersionUID = 381900140793944629L;

  /**
   * Get the parallel response observer instrumenter for the model at hand.
   * 
   * @return the model instrumenter
   */
  @Override
  public abstract IParResponseObsModelInstrumenter getModelInstrumenter();

  /**
   * Get the parallel response observer instrumenter for the simulation at hand.
   * 
   * @return the simulation instrumenter
   */
  @Override
  public abstract IParResponseObsSimInstrumenter getSimulationInstrumenter();

  /**
   * adds a factor variable. Remember using unique names. This factor will be
   * added to the responses as well.
   * 
   * @param type
   *          the type
   */
  @Override
  protected void addFactor(BaseVariable<?> type) {
    if (getFactors().get(type.getName()) != null) {
      throw new RuntimeException(
          "Could not add new factor. There is already a response called '"
              + type.getName() + "' registered: "
              + getFactors().get(type.getName()));
    }
    getFactors().put(type.getName(), type);
  }

}
