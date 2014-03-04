/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.algorithm;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.exploration.ils.termination.ITerminationIndicator;

/**
 * Basic Iterated Local Search (ILS) algorithm to solve the 'Algorithm
 * Configuration Problem'.
 * <p/>
 * From "ParamILS: An automatic algorithm configuration framework" by: F.
 * Hutter, H. Hoos, K. Leyton-Brown, T. Stützle Journal of Artificial
 * Intelligence Research, Vol. 36 (2009), pp. 267-306. <a
 * href="http://dx.doi.org/10.1613/jair.2861">doi:10.1613/jair.2861</a>
 * <p/>
 * This is one possibility of implementing the ParamILS algorithm. It uses a
 * better function which calculates the estimated cost for different
 * ParameterConfigurations on equal test cases.
 * 
 * @author Robert Engelke
 * 
 */
public class BasicILS extends ParamILS {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3071628799247662179L;

  /** The number of training instances. */
  private final int numberOFTrainingInstances;

  /** The Constant DEFAULT_NUMBER_OF_TRAINING_INSTANCES. */
  private static final int DEFAULT_NUMBER_OF_TRAINING_INSTANCES = 100;

  /**
   * Instantiates a new basic ils.
   * 
   * @param termination
   *          the termination condition
   */
  public BasicILS(ITerminationIndicator<ParamILS> termination) {
    this(DEFAULT_NUMBER_OF_TRAINING_INSTANCES, termination);
  }

  /**
   * Instantiates a new basic ils.
   * 
   * @param trainingInstances
   *          the number of training instances
   * @param termination
   *          the termination condition
   */
  public BasicILS(int trainingInstances,
      ITerminationIndicator<ParamILS> termination) {
    super(termination);
    numberOFTrainingInstances = trainingInstances;
  }

  @Override
  public boolean better(ParameterBlock config1, ParameterBlock config2) {
    double objectiveConfig1 =
        objective(config1, getNumberOfTrainingInstances(),
            getConsistentEstimator(config2, getNumberOfTrainingInstances()));
    double objectiveConfig2 =
        objective(config2, getNumberOfTrainingInstances(),
            getMaxExecutionTime());
    if (Double.compare(objectiveConfig1, getMaxPossibleObjective()) == 0) {
      return false;
    }
    return objectiveConfig1 <= objectiveConfig2;
  }

  /**
   * Gets the number of training instances.
   * 
   * @return the number of training instances
   */
  public int getNumberOfTrainingInstances() {
    return this.numberOFTrainingInstances;
  }

}
