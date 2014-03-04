/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util.sampling;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.model.variables.QuantitativeBaseVariable;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Strings;

/**
 * Extends {@link ConfigurationSampler} by advanced handling of algorithm
 * parameters.
 * 
 * Note: this class may use up a considerable amount of memory, as all parameter
 * blocks that have been tried need to be stored in a map (for further usage)
 * and hence will not be garbage collected.
 * 
 * @author Roland Ewald
 * @author Robert Engelke
 * 
 */
public class RandomConfigurationSampler extends Entity implements
    IConfigurationSampler {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -9153585311665289687L;

  /** Maximal numerical error allowed. */
  private static final double EPSILON = 1e-6;

  /** Maps default setups to their parameterization trees. */
  private final Map<ParameterBlock, ParameterizationTree> paramTrees = new HashMap<>();

  /**
   * The map from all available/generated parameter blocks to those parameter
   * blocks that have been used to create parameterization trees, which means
   * they are in {@link RandomConfigurationSampler#defaultConfigurations} and
   * are also keys for {@link RandomConfigurationSampler#paramTrees}.
   */
  private final Map<ParameterBlock, ParameterBlock> paramsToTreeParams = new HashMap<>();

  /** The selection probabilities of each parameterization tree. */
  private final Map<ParameterizationTree, Double> selectionProbabilities;

  /** The default configurations. */
  private final List<ParameterBlock> defaultConfigurations;

  /**
   * The generated parameterization trees. Order corresponds to
   * {@link RandomConfigurationSampler#defaultConfigurations}.
   */
  private final List<ParameterizationTree> parameterizationTrees = new ArrayList<>();

  /** Random number generator. */
  private final IRandom random;

  /**
   * The sampling rules that apply (for example, how is the neighborhood
   * defined).
   */
  private final IConfigurationSamplingRules configSamplingRules;

  /**
   * Instantiates a new variable configuration sampler.
   * 
   * @param defaultConfigurations
   *          the default configurations
   * @param rng
   *          the random number generator
   * @param variablesForParameters
   *          the variables to define which algorithm parameter to adjust
   */
  public RandomConfigurationSampler(
      IConfigurationSamplingRules samplingRules,
      List<ParameterBlock> defaultConfigurations,
      IRandom rng,
      Map<Class<? extends Factory<?>>, List<QuantitativeBaseVariable<?>>> variablesForParameters) {
    this.configSamplingRules = samplingRules;
    this.defaultConfigurations = Collections
        .unmodifiableList(new ArrayList<>(defaultConfigurations));
    checkInputConstraints(defaultConfigurations, variablesForParameters);
    random = rng;
    this.configSamplingRules.setRNG(random);
    createParameterizationTrees(variablesForParameters);
    selectionProbabilities = configSamplingRules
        .calculateSelectionProbabilities(parameterizationTrees);
    checkProbabilities();
  }

  /**
   * Some checks for the probabilities as calculated by the
   * {@link IConfigurationSamplingRules}.
   */
  private void checkProbabilities() {
    double probSum = 0;
    for (Double probability : selectionProbabilities.values()) {
      if (probability < 0 || probability > 1) {
        throw new IllegalStateException("The sampling rules '"
            + configSamplingRules.getClass().getName()
            + "' returned invalid probability of '" + probability
            + "'. All probabilities: "
            + Strings.dispMap(selectionProbabilities));
      }
      probSum += probability;
    }
    if (Math.abs(probSum - 1.) > EPSILON) {
      throw new IllegalStateException(
          "Probabilities do not sum up to one. All probabilities: '"
              + Strings.dispMap(selectionProbabilities) + "'");
    }
  }

  /**
   * Checks input constraints (non-emptiness).
   * 
   * @param defaultConfigurations
   *          the default configurations
   * @param variablesForParameters
   *          the variables for parameters
   */
  private void checkInputConstraints(
      List<ParameterBlock> defaultConfigurations,
      Map<Class<? extends Factory<?>>, List<QuantitativeBaseVariable<?>>> variablesForParameters) {
    if (defaultConfigurations.isEmpty()) {
      throw new IllegalArgumentException(
          "List of configurations must be non-empty.");
    }
    if (variablesForParameters.isEmpty()) {
      throw new IllegalArgumentException(
          "List of factory parameters to adjust must be non-empty.");
    }
  }

  /**
   * Creates the parameterization trees.
   * 
   * @param variables
   *          the variables to use for sampling
   */
  private void createParameterizationTrees(
      Map<Class<? extends Factory<?>>, List<QuantitativeBaseVariable<?>>> variables) {
    Set<Class<? extends Factory<?>>> foundFactories = new HashSet<>();
    for (ParameterBlock defaultConfiguration : defaultConfigurations) {
      ParameterBlock pbCopy = defaultConfiguration.getCopy();
      ParameterizationTree paramTree = new ParameterizationTree(pbCopy,
          variables);
      paramTrees.put(pbCopy, paramTree);
      parameterizationTrees.add(paramTree);
      paramsToTreeParams.put(defaultConfiguration, pbCopy);
      foundFactories.addAll(paramTree.getAffectedFactories());
    }
  }

  /**
   * Gets the parameterization tree that has been used to create a configuration
   * in question.
   * 
   * @param configuration
   *          the configuration
   * @return the tree that generated the configuration
   */
  private ParameterizationTree getTreeForConfig(ParameterBlock configuration) {
    return paramTrees.get(paramsToTreeParams.get(configuration));
  }

  /**
   * Gets a random configuration.
   * 
   * @return the random configuration
   */
  public ParameterBlock getRandomConfiguration() {
    double sample = random.nextDouble();
    double probabilitySum = 0;
    for (int i = 0; i < defaultConfigurations.size(); i++) {
      probabilitySum += selectionProbabilities
          .get(parameterizationTrees.get(i));
      if (sample <= probabilitySum) {
        return getRandomNeighbour(defaultConfigurations.get(i));
      }
    }
    SimSystem
        .report(
            Level.WARNING,
            "It seems the selection probabilities of the parameterized trees do not sum up to one. This could be a numerical problem - if this happens repeatedly, please investigate.");
    return getRandomNeighbour(defaultConfigurations.get(defaultConfigurations
        .size() - 1));
  }

  @Override
  public ParameterBlock getInitialConfiguration() {
    return getRandomConfiguration();
  }

  @Override
  public ParameterBlock getRandomNeighbour(ParameterBlock simConfig) {
    int paramNumber = getNumberOfParameters(simConfig);
    if (paramNumber == 0) {
      return getRandomConfiguration();
    }
    return getRandomNeighbour(simConfig, random.nextInt(paramNumber));
  }

  @Override
  public ParameterBlock getRandomNeighbour(ParameterBlock simConfig,
      int positionToChange) {
    // Get neighbor
    ParameterBlock neighbor = configSamplingRules.getNeighbor(simConfig,
        getTreeForConfig(simConfig), positionToChange);

    // Store neighbor in mapping
    paramsToTreeParams.put(neighbor, paramsToTreeParams.get(simConfig));
    return neighbor;
  }

  @Override
  public List<ParameterBlock> getNeighbourhood(ParameterBlock simConfig) {
    // Get neighborhood
    List<ParameterBlock> neighborhood = configSamplingRules.getNeighborhood(
        simConfig, getTreeForConfig(simConfig));

    // Store neighborhood in mapping
    ParameterBlock originalTreeParameters = paramsToTreeParams.get(simConfig);
    for (ParameterBlock config : neighborhood) {
      paramsToTreeParams.put(config, originalTreeParameters);
    }
    return neighborhood;
  }

  @Override
  public int getNumberOfParameters(ParameterBlock configuration) {
    return getTreeForConfig(configuration).countParameters();
  }

  @Override
  public ParameterBlock restart() {
    return getRandomConfiguration();
  }

  public IConfigurationSamplingRules getConfigSamplingRules() {
    return configSamplingRules;
  }

}
