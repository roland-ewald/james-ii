/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util.sampling;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.model.variables.DoubleVariable;
import org.jamesii.core.model.variables.IntVariable;
import org.jamesii.core.model.variables.LongVariable;
import org.jamesii.core.model.variables.QuantitativeBaseVariable;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;

/**
 * A simple {@link IConfigurationSamplingRules} implementation that defines a
 * neighborhood by permitting to add/subtract a step-width from any parameter.
 * Selection probabilities are proportional to 2^n, with <i>n</i> being the
 * number of parameters that shall be sampled.
 * 
 * @see ParameterizationTree
 * 
 * @author Roland Ewald
 */
public class SimpleConfigurationSamplingRules implements
    IConfigurationSamplingRules {

  /** The random number generator to be used. */
  private IRandom rng;

  @Override
  public Map<ParameterizationTree, Double> calculateSelectionProbabilities(
      List<ParameterizationTree> trees) {

    Map<ParameterizationTree, Double> selectionProbabilities =
        new HashMap<>();

    double sumOfWeights = 0;
    for (ParameterizationTree tree : trees) {
      sumOfWeights += (1 << tree.countParameters());
    }

    for (ParameterizationTree tree : trees) {
      selectionProbabilities.put(tree, (1 << tree.countParameters())
          / sumOfWeights);
    }

    return selectionProbabilities;
  }

  @Override
  public ParameterBlock getNeighbor(ParameterBlock configuration,
      ParameterizationTree treeForConfig, int parameterToChange) {
    return RandomSampler.sample(
        1,
        getNeighborsForParameter(configuration, treeForConfig,
            parameterToChange), rng).get(0);
  }

  @Override
  public List<ParameterBlock> getNeighborhood(ParameterBlock configuration,
      ParameterizationTree treeForConfig) {
    List<ParameterBlock> neighborhood = new ArrayList<>();
    for (int i = 0; i < treeForConfig.countParameters(); i++) {
      neighborhood.addAll(getNeighborsForParameter(configuration,
          treeForConfig, i));
    }
    return neighborhood;
  }

  @Override
  public void setRNG(IRandom random) {
    rng = random;
  }

  public IRandom getRNG(IRandom random) {
    return rng;
  }

  /**
   * Gets the neighbors for a given parameter. The neighborhood are all
   * configurations within the parameter bounds that differ in one parameter by
   * +/- step width.
   * 
   * @param configuration
   *          the configuration
   * @param treeForConfig
   *          the tree that has been used for configuration
   * @param parameterIndex
   *          the parameter to change
   * @return the neighbors for parameter
   */
  private List<ParameterBlock> getNeighborsForParameter(
      ParameterBlock configuration, ParameterizationTree treeForConfig,
      int parameterIndex) {
    List<ParameterBlock> neighbors = new ArrayList<>();
    neighbors.addAll(createNeighbor(configuration, treeForConfig,
        parameterIndex, true));
    neighbors.addAll(createNeighbor(configuration, treeForConfig,
        parameterIndex, false));
    return neighbors;
  }

  /**
   * Creates the neighbor.
   * 
   * @param configuration
   *          the configuration
   * @param treeForConfig
   *          the tree corresponding to the configuration
   * @param parameterIndex
   *          the parameter index
   * @param increase
   *          the flag to determine whether to increase (true) or to decrease
   *          (false) the value
   * @return the list of neighbors - either contains a single element or is
   *         empty
   */
  private List<ParameterBlock> createNeighbor(ParameterBlock configuration,
      ParameterizationTree treeForConfig, int parameterIndex, boolean increase) {

    List<ParameterBlock> neighbors = new ArrayList<>();
    ParameterizationTree treeToAdapt =
        treeForConfig.createTreeForParameters(configuration.getCopy());
    Pair<BaseVariable<?>, ParameterBlock> parameterToChange =
        treeToAdapt.getParameter(parameterIndex);

    if (!(parameterToChange.getFirstValue() instanceof QuantitativeBaseVariable<?>)) {
      throw new IllegalArgumentException(
          "These sampling rules only apply to quantitative variables.");
    }

    QuantitativeBaseVariable<?> variable =
        (QuantitativeBaseVariable<?>) parameterToChange.getFirstValue();

    if (changeParameter(parameterToChange, variable, increase)) {
      neighbors.add(treeToAdapt.getConfiguration());
    }
    return neighbors;
  }

  /**
   * Changes parameter in question.
   * 
   * @param parameterToChange
   *          the parameter to change
   * @param variable
   *          the variable
   * @param increase
   *          the flag to decide whether to increase (true) or to decrease
   *          (false)
   * @return true, if change was successful
   */
  private boolean changeParameter(
      Pair<BaseVariable<?>, ParameterBlock> parameterToChange,
      QuantitativeBaseVariable<?> variable, boolean increase) {

    ParameterBlock blockToChange = parameterToChange.getSecondValue();

    if (variable instanceof DoubleVariable) {
      return handleDoubleVariable(variable, increase, blockToChange);
    } else if (variable instanceof IntVariable) {
      return handleIntVariable(variable, increase, blockToChange);
    } else if (variable instanceof LongVariable) {
      return handleLongVariable(variable, increase, blockToChange);
    }
    throw new IllegalArgumentException("Parameters described by type '"
        + variable.getClass() + "' are not supported yet.");
  }

  /**
   * Handle long variable.
   * 
   * @param variable
   *          the variable
   * @param increase
   *          the flag to increase/decrease
   * @param blockToChange
   *          the block to change
   * @return true, if successful
   */
  private boolean handleLongVariable(QuantitativeBaseVariable<?> variable,
      boolean increase, ParameterBlock blockToChange) {
    long newValue =
        (Long) blockToChange.getValue() + (increase ? 1 : -1)
            * (Long) variable.getStepWidth();
    if (newValue < (Long) variable.getLowerBound()
        || newValue > (Long) variable.getUpperBound()) {
      return false;
    }
    blockToChange.setValue(newValue);
    return true;
  }

  /**
   * Handle integer variable.
   * 
   * @param variable
   *          the variable
   * @param increase
   *          the flag to increase/decrease
   * @param blockToChange
   *          the block to change
   * @return true, if successful
   */
  private boolean handleIntVariable(QuantitativeBaseVariable<?> variable,
      boolean increase, ParameterBlock blockToChange) {
    int newValue =
        (Integer) blockToChange.getValue() + (increase ? 1 : -1)
            * (Integer) variable.getStepWidth();
    if (newValue < (Integer) variable.getLowerBound()
        || newValue > (Integer) variable.getUpperBound()) {
      return false;
    }
    blockToChange.setValue(newValue);
    return true;
  }

  /**
   * Handle double variable.
   * 
   * @param variable
   *          the variable
   * @param increase
   *          the flag to increase/decrease
   * @param blockToChange
   *          the block to change
   * @return true, if successful
   */
  private boolean handleDoubleVariable(QuantitativeBaseVariable<?> variable,
      boolean increase, ParameterBlock blockToChange) {
    double newValue =
        (Double) blockToChange.getValue() + (increase ? 1 : -1)
            * (Double) variable.getStepWidth();
    if (newValue < (Double) variable.getLowerBound()
        || newValue > (Double) variable.getUpperBound()) {
      return false;
    }
    blockToChange.setValue(newValue);
    return true;
  }
}
