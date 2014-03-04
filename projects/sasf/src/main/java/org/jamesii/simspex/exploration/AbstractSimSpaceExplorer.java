/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.steering.VariablesAssignment;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.ObservationDelegate;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulationrun.stoppolicy.DisjunctiveSimRunStopPolicyFactory;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Triple;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;
import org.jamesii.simspex.exploration.calibration.ISimStopTimeCalibrator;
import org.jamesii.simspex.exploration.paramblocks.UpdateGenerator;

/**
 * Abstract simulation space explorer class. Defines the main strategy pattern
 * all implementations of {@link ISimSpaceExplorer} should follow.
 * 
 * @author Roland Ewald
 */
public abstract class AbstractSimSpaceExplorer implements ISimSpaceExplorer,
    IObservable {

  /** Defines the configuration space to be explored. */
  private List<ParameterBlock> configurations;

  /** Defines the model space to be explored. */
  private Set<BaseVariable<?>> modelVariables = new HashSet<>();

  /** The current model setup to be used. */
  private Map<String, Serializable> currentModelSetup = new HashMap<>();

  /** Exploration phase, should start with calibration. */
  private transient ExplorationPhase phase = ExplorationPhase.START_CALIBRATION;

  /** The model calibrator to be used. */
  private ISimStopTimeCalibrator calibrator;

  /** The underlying selection tree set. */
  private SelectionTreeSet selectionTreeSet;

  /** The current stop time to be chosen for a simulation run. */
  private double currentSimStopTime = Double.MAX_VALUE;

  /** The current maximal wall clock time for a simulation run (in ms). */
  private Long currentMaxWallClockTime = Long.MAX_VALUE;

  /** The allow sub structures. */
  private boolean allowingSubStructures = false;

  /** The flag that decides whether the steerer is waiting for results. */
  private boolean waiting;

  /** The observation delegate. */
  private ObservationDelegate observationDelegate = new ObservationDelegate(
      this);

  /**
   * Constructor for bean compliance. Do not use manually.
   */
  public AbstractSimSpaceExplorer() {
  }

  /**
   * Default constructor.
   * 
   * @param configs
   *          the set of algorithms to be explored
   */
  public AbstractSimSpaceExplorer(List<ParameterBlock> configs) {
    configurations = configs;
  }

  /**
   * Instantiates a new abstract simulation space explorer.
   * 
   * @param selTreeSet
   *          the selection tree set
   */
  public AbstractSimSpaceExplorer(SelectionTreeSet selTreeSet) {
    this(selTreeSet, null);
  }

  /**
   * Instantiates a new abstract simulation space explorer.
   * 
   * @param selTreeSet
   *          the selelection tree set
   * @param blackList
   *          the black list
   */
  public AbstractSimSpaceExplorer(SelectionTreeSet selTreeSet,
      List<String> blackList) {
    selTreeSet.generateFactoryCombinations(blackList);
    configurations = selTreeSet.getFactoryCombinations();
  }

  @Override
  public VariablesAssignment getNextVariableAssignment() {
    if (waiting) {
      return null;
    }
    waiting = true;
    return getVarAssignment();
  }

  /**
   * Gets the variable assignment.
   * 
   * @return the variable assignment
   */
  public VariablesAssignment getVarAssignment() {

    changed(new Triple<>(phase, currentSimStopTime, currentModelSetup));

    switch (phase) {
    case START_CALIBRATION:
      currentModelSetup = nextModelSetupToExplore();
      if (currentModelSetup == null) {
        return null;
      }
      // Do we have a calibrator?
      if (calibrator == null) {
        phase = ExplorationPhase.EXPLORATION;
        return getVarAssignment();
      }
      phase = ExplorationPhase.CALIBRATION;
      calibrator.setNewModelSetup(currentModelSetup);
      return calibration();
    case CALIBRATION:
      return calibration();
    case EXPLORATION:
      return explorePerformanceSpace();
    }
    return null;
  }

  /**
   * Explore the performance space.
   * 
   * @return the variables assignment to configure the exploration
   */
  private VariablesAssignment explorePerformanceSpace() {
    ParameterBlock config = nextConfigToExplore();
    if (nextProblem()) {
      return newProblemExploration();
    }
    // If this is null, the exploration is finished
    if (config == null) {
      changed(null);
      return null;
    }
    VariablesAssignment va = createAssignment(config);
    va.putAll(currentModelSetup);
    return va;
  }

  /**
   * Manages transition to new element in problem space.
   * 
   * @return the next variable assignment
   */
  protected VariablesAssignment newProblemExploration() {
    currentModelSetup = nextModelSetupToExplore();

    // Are we finished?
    if (currentModelSetup == null) {
      return null;
    }

    if (calibrator != null) {
      calibrator.setNewModelSetup(currentModelSetup);
      phase = ExplorationPhase.CALIBRATION;
    }
    return getVarAssignment();
  }

  /**
   * Select a new model setup to explore.
   * 
   * @return map containing new model parameters
   */
  protected abstract Map<String, Serializable> nextModelSetupToExplore();

  /**
   * Define the parameter block containing the new configuration to be tested.
   * If this method returns null and
   * {@link AbstractSimSpaceExplorer#nextProblem()}, which is called thereafter,
   * returns false (so that no new model setup can be retrieved), the steerer
   * stops.
   * 
   * @return the parameter block containing the new configuration to be tested,
   *         or null if a new model setup shall be used
   */
  protected abstract ParameterBlock nextConfigToExplore();

  /**
   * Decides whether the exploration of this particular problem has finished or
   * not. If the result is true,
   * {@link AbstractSimSpaceExplorer#nextModelSetupToExplore()} will be called
   * subsequently.
   * 
   * @return true if the next problem should be evaluated
   */
  protected abstract boolean nextProblem();

  /**
   * Triggers next call of calibration module.
   * 
   * @return the variable assignment to be executed
   */
  protected VariablesAssignment calibration() {
    Pair<Pair<Double, Long>, ParameterBlock> setup =
        calibrator.getNextSimTime();
    currentSimStopTime = setup.getFirstValue().getFirstValue();
    currentMaxWallClockTime = setup.getFirstValue().getSecondValue();
    VariablesAssignment va = createAssignment(setup.getSecondValue());
    va.putAll(currentModelSetup);
    return va;
  }

  /**
   * Creates a variable assignment for the given simulation end time and
   * configuration.
   * 
   * @param configuration
   *          the configuration to be used
   * 
   * @return a {@link VariablesAssignment} instance containing all necessary
   *         {@link org.jamesii.core.experiments.execonfig.IParamBlockUpdate}
   *         instances
   */
  protected VariablesAssignment createAssignment(ParameterBlock configuration) {
    return UpdateGenerator.getUpdateAssignment(UpdateGenerator
        .generateUpdates(configuration));
  }

  @Override
  public void executionFinished(TaskConfiguration simConfig,
      RunInformation runInfo) {

    waiting = false;

    if (calibrator == null || phase == ExplorationPhase.EXPLORATION) {
      return;
    }

    calibrator.simFinished(runInfo.getTotalRuntime());

    if (calibrator.done()) {
      phase = ExplorationPhase.EXPLORATION;
      currentSimStopTime = calibrator.getCalibratedEndTime();
    }
  }

  @Override
  public boolean allowSubStructures() {
    return allowingSubStructures;
  }

  @Override
  public Set<BaseVariable<?>> getModelVariables() {
    return modelVariables;
  }

  @Override
  public void setModelVariables(Set<BaseVariable<?>> modelVariables) {
    this.modelVariables = modelVariables;
  }

  /**
   * Gets the calibrator.
   * 
   * @return the calibrator
   */
  public ISimStopTimeCalibrator getCalibrator() {
    return calibrator;
  }

  /**
   * Sets the calibrator.
   * 
   * @param calibrator
   *          the new calibrator
   */
  public void setCalibrator(ISimStopTimeCalibrator calibrator) {
    this.calibrator = calibrator;
  }

  /**
   * Gets the current simulation stop time.
   * 
   * @return the current simulation stop time
   */
  public double getCurrentSimStopTime() {
    return currentSimStopTime;
  }

  /**
   * Sets the current simulation stop time.
   * 
   * @param simStopTime
   *          the new current simulation stop time
   */
  public void setCurrentSimStopTime(double simStopTime) {
    this.currentSimStopTime = simStopTime;
  }

  /**
   * Gets the configurations.
   * 
   * @return the configurations
   */
  @Override
  public List<ParameterBlock> getConfigurations() {
    return configurations;
  }

  /**
   * Sets the configurations.
   * 
   * @param configurations
   *          the configurations to set
   */
  @Override
  public void setConfigurations(List<ParameterBlock> configurations) {
    this.configurations = configurations;
  }

  /**
   * Gets the selection tree set.
   * 
   * @return the selectionTreeSet
   */
  @Override
  public SelectionTreeSet getSelectionTreeSet() {
    return selectionTreeSet;
  }

  /**
   * Sets the selection tree set.
   * 
   * @param selectionTreeSet
   *          the selection tree set
   */
  public void setSelectionTreeSet(SelectionTreeSet selectionTreeSet) {
    this.selectionTreeSet = selectionTreeSet;
  }

  /**
   * Checks if is allow sub structures.
   * 
   * @return the allowSubStructures
   */
  public boolean isAllowingSubStructures() {
    return allowingSubStructures;
  }

  /**
   * Sets the allowing sub structures.
   * 
   * @param allowingSubStructures
   *          the allowingSubStructures to set
   */
  public void setAllowingSubStructures(boolean allowingSubStructures) {
    this.allowingSubStructures = allowingSubStructures;
  }

  @Override
  public ParameterBlock getExperimentParameters() {
    // Change the predefined runtime only if calibrator is set
    if (calibrator == null) {
      return new ParameterBlock();
    }
    return configureSimStopping(this.currentSimStopTime,
        this.currentMaxWallClockTime);
  }

  /**
   * Creates parameter block that configures simulation stopping policy.
   * 
   * @param simStopTime
   *          the sim stop time
   * @param maxWallClockTime
   *          the max wall clock time (in ms)
   * @return the parameter block that configures simulation stopping
   */
  public static ParameterBlock configureSimStopping(Double simStopTime,
      Long maxWallClockTime) {
    ParameterBlock expParameters = new ParameterBlock();
    expParameters.addSubBlock(ExperimentVariables.STOP_TIME_FACTORY,
        DisjunctiveSimRunStopPolicyFactory.configureStoppingWithUpperLimit(
            simStopTime, maxWallClockTime));
    return expParameters;
  }

  protected ExplorationPhase getPhase() {
    return phase;
  }

  protected void setPhase(ExplorationPhase phase) {
    this.phase = phase;
  }

  @Override
  public IMediator getMediator() {
    return observationDelegate.getMediator();
  }

  @Override
  public void registerObserver(IObserver observer) {
    observationDelegate.registerObserver(observer);
  }

  @Override
  public void setMediator(IMediator mediator) {
    observationDelegate.setMediator(mediator);
  }

  @Override
  public void unregisterObserver(IObserver observer) {
    observationDelegate.unregisterObserver(observer);
  }

  @Override
  public void unregisterObservers() {
    observationDelegate.unregisterObservers();
  }

  @Override
  public void changed() {
    observationDelegate.changed();
  }

  @Override
  public void changed(Object hint) {
    observationDelegate.changed(hint);
  }

}