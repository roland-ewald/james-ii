/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.experiments.optimization.ConfigurationInfo;
import org.jamesii.core.experiments.optimization.ConfigurationInfos;
import org.jamesii.core.experiments.optimization.OptimizationVariables;
import org.jamesii.core.experiments.optimization.parameter.cancellation.ICancelOptimizationCriterion;
import org.jamesii.core.experiments.optimization.parameter.cancellation.ICancelSimulationCriterion;
import org.jamesii.core.experiments.optimization.parameter.instrumenter.IResponseObsModelInstrumenter;
import org.jamesii.core.experiments.optimization.parameter.instrumenter.IResponseObsSimInstrumenter;
import org.jamesii.core.experiments.optimization.parameter.representativeValue.IRepresentativeValueOfObjectives;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.util.IConstraint;

/**
 * This is the base description for optimization problems. It is observable for
 * changes in responses and calculates the objective function, where the model
 * is responsible for update() calls. (see instrumentation) based on the factors
 * this class gets and contains an appropriate optimizer. You can define post
 * and pre-constraints, as well as CancelCriteria.
 * 
 * @author Arvid Schwecke
 * @author Roland Ewald
 */
public abstract class OptimizationProblemDefinition implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3903624687414509601L;

  /** Cancel criteria for the overall optimisation. */
  private List<ICancelOptimizationCriterion> cancelOptimization =
      new ArrayList<>();

  /**
   * Criteria for early simulation termination, in case of unbounded simulation
   * runs.
   */
  private ICancelSimulationCriterion cancelSimulationCriteria;

  /** used factors for optimisation. */
  private Configuration factors = new Configuration();

  /** List of objective functions. */
  private List<IOptimizationObjective> optObjectives = new ArrayList<>();

  /** List of post-constraints. */
  private List<IConstraint<ConfigurationInfo>> postConstraints =
      new ArrayList<>();

  /** List of pre-constraints. */
  private List<IConstraint<Configuration>> preConstraints = new ArrayList<>();

  /** list of known configurations used for optimising first/only. */
  private List<Configuration> predefinedConfigurations = new ArrayList<>();

  /**
   * Calculation of a representative value for replications of a single
   * configuration.
   */
  private IRepresentativeValueOfObjectives representedValueCalculation;

  /**
   * Data interface for observed responses. Actually used only after simulation
   * is done, resulting in unnecessary(costly) update calls.
   */
  private IResponseObserver responseObserver;

  /** Flag for using just the list of predefined configurations. */
  private boolean usingOnlyPredefinedConfiguration = false;

  /**
   * Instantiate this class and call factor creation.
   */
  public OptimizationProblemDefinition() {
  }

  /**
   * Adds a new cancel criteria to the optimisation main loop.
   * 
   * @param newCriteria
   *          the new criteria
   */
  public void addCancelOptimizationCriteria(
      ICancelOptimizationCriterion newCriteria) {
    cancelOptimization.add(newCriteria);
  }

  /**
   * adds a factor variable. Remember using unique names.
   * 
   * @param type
   *          the type
   */
  protected void addFactor(BaseVariable<?> type) {
    if (factors.get(type.getName()) != null) {
      throw new RuntimeException(
          "Could not add new factor. There is already a response called '"
              + type.getName() + "' registered: " + factors.get(type.getName()));
    }
    factors.put(type.getName(), type);
  }

  /**
   * Adds a post-constraint.
   * 
   * @param constraint
   *          the constraint
   */
  public void addPostConstraint(IConstraint<ConfigurationInfo> constraint) {
    postConstraints.add(constraint);
  }

  /**
   * Adds a pre-constriant.
   * 
   * @param constraint
   *          the constraint
   */
  public void addPreConstriant(IConstraint<Configuration> constraint) {
    preConstraints.add(constraint);
  }

  /**
   * Adds a predefined configuration.
   * 
   * @param predefinedConfiguration
   *          the predefined configuration
   */
  public void addPredefinedConfiguration(Configuration predefinedConfiguration) {
    predefinedConfigurations.add(predefinedConfiguration);
  }

  /**
   * Calculation of objectives.
   * 
   * @param configuration
   *          the current parameter configuration
   * @param response
   *          the response of the simulation
   * 
   * @return the list of objective
   */
  public Map<String, Double> calcObjectives(Configuration configuration,
      Map<String, BaseVariable<?>> response) {
    Map<String, Double> results = new HashMap<>();
    for (IOptimizationObjective objective : optObjectives) {
      results.put(objective.getName(),
          objective.calcObjective(configuration, response));
    }
    return results;
  }

  /**
   * Calculates the representative values for all objectives that were generated
   * for this configuration. Sets the result in configInfo and returns the
   * calculated value.
   * 
   * @param runs
   *          the configuration and its objectives
   * 
   * @return the calculated values
   */
  public Map<String, Double> calcRepresentedValue(ConfigurationInfos runs) {
    Map<String, Double> result =
        representedValueCalculation.calcRepresentativeValue(runs);
    return result;

  }

  /**
   * Creates a new instance of factors according to the actual problem. This is
   * capable of handle IOrderedSet as intended.
   * 
   * @return a list of factors
   */
  public Configuration createFactors() throws CloneNotSupportedException {
    return factors.clone();
  }

  /**
   * Get response observer instrumenter for the model at hand.
   * 
   * @return the model instrumenter
   */
  public abstract IResponseObsModelInstrumenter getModelInstrumenter();

  /**
   * Get response observer instrumenter for the simulation at hand.
   * 
   * @return the simulation instrumenter
   */
  public abstract IResponseObsSimInstrumenter getSimulationInstrumenter();

  /**
   * Gets the cancel optimisation criteria.
   * 
   * @return the cancelOptimization
   */
  public List<ICancelOptimizationCriterion> getCancelOptimizationCriteria() {
    return cancelOptimization;
  }

  /**
   * Gets the cancel simulation criteria.
   * 
   * @return the cancel simulation criteria
   */
  public ICancelSimulationCriterion getCancelSimulationCriteria() {
    return cancelSimulationCriteria;
  }

  /**
   * Gets the factor count.
   * 
   * @return the factor count
   */
  public int getFactorCount() {
    return factors.size();
  }

  /**
   * Gets the factors.
   * 
   * @return the factors
   */
  public Configuration getFactors() {
    return factors;
  }

  /**
   * Gets the list of optimisation objectives.
   * 
   * @return the optimisation objective
   */
  public List<IOptimizationObjective> getOptimizationObjectives() {
    return this.optObjectives;
  }

  /**
   * Checks if a single objective is defined.
   * 
   * @return true if there is exactly one objective
   */
  public boolean isSingleObjective() {
    return (optObjectives.size() == 1);
  }

  /**
   * Creates {@link OptimizationVariables} by instantiating an
   * {@link ExperimentVariable} for each {@link BaseVariable} that is a factor
   * to be considered by the optimisation algorithm.
   * 
   * @return the optimisation variables
   */
  public OptimizationVariables getOptimizationVariables() {
    OptimizationVariables ovs = new OptimizationVariables();
    for (BaseVariable<?> element : factors.values()) {
      ovs.addVariable(new ExperimentVariable<BaseVariable<?>>(
          element.getName(), element));
    }
    return ovs;
  }

  /**
   * Gets the post-constraints.
   * 
   * @return the post-constraints
   */
  public List<IConstraint<ConfigurationInfo>> getPostConstraints() {
    return postConstraints;
  }

  /**
   * Gets the pre-constraints.
   * 
   * @return the pre-constraints
   */
  public List<IConstraint<Configuration>> getPreConstraints() {
    return preConstraints;
  }

  /**
   * Gets the predefined configurations.
   * 
   * @return the predefined configurations
   */
  public List<Configuration> getPredefinedConfigurations() {
    return predefinedConfigurations;
  }

  /**
   * Get model instrumenter for response observers.
   * 
   * @return the response obs model instrumenter
   */
  public IResponseObsModelInstrumenter getResponseObsModelInstrumenter() {
    return getModelInstrumenter();
  }

  /**
   * Get simulation instrumenter for response observers.
   * 
   * @return the response observer simulation instrumenter
   */
  public IResponseObsSimInstrumenter getResponseObsSimInstrumenter() {
    return getSimulationInstrumenter();
  }

  /**
   * Checks if is using only predefined configuration.
   * 
   * @return true, if is using only predefined configuration
   */
  public boolean isUsingOnlyPredefinedConfiguration() {
    return usingOnlyPredefinedConfiguration;
  }

  /**
   * removes a cancel criteria from the optimisation main loop.
   * 
   * @param newCriteria
   *          the new criteria
   */
  public void removeCancelOptimizationCriteria(
      ICancelOptimizationCriterion newCriteria) {
    cancelOptimization.remove(newCriteria);
  }

  /**
   * Removes the post-constraint.
   * 
   * @param constraint
   *          the post-constraint
   */
  public void removePostContraint(IConstraint<ConfigurationInfo> constraint) {
    postConstraints.remove(constraint);
  }

  /**
   * Removes the pre-constraint.
   * 
   * @param constraint
   *          the pre-constraint
   */
  public void removePreConstraint(IConstraint<Configuration> constraint) {
    preConstraints.remove(constraint);
  }

  /**
   * Sets the cancel simulation criteria.
   * 
   * @param cancelSimulationCriteria
   *          the new cancel simulation criteria
   */
  public void setCancelSimulationCriteria(
      ICancelSimulationCriterion cancelSimulationCriteria) {
    this.cancelSimulationCriteria = cancelSimulationCriteria;
  }

  /**
   * Adds optimisation objective.
   * 
   * @param objective
   *          an optimisation objective
   */
  public void addOptimizationObjective(IOptimizationObjective objective) {
    this.optObjectives.add(objective);
  }

  /**
   * Sets the represented value calculation.
   * 
   * @param calculation
   *          the calculation
   */
  public void setRepresentedValueCalculation(
      IRepresentativeValueOfObjectives calculation) {
    this.representedValueCalculation = calculation;
  }

  /**
   * Sets flag whether to use only predefined configurations.
   * 
   * @param useOnlyPredefinedConfiguration
   *          the new use only predefined configuration
   */
  public void setUseOnlyPredefinedConfiguration(
      boolean useOnlyPredefinedConfiguration) {
    this.usingOnlyPredefinedConfiguration = useOnlyPredefinedConfiguration;
  }

  public IResponseObserver getResponseObserver() {
    return responseObserver;
  }

  public void setResponseObserver(IResponseObserver responseObserver) {
    this.responseObserver = responseObserver;
  }

}
