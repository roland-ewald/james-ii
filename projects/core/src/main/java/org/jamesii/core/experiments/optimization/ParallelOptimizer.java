/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.optimization.algorithm.IOptimizationAlgorithm;
import org.jamesii.core.experiments.optimization.parameter.Configuration;
import org.jamesii.core.experiments.optimization.parameter.ParallelOptimizationProblemDefinition;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.model.variables.BaseVariable;

/**
 * Base class for all parallel optimisation algorithms. Describes the base
 * functionality of an parallel optimisation algorithm. Other than the
 * sequential optimiser, this may not be understood as a loop, but rather there
 * are two main functions: Accepting results in update() and giving new
 * Configurations in next(). Relating to optimiser, this means a less exhausting
 * use of object variables in methods, especially concerning
 * "currentConfigurationsRuns".
 * 
 * @author Peter Sievert 
 * @date 05.08.2008
 */
public class ParallelOptimizer extends Optimizer {

  /** Generated serialVersionUID. */
  private static final long serialVersionUID = -2604117666483493971L;

  /**
   * This list stores all running ConfigurationsInfos, so if an answer is
   * received, one can find the corresponding configuration infos in this list.
   * Since a sequential optimiser is extended, this optimiser's behaviour is
   * supposed to be like the parallelisation of processes on a sequential CPU in
   * operating systems. In this way most of the extended methods can still be
   * used or at least most of their code is reusable. There has been a method
   * integrated into ConfigurationInfos for this purpose, so that for an
   * arriving response in update() the right infos from the
   * runningConfigurations can be found.
   */
  private List<ConfigurationInfos> runningConfigurations;

  /**
   * This list contains all ConfigurationInfos that are not running at the
   * moment, but that need further replication. If it isn't empty the process of
   * next() consists simply of getting a configuration from it.
   */
  private List<ConfigurationInfos> configsWaitingForFurtherReplication;

  /**
   * List of replication criteria that control the number of replications (using
   * AND, i.e. all criteria have to return true if the replications can stop)
   * Using the former replication criteria, one would have to assure, that
   * optimiser and simulation runner are using the same replication criteria.
   * This seems to be a problem, since there is no complete RunInformation which
   * can be used here from inside the optimiser. Anyway the Data in
   * RunInformation seems inappropriate to control the number of replications.
   * For this reason a new Interface for ReplicationCriteria was defined, which
   * takes ConfigurationInfos as Input. The replication criteria for the
   * SimulationRunner don't need to be used in this way.
   */
  private List<IOptimizationReplicationCriterion> replicationCriteria =
      new ArrayList<>();

  /**
   * This determines how many simulation configurations results will be kept in
   * the experiment history.
   */
  private int sizeOfHistory;

  /** The time. */
  private long time;

  /**
   * Contains description of what to optimise, when to abort, etc. In addition
   * to the serial OptimizationProblemDefinition there have to be some specific
   * extra methods and values.
   */
  private ParallelOptimizationProblemDefinition optimizationProblem;

  /**
   * Is set to true if optimization run is finished.
   */
  private boolean finished = false;

  /**
   * Instantiates a new parallel optimiser.
   * 
   * @param pd
   *          the problem definition
   */
  public ParallelOptimizer(IOptimizationAlgorithm algorithm,
      ParallelOptimizationProblemDefinition pd) {
    super(algorithm, pd);
    setDb(new SimpleConfigurationStorage());
    optimizationProblem = pd;
    runningConfigurations = new ArrayList<>();
    configsWaitingForFurtherReplication = new ArrayList<>();
    useCache = true;
    sizeOfHistory = 50;
  }

  @Override
  public boolean isFinished() {
    return finished;
  }

  /**
   * This must be called from update and not from next(), since one can not
   * assure, there will be no other configuration finished. That is why the
   * replication criteria have to be integrated into an parallel optimiser.
   * 
   * <br/>
   * Throws a NoNextVariableException if no further optimisation steps exists.
   * 
   * @param configInfos
   *          the configuration infos
   * 
   * 
   */
  protected void currentConfigurationIsDone(ConfigurationInfos configInfos)
      throws NoNextVariableException {
    Map<String, Double> representedValues =
        optimizationProblem.calcRepresentedValue(configInfos);

    getState().configurationDone(configInfos, representedValues);
    getDb().addConfiguration(configInfos);
    for (ConfigurationInfo info : configInfos.getConfigInfos()) {
      getAlgorithm().addResults(info.getConfiguration(), info.getObjectives(),
          getParetoFront());
    }
  }

  /**
   * Checks whether a configuration is already running somewhere.
   * 
   * @param configInfos
   *          the configuration infos
   * 
   * @return true, if successful
   */
  private boolean alreadyRunning(ConfigurationInfos configInfos) {

    if (runningConfigurations.size() == 0) {
      return false;
    }

    int position = 0;
    Configuration config = configInfos.getConfiguration();

    while (position < runningConfigurations.size()) {
      if (runningConfigurations.get(position).containsConfiguration(config)) {
        return true;
      }
      position++;
    }

    return false;
  }

  /**
   * Checks pre-constraints.
   * 
   * @param configurationInfos
   *          the configuration infos
   * 
   * @return the configuration infos
   * 
   * @throws NoNextVariableException
   *           the no next variable exception
   */
  protected ConfigurationInfos checkConfigurationAndFindOtherIfNeeded(
      ConfigurationInfos configurationInfos) throws NoNextVariableException {

    // Configuration violates at least one constraint
    while ((!meetPreConstraints(configurationInfos.getConfiguration()))
        || alreadyRunning(configurationInfos)) {
      if (!alreadyRunning(configurationInfos)) {
        SimSystem.report(Level.INFO, configurationInfos.getConfiguration().toString()
        + " is out of pre constraints");
        // Due to no responses set response to null, time to 0, and objective to
        // infinity
        configurationInfos.addRun(null,
            createPositiveInfinityObjectives(optimizationProblem
                .getOptimizationObjectives()), 0);
        // Call optimization step done
        // currentConfigurationIsDone(_configurationinfos);
        currentConfigurationIsDone(configurationInfos);
        getState().preConstraintsViolated();
        // Try next state
        configurationInfos =
            new ConfigurationInfos(getAlgorithm().getNextConfiguration(),
                getConfigCounter(), getReprValueComparator());
        setConfigCounter(getConfigCounter() + 1);
      } else {
        configurationInfos =
            new ConfigurationInfos(getAlgorithm().getNextConfiguration(),
                getConfigCounter(), getReprValueComparator());
      }
    }
    // Exist configuration in cache?
    while (getDb().containsConfiguration(configurationInfos.getConfiguration())
        && useCache) {

      SimSystem.report(Level.INFO, configurationInfos.getConfiguration().toString()
      + " found in storage. Use storage.");

      // get objectives
      configurationInfos =
          getDb().getConfigurationInfos(configurationInfos.getConfiguration());

      // if already enough replications, db results can be used and another
      // Configuration is needed
      if (configurationNeedsMoreReplications(configurationInfos)) {
        return configurationInfos;
      }
      for (ConfigurationInfo info : configurationInfos.getConfigInfos()) {
        getAlgorithm().addResults(info.getConfiguration(),
            info.getObjectives(), getParetoFront());
      }
      configurationInfos =
          new ConfigurationInfos(getAlgorithm().getNextConfiguration(),
              getConfigCounter(), getReprValueComparator());
      setConfigCounter(getConfigCounter() + 1);

    }
    return configurationInfos;
  }

  /**
   * Returns the next configuration. Everything, that is not optimizer specific,
   * should be taken care of here.
   * 
   * @return Factors - the configuration for the next run
   * 
   * @throws NoNextVariableException
   *           the no next variable exception
   */
  @Override
  public Configuration next() throws NoNextVariableException {

    if (finished) {
      throw new NoNextVariableException();
    }

    ConfigurationInfos newConfigInfos = null;

    // CancelCriteria
    if (meetCancelCriteria(getState())) {
      SimSystem.report(Level.INFO, "Optimization meets a cancel criteria.");
      throw new NoNextVariableException();
    }

    // check if further replication of a former Configuration is needed
    // if so, put it into the running pool and return it
    if (configsWaitingForFurtherReplication.size() > 0) {
      newConfigInfos = configsWaitingForFurtherReplication.remove(0);
      runningConfigurations.add(newConfigInfos);
      return newConfigInfos.getConfiguration();
    }

    newConfigInfos =
        new ConfigurationInfos(getAlgorithm().getNextConfiguration(),
            getConfigCounter(), getReprValueComparator());

    setConfigCounter(getConfigCounter() + 1);
    newConfigInfos = checkConfigurationAndFindOtherIfNeeded(newConfigInfos);
    runningConfigurations.add(newConfigInfos);
    return newConfigInfos.getConfiguration();
  }

  /**
   * Configuration needs more replications.
   * 
   * @param configInfos
   *          the configuration infos
   * 
   * @return true, if successful
   */
  private boolean configurationNeedsMoreReplications(
      ConfigurationInfos configInfos) {

    boolean result = false;

    for (IOptimizationReplicationCriterion criterion : replicationCriteria) {
      if (criterion.sufficientReplications(configInfos.getConfigInfos(),
          getState().getEvaluatedSolutions()) > 0) {
        result = true;
      }
    }

    return result;
  }

  /**
   * Find configuration infos from list of running ones, by configuration.
   * 
   * @param config
   *          the parameter configuration
   * 
   * @return the corresponding configuration infos, null if none were found
   */
  private ConfigurationInfos findConfigInfosInRunningByConfiguration(
      Configuration config) {
    // look for position of configuration
    int position = 0;

    while ((position < runningConfigurations.size())
        && (!runningConfigurations.get(position).containsConfiguration(config))) {
      position++;
    }

    if (position < runningConfigurations.size()) {
      ConfigurationInfos result = runningConfigurations.remove(position);
      return result;
    }
    return null;
  }

  /**
   * Extract configuration from simulation configuration.
   * 
   * @param simConfig
   *          the simulation configuration
   * 
   * @return the corresponding (parameter-) configuration
   */
  private Configuration extractConfigurationFromSimulationConfiguration(
      TaskConfiguration simConfig) {

    Configuration config;
    try {
      config = optimizationProblem.createFactors();
    } catch (CloneNotSupportedException e) {
      SimSystem.report(e);
      return null;
    }

    for (BaseVariable<?> factor : config.values()) {
      BaseVariable<?> temp =
          (BaseVariable<?>) simConfig.getParameters().get(factor.getName());
      config.put(factor.getName(), temp);
    }

    return config;
  }

  @Override
  public void executionFinished(TaskConfiguration simConfig,
      RunInformation runInfo) {

    Configuration arrivedConfig =
        extractConfigurationFromSimulationConfiguration(simConfig);

    Map<String, BaseVariable<?>> observedResponses =
        extractResponse(runInfo.getResponse());

    Map<String, Double> currentObjective =
        createPositiveInfinityObjectives(optimizationProblem
            .getOptimizationObjectives());

    ConfigurationInfos currentConfigInfos =
        findConfigInfosInRunningByConfiguration(arrivedConfig);

    currentConfigInfos.addRun(observedResponses, runInfo.getTotalRuntime());

    // Test if the result violates a post-constraint:
    if (!(meetPostConstraints(currentConfigInfos.getLastConfigurationInfo()))) {
      SimSystem.report(Level.INFO, currentConfigInfos.getConfiguration().toString()
      + " violates post constraints!");
      getState().postConstraintsViolated();
      postConstraintViolated();
    } else {
      // calculate the objective on the response and store for variance
      // calculation of replications
      currentObjective =
          optimizationProblem.calcObjectives(
              currentConfigInfos.getConfiguration(), observedResponses);
    }

    currentConfigInfos.getLastConfigurationInfo().setObjectives(
        currentObjective);

    try {
      currentSimulationIsDone(currentConfigInfos);
    } catch (NoNextVariableException ex) {
      finished = true;
    }
  }

  /**
   * Current simulation is done.
   * 
   * @param configInfos
   *          the configuration infos
   */
  private void currentSimulationIsDone(ConfigurationInfos configInfos)
      throws NoNextVariableException {
    if (configurationNeedsMoreReplications(configInfos)) {
      configsWaitingForFurtherReplication.add(configInfos);
    } else {
      currentConfigurationIsDone(configInfos);
    }
  }

  /**
   * Gets the size of history.
   * 
   * @return the size of history
   */
  public int getSizeOfHistory() {
    return sizeOfHistory;
  }

  /**
   * Sets the size of history.
   * 
   * @param _sizeOfHistory
   *          the new size of history
   */
  public void setSizeOfHistory(int _sizeOfHistory) {
    sizeOfHistory = _sizeOfHistory;
  }

  @Override
  protected void newConfigurationCreated() throws NoNextVariableException {
    // nothing to do here, this method is not suitable for parallel purposes
  }

  /**
   * Adds the optimisation replication criterion.
   * 
   * @param _criterion
   *          the _criterion
   */
  public void addOptimizationReplicationCriterion(
      IOptimizationReplicationCriterion _criterion) {
    this.replicationCriteria.add(_criterion);
  }

  /**
   * Clear optimisation replication criteria.
   */
  public void clearOptimizationReplicationCriteria() {
    this.replicationCriteria.clear();
  }
}
