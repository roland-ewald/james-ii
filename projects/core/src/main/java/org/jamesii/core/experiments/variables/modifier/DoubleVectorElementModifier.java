package org.jamesii.core.experiments.variables.modifier;

import java.util.List;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.experiments.replication.IReplicationCriterion;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Iterates through list of points (double arrays), returns element from given
 * (fixed) dimension. Useful for setting up modifiers that parameterise a model
 * with parameter vectors from a point cloud, e.g., as created by
 * {@link RandomSampler}.
 * 
 * @see RandomSampler
 * 
 * @author Roland Ewald
 * @author Stefan Leye
 * 
 */
public class DoubleVectorElementModifier implements IVariableModifier<Double> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -9036447861183690927L;

  /** The list of points. */
  private final List<Double[]> points;

  /** The selected dimension. */
  private final int dimension;

  /** The counter variable. */
  private int counter = 0;

  public DoubleVectorElementModifier() {
    dimension = 0;
    points = null;
  }

  public DoubleVectorElementModifier(List<Double[]> pointCloud, int dim) {
    points = pointCloud;
    dimension = dim;
  }

  @Override
  public ParameterBlock getExperimentParameters() {
    return null;
  }

  @Override
  public IModelInstrumenter getModelInstrumenter() {
    return null;
  }

  @Override
  public IReplicationCriterion getReplicationCriterion() {
    return null;
  }

  @Override
  public IComputationInstrumenter getSimulationInstrumenter() {
    return null;
  }

  @Override
  public Double next(ExperimentVariables variables)
      throws NoNextVariableException {
    int currentIndex = counter;
    if (currentIndex == points.size()) {
      throw new NoNextVariableException();
    }
    counter++;
    return points.get(currentIndex)[dimension];
  }

  @Override
  public void reset() {
    counter = 0;
  }

}