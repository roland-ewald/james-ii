/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.experiments.optimization.algorithm.IOptimizationAlgorithm;
import org.jamesii.core.experiments.optimization.parameter.Configuration;
import org.jamesii.core.experiments.optimization.parameter.IOptimizationObjective;
import org.jamesii.core.experiments.optimization.parameter.OptimizationProblemDefinition;
import org.jamesii.core.experiments.optimization.parameter.cancellation.ICancelOptimizationCriterion;
import org.jamesii.core.experiments.optimization.parameter.representativeValue.IRepresentativeValuesComparator;
import org.jamesii.core.experiments.optimization.parameter.representativeValue.SimpleRepValueComparator;
import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.experiments.steering.IExperimentSteerer;
import org.jamesii.core.experiments.steering.VariablesAssignment;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.IConstraint;

/**
 * Base class for all optimisation algorithms. Describes the base functionality
 * of an optimisation algorithm, concerning the main loop:
 * 
 * 1. Get responses
 * 
 * 2. Check constraints
 * 
 * 3. Do replications
 * 
 * 4. check cancel criteria
 * 
 * 5. set next configuration.
 * 
 * 
 * This is a sequential optimiser. See {@link ParallelOptimizer} for a parallel
 * version.
 * 
 * Provides a data interface to store and get results.
 * 
 * @author Arvid Schwecke
 * @author Roland Ewald
 * @author Peter Sievert
 * @author Jan Himmelspach
 */
public class Optimizer extends Entity implements IExperimentSteerer {
  static {
    SerialisationUtils.addDelegateForConstructor(Optimizer.class,
        new IConstructorParameterProvider<Optimizer>() {
          @Override
          public Object[] getParameters(Optimizer opt) {
            Object[] params =
                new Object[] { opt.algorithm, opt.optimizationProblem };
            return params;
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = 4341712771781411434L;

  /**
   * Unless the naive implementation of the cache is replaced by a better one,
   * activation of cache is discouraged! (Additional replication criteria should
   * be applied to configuration infos).
   */
  public boolean useCache = false;

  /** We have at least a start configuration. */
  private int configCounter = 0;

  /** The information to the current configuration runs. */
  private ConfigurationInfos currentConfigurationRuns;

  /** Storage for tested configurations. */
  private IConfigurationStorage db;

  /** The optimization algorithm to be used. */
  private IOptimizationAlgorithm algorithm;

  /** Contains description of what to optimise, when to abort, etc. */
  private OptimizationProblemDefinition optimizationProblem;

  /** Contains the progress of optimisation, it count runs and used time. */
  private OptimizationStatistics state = new OptimizationStatistics();

  private Map<Configuration, Map<String, Double>> paretoFront =
      new LinkedHashMap<>();

  /**
   * Compares two lists containing the objective values for two configurations
   * (smaller is better).
   */
  private IRepresentativeValuesComparator reprValueComparator =
      new SimpleRepValueComparator();

  /**
   * Flag to determine whether the first configuration has already been created.
   */
  private boolean firstConfigCreated = false;

  /**
   * Flag to check whether 'next' has been called before, used to enforce
   * sequential config-by-config execution.
   */
  private boolean nextHasBeenCalledBefore;

  /**
   * Default constructor.
   * 
   * @param algorithm
   *          the algorithm
   * @param problemDefinition
   *          the problem definition
   */
  public Optimizer(IOptimizationAlgorithm algorithm,
      OptimizationProblemDefinition problemDefinition) {
    this.optimizationProblem = problemDefinition;
    this.algorithm = algorithm;
    // Select DB
    setDb(new SimpleConfigurationStorage());
  }

  @Override
  public boolean isFinished() {
    return meetCancelCriteria(state);
  }

  /**
   * This is called after one optimisation step if needed implement
   * functionality here.
   * 
   * @throws NoNextVariableException
   *           if no further optimisation steps exists
   */
  protected void newConfigurationCreated() throws NoNextVariableException {

    Map<String, Double> representedValues =
        optimizationProblem.calcRepresentedValue(currentConfigurationRuns);

    Configuration tempConfig;
    try {
      tempConfig = currentConfigurationRuns.getConfiguration().clone();
    } catch (CloneNotSupportedException e) {
      throw new NoNextVariableException(e);
    }

    compareWithBestConfiguration(tempConfig, representedValues);

    // refresh state
    state.configurationDone(currentConfigurationRuns, representedValues);

    // store this configuration
    getDb().addConfiguration(currentConfigurationRuns);

    ConfigurationInfo lastInfo =
        currentConfigurationRuns.getLastConfigurationInfo();

    algorithm.addResults(lastInfo.getConfiguration(), lastInfo.getObjectives(),
        paretoFront);
    // currentConfigurationIsDone(currentConfigurationRuns, representedValues);

  }

  /**
   * Compares current configuration with best configuration (by their
   * representative value).
   * 
   * @param configuration
   *          the configuration
   * @param representativeValues
   *          the represented value
   */
  protected void compareWithBestConfiguration(Configuration configuration,
      Map<String, Double> representativeValues) {

    boolean checkFlag = true;

    if (paretoFront.isEmpty()) {
      paretoFront.put(configuration, representativeValues);
    }
    Map<Configuration, Map<String, Double>> newEntries = new LinkedHashMap<>();
    for (Iterator<Configuration> it = paretoFront.keySet().iterator(); it
        .hasNext();) {
      Configuration config = it.next();
      Map<String, Double> values = paretoFront.get(config);
      int comp = reprValueComparator.compare(representativeValues, values);
      if (comp > 0) {
        checkFlag = false;
      }
      if (comp <= 0) {

        if (comp != 0) {
          it.remove();
          // newEntries.put(configuration, representativeValues);
        }
      }
    }
    if (checkFlag) {
      newEntries.put(configuration, representativeValues);
    }
    if (!newEntries.isEmpty()) {
      paretoFront.putAll(newEntries);
    }
  }

  /**
   * Checks pre-constraints.
   * 
   * @throws NoNextVariableException
   *           the no next variable exception
   */
  protected void checkConfigurationAndFindOtherIfNeeded()
      throws NoNextVariableException {

    // Configuration violates at least one constraint
    while (!meetPreConstraints(currentConfigurationRuns.getConfiguration())) {
      SimSystem.report(Level.INFO, currentConfigurationRuns.getConfiguration().toString()
      + " is out of pre constraints");
      // Due to no responses set response to null, time to 0, and objective to
      // infinity
      currentConfigurationRuns.addRun(null,
          createPositiveInfinityObjectives(optimizationProblem
              .getOptimizationObjectives()), 0);
      // Call optimization step done
      newConfigurationCreated();
      state.preConstraintsViolated();
      // Try next state
      currentConfigurationRuns =
          new ConfigurationInfos(algorithm.getNextConfiguration(),
              ++configCounter, reprValueComparator);
    }
    // Exist configuration in cache?
    if (getDb().containsConfiguration(
        currentConfigurationRuns.getConfiguration())
        && useCache) {
      // TODO: Just now this is a *naive* implementation: what happens if number
      // of replications is not sufficient?
      SimSystem.report(Level.INFO, currentConfigurationRuns.getConfiguration().toString()
      + " found in storage. Using storage.");
      // get objectives
      currentConfigurationRuns =
          getDb().getConfigurationInfos(
              currentConfigurationRuns.getConfiguration());
      currentConfigurationRuns.setStorageUse(true);
      // and do another step
      next();
    }
  }

  /**
   * Creates a map containing given names and a positive infinity for each of
   * them.
   * 
   * @param names
   *          the names
   * @return the map with positive infinity values
   */
  public static Map<String, Double> createPositiveInfinityObjectives(
      Set<String> names) {
    Map<String, Double> result = new HashMap<>();
    for (String name : names) {
      result.put(name, Double.POSITIVE_INFINITY);
    }
    return result;
  }

  /**
   * Creates a map containing names of the given objectives and a positive
   * infinity for each of them.
   * 
   * @param objectives
   *          the objectives
   * @return the map with positive infinity values
   */
  public static Map<String, Double> createPositiveInfinityObjectives(
      List<IOptimizationObjective> objectives) {
    Map<String, Double> result = new HashMap<>();
    for (IOptimizationObjective objective : objectives) {
      result.put(objective.getName(), Double.POSITIVE_INFINITY);
    }
    return result;
  }

  /**
   * Gets the optimisation parameter.
   * 
   * @return the optimisation parameter
   */
  public OptimizationProblemDefinition getOptimizationParameter() {
    return optimizationProblem;
  }

  /**
   * Checks if the optimisation has to abort/quit.
   * 
   * @param stats
   *          the current statistics on the optimisation process
   * 
   * @return true if at least one criteria returns true
   */
  protected boolean meetCancelCriteria(OptimizationStatistics stats) {
    boolean result = false;
    for (ICancelOptimizationCriterion criterion : optimizationProblem
        .getCancelOptimizationCriteria()) {
      result = result || criterion.meetsCancelCriterion(stats);
    }
    return result;
  }

  /**
   * Meet post constraints.
   * 
   * @param configInfo
   *          the configuration info
   * 
   * @return true, if successful
   */
  protected boolean meetPostConstraints(ConfigurationInfo configInfo) {
    boolean result = true;
    for (IConstraint<ConfigurationInfo> contraint : optimizationProblem
        .getPostConstraints()) {
      result = result && contraint.isFulfilled(configInfo);
    }
    return result;
  }

  /**
   * Meet pre-constraints.
   * 
   * @param configuration
   *          the configuration
   * 
   * @return true if successful
   */
  protected boolean meetPreConstraints(Configuration configuration) {
    boolean result = true;
    for (IConstraint<Configuration> constraint : optimizationProblem
        .getPreConstraints()) {
      result = result && constraint.isFulfilled(configuration);
    }
    return result;
  }

  /**
   * Describes the main loop of optimisation and returns the next configuration.
   * 
   * @return the configuration for the next run
   * 
   * @throws NoNextVariableException
   *           the no next variable exception
   */
  public Configuration next() throws NoNextVariableException {

    // Enforce to consider only a single configuration (sequential execution)
    if (nextHasBeenCalledBefore) {
      nextHasBeenCalledBefore = false;
      return null;
    }
    nextHasBeenCalledBefore = true;

    // Do sth. after each configuration (calc representative objective etc.)
    if (!firstConfigCreated) {
      firstConfigCreated = true;
    } else {
      newConfigurationCreated();
    }

    // CancelCriteria
    if (meetCancelCriteria(state)) {
      SimSystem.report(Level.INFO, "Optimization meets a cancel criteria.");
      throw new NoNextVariableException();
    }

    // Start new Configuration: check pre conditions on new factos
    currentConfigurationRuns =
        new ConfigurationInfos(algorithm.getNextConfiguration(),
            ++configCounter, reprValueComparator);
    checkConfigurationAndFindOtherIfNeeded();
    return currentConfigurationRuns.getConfiguration();

  }

  /**
   * This is called after hitting a post constraint; implement fall-back.
   */
  protected void postConstraintViolated() {
    // TODO
  }

  @Override
  public void executionFinished(TaskConfiguration simConfig,
      RunInformation runInfo) {

    Map<String, BaseVariable<?>> currentResponses =
        extractResponse(runInfo.getResponse());

    executionFinished(currentResponses, runInfo.getTotalRuntime());

  }

  /**
   * Execution finished.
   * 
   * @param currentResponses
   *          the current responses
   * @param runTime
   *          the run time
   */
  public void executionFinished(Map<String, BaseVariable<?>> currentResponses,
      Double runTime) {
    Map<String, Double> currentObjectives =
        createPositiveInfinityObjectives(optimizationProblem
            .getOptimizationObjectives());
    currentConfigurationRuns.addRun(currentResponses, runTime);

    // Test if the result violates a post-constraint:
    if (!(meetPostConstraints(currentConfigurationRuns
        .getLastConfigurationInfo()))) {
      SimSystem.report(Level.INFO, currentConfigurationRuns.getConfiguration().toString()
      + " violates post constraints!");
      if (currentConfigurationRuns.isStorageUse()) {
        state.postConstraintsViolated();
      }
      postConstraintViolated();
    } else {
      // calculate the objective on the response and store for variance
      // calculation(repeat runs)
      currentObjectives =
          optimizationProblem.calcObjectives(
              currentConfigurationRuns.getConfiguration(), currentResponses);
    }

    currentConfigurationRuns.getLastConfigurationInfo().setObjectives(
        currentObjectives);
  }

  public ConfigurationInfos getCurrentConfigurationRuns() {
    return currentConfigurationRuns;
  }

  /**
   * Extract response.
   * 
   * @param response
   *          the response
   * 
   * @return the map< string, base variable<?>>
   */
  protected Map<String, BaseVariable<?>> extractResponse(
      Map<String, Object> response) {
    Map<String, BaseVariable<?>> currentResponses = new HashMap<>();
    if (response != null) {
      for (Object o : response.values()) {
        if (o instanceof BaseVariable<?>) {
          currentResponses.put(((BaseVariable<?>) o).getName(),
              (BaseVariable<?>) o);
        }
      }
    }
    return currentResponses;
  }

  @Override
  public boolean allowSubStructures() {
    return false;
  }

  @SuppressWarnings("deprecation")
  @Override
  public ParameterBlock getExperimentParameters() {
    ParameterBlock result = new ParameterBlock();
    List<IModelInstrumenter> modelInstrumenters = new ArrayList<>();
    IModelInstrumenter mi =
        optimizationProblem.getResponseObsModelInstrumenter();
    if (mi != null) {
      modelInstrumenters.add(mi);
    }
    result.addSubBlock(ExperimentVariables.MODEL_INSTRUMENTERS,
        modelInstrumenters);
    List<IComputationInstrumenter> simInstrumenters = new ArrayList<>();
    IComputationInstrumenter si =
        optimizationProblem.getResponseObsSimInstrumenter();
    if (si != null) {
      simInstrumenters.add(si);
    }
    result.addSubBlock(ExperimentVariables.SIMULATION_INSTRUMENTERS,
        simInstrumenters);
    // TODO create replication criteria
    result.addSubBlock(ExperimentVariables.REPLICATION_CRITERION_FACTORIES,
        new ArrayList<ParameterizedFactory<RepCriterionFactory>>());
    return result;
  }

  @Override
  public VariablesAssignment getNextVariableAssignment() {
    VariablesAssignment assignment = new VariablesAssignment();
    try {
      Configuration conf = next();
      if (conf == null) {
        return null;
      }
      for (BaseVariable<?> var : conf.values()) {
        assignment.put(var.getName(), var);
      }
    } catch (NoNextVariableException ex) {
      SimSystem.report(Level.INFO, state.toString());
      return null;
    }
    return assignment;
  }

  @Override
  public void init() {
    this.firstConfigCreated = false;
  }

  public Map<Configuration, Map<String, Double>> getParetoFront() {
    return paretoFront;
  }

  public void setReprValueComparator(
      IRepresentativeValuesComparator reprValueComparator) {
    this.reprValueComparator = reprValueComparator;
  }

  /**
   * @return the db
   */
  protected final IConfigurationStorage getDb() {
    return db;
  }

  /**
   * @param db
   *          the db to set
   */
  protected final void setDb(IConfigurationStorage db) {
    this.db = db;
  }

  /**
   * @return the state
   */
  protected final OptimizationStatistics getState() {
    return state;
  }

  /**
   * @param state
   *          the state to set
   */
  protected final void setState(OptimizationStatistics state) {
    this.state = state;
  }

  /**
   * @return the algorithm
   */
  protected final IOptimizationAlgorithm getAlgorithm() {
    return algorithm;
  }

  /**
   * @param algorithm
   *          the algorithm to set
   */
  protected final void setAlgorithm(IOptimizationAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * @return the reprValueComparator
   */
  protected final IRepresentativeValuesComparator getReprValueComparator() {
    return reprValueComparator;
  }

  /**
   * @return the configCounter
   */
  protected final int getConfigCounter() {
    return configCounter;
  }

  /**
   * @param configCounter
   *          the configCounter to set
   */
  protected final void setConfigCounter(int configCounter) {
    this.configCounter = configCounter;
  }
}
