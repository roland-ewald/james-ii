/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.plugintype;

import org.jamesii.core.experiments.optimization.Optimizer;
import org.jamesii.core.experiments.optimization.parameter.OptimizationProblemDefinition;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Creates an instance of an optimisation algorithm.
 * 
 * @author Jan Himmelspach
 */
public abstract class OptimizerFactory extends Factory<Optimizer> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4391781842890016518L;

  /**
   * Instantiate and return an optimiser.
   * 
   * @param problemDefinition
   *          the problem definition
   * @param parameter
   *          the parameter block, containing an
   *          {@link OptimizationProblemDefinition} as value
   * 
   * @return the optimiser
   */
  @Override
  public abstract Optimizer create(ParameterBlock parameter);

  /**
   * Return true if this factory needs to implement IOrderedSet for factors.
   * 
   * @return true, if needs ordered set
   */
  protected abstract boolean needsOrderedSet();

  /**
   * Supports predefined configurations.
   * 
   * @param pb
   *          the parameter block
   * 
   * @return true if this problem definition is compatible for this optimisation
   *         algorithm
   */
  protected abstract boolean supportsPredefinedConfigurations(ParameterBlock pb);

  /**
   * Checks if optimiser supports multiple criteria.
   * 
   * @return true, if it does
   */
  protected abstract boolean supportsMultipleCriteria();

  /**
   * Checks if a single objective or not.
   * 
   * @param paramBlock
   *          the parameter block containing the problem description (as value)
   * 
   * @return true, if problem defines exactly one objective function
   */
  public static boolean checkForSingleObjective(ParameterBlock paramBlock) {
    if (paramBlock.getValue() != null
        && paramBlock.getValue() instanceof OptimizationProblemDefinition) {
      return ((OptimizationProblemDefinition) paramBlock.getValue())
          .isSingleObjective();
    }
    return false;
  }
}
