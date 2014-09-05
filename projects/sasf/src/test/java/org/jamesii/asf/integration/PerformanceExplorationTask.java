/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jamesii.SimSystem;
import org.jamesii.asf.integrationtest.bogus.application.simulator.BogusSimulatorFactoryA;
import org.jamesii.asf.integrationtest.bogus.application.simulator.BogusSimulatorFactoryB;
import org.jamesii.asf.integrationtest.bogus.application.simulator.BogusSimulatorFactoryC;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.steering.ExperimentSteererVariable;
import org.jamesii.core.experiments.steering.IExperimentSteerer;
import org.jamesii.core.experiments.steering.SteeredExperimentVariables;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.model.variables.IntVariable;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.StopWatch;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;
import org.jamesii.simspex.exploration.ProblemInstanceSelectionMode;
import org.jamesii.simspex.exploration.calibration.SimpleSimStopTimeCalibrator;
import org.jamesii.simspex.exploration.simple.SimpleSimSpaceExplorer;
import org.jamesii.simspex.exploration.simple.SimpleSimSpaceExplorerFactory;
import org.jamesii.simspex.gui.PerfDBRecorder;
import org.jamesii.simspex.util.SelTreeSetCreation;

/**
 * Subsumes all operations that are required for performance exploration. An
 * instance of {@link IPerformanceDatabase} has to be given.
 * 
 * @author Roland Ewald
 * 
 */
public class PerformanceExplorationTask {

  /**
   * Factory by which the default simulation end time will be multiplied to set
   * the maximal simulation end time.
   */
  public final static int MAX_SIM_SIZE_FACTOR = 100;

  /** The number of randomly drawn parameter setups for exploration. */
  public final int sampleSize;

  /**
   * The upper bound for all dimensions within the models parameter space to be
   * explored.
   */
  public final int maxParamVal;

  /** The portfolio size of the simulation end time calibrator. */
  public final int calibrationPortfolioSize;

  /** The performance measure of interest. */
  public final String perfMeasure;

  /** The benchmark model to be used. */
  public final String model;

  /** The number of replications per benchmark model. */
  public final int replications;

  /**
   * The default simulation end time (multiplied by constant factor for maximal
   * stop time calculation).
   */
  public final double defSimStopTime;

  /** The minimal simulation end time. */
  public final double minSimStopTime;

  /** The desired optimal wall-clock time. */
  public final double optWCTime;

  /** The maximal wall-clock time. */
  public final double maxWCTime;

  /** The factory to generate a simulation space explorer. */
  SimpleSimSpaceExplorerFactory simSpExFactory =
      new SimpleSimSpaceExplorerFactory();

  /** The experiment to collect performance data. */
  BaseExperiment experiment;

  /** The simulation space explorer that was used. */
  SimpleSimSpaceExplorer simSpaceExplorer;

  /** The time it took to execute the performance experiment. */
  double elapsedTime;

  /** The experiment variables to be varied (model parameters). */
  private Set<BaseVariable<?>> experimentVariablesForModel;

  /**
   * Instantiates a new performance exploration task.
   * 
   * @param sampleSizeToExplore
   *          the sample size to explore
   * @param sizeOfCalibrationPortfolio
   *          the size of calibration portfolio
   * @param maximalParameterValue
   *          the maximal parameter value
   * @param numOfReplications
   *          the num of replications
   * @param performanceMeasure
   *          the performance measure
   * @param benchmarkModel
   *          the benchmark model
   * @param minimalSimStopTime
   *          the minimal sim stop time
   * @param defaultSimStopTime
   *          the default sim stop time
   * @param optimalWallclcokTime
   *          the optimal wallclcok time
   * @param maximalWallclcokTime
   *          the maximal wallclcok time
   */
  public PerformanceExplorationTask(int sampleSizeToExplore,
      int sizeOfCalibrationPortfolio, int maximalParameterValue,
      int numOfReplications, String performanceMeasure, String benchmarkModel,
      double minimalSimStopTime, double defaultSimStopTime,
      double optimalWallclcokTime, double maximalWallclcokTime) {
    sampleSize = sampleSizeToExplore;
    calibrationPortfolioSize = sizeOfCalibrationPortfolio;
    maxParamVal = maximalParameterValue;
    perfMeasure = performanceMeasure;
    model = benchmarkModel;
    replications = numOfReplications;
    minSimStopTime = minimalSimStopTime;
    defSimStopTime = defaultSimStopTime;
    optWCTime = optimalWallclcokTime;
    maxWCTime = maximalWallclcokTime;
  }

  /**
   * Configures and executes a performance experiment. Returns the experiment
   * that has been configured and executed, along with its duration.
   * 
   * @param perfDB
   *          the performance database to which the results shall be written
   * @param rngSeed
   *          the RNG seed (set null for random)
   * @param switchOffExploration
   *          flag that determines whether the experiment shall be executed or
   *          not (if not it will just be initialised and null will be returned)
   * 
   * @return the execution duration in seconds
   * @throws Exception
   */
  public Double executePerformanceExploration(IPerformanceDatabase perfDB,
      Long rngSeed, boolean switchOffExploration) throws Exception {

    experiment = new BaseExperiment();
    experiment.setModelLocation(new URI(model));

    PerfDBRecorder perfRecorder = new PerfDBRecorder();
    perfRecorder.getInstanceGenerator().setCurrentMode(
        ProblemInstanceSelectionMode.CONSTANT);
    experiment.getExecutionController().addExecutionListener(perfRecorder);
    experiment.setDefaultSimStopTime(defSimStopTime);

    configureTestCase();

    if (switchOffExploration) {
      return null;
    }

    perfDB.clear();

    return runExperiment(rngSeed, perfRecorder);
  }

  /**
   * Run experiment.
   * 
   * @param rngSeed
   *          the rng seed
   * @param perfRecorder
   *          the perf recorder
   * @return the double
   */
  private double runExperiment(Long rngSeed, PerfDBRecorder perfRecorder) {
    if (rngSeed != null) {
      SimSystem.getRNGGenerator().setSeed(rngSeed);
    }
    StopWatch watch = new StopWatch();
    perfRecorder.start();
    watch.start();
    experiment.execute();
    watch.stop();
    perfRecorder.stop();
    elapsedTime = watch.elapsedSeconds();
    return elapsedTime;
  }

  /**
   * Configure test case.
   * 
   * @throws Exception
   *           the exception
   */
  protected void configureTestCase() throws Exception {
    SelectionTreeSet selectionTreeSet =
        SelTreeSetCreation.createSelectionTreeSet(
            experiment.getModelRWParameters(), experiment.getModelLocation(),
            experiment.getFixedModelParameters());
    System.err.println("Number of available setups:"
        + selectionTreeSet.calculateFactoryCombinations());
    simSpaceExplorer = configureSimSpaceExplorer(selectionTreeSet);
    configureCalibrator();
    configureModelVarsForExploration();
    configureExperimentSteering();
  }

  /**
   * Configure sim space explorer.
   * 
   * @param selectionTreeSet
   *          the selection tree set
   * @return the configured simple sim-space explorer
   */
  protected SimpleSimSpaceExplorer configureSimSpaceExplorer(
      SelectionTreeSet selectionTreeSet) {
    SimpleSimSpaceExplorer explorer =
        simSpExFactory.create(new ParameterBlock(selectionTreeSet,
            SimpleSimSpaceExplorerFactory.SELECTION_TREE_SET), SimSystem.getRegistry().createContext());
    explorer.setNumOfReplications(replications);
    explorer.setMaxModelSpaceElems(sampleSize);
    return explorer;
  }

  /**
   * Configure calibrator.
   */
  protected void configureCalibrator() {
    SimpleSimStopTimeCalibrator calibration =
        (SimpleSimStopTimeCalibrator) simSpaceExplorer.getCalibrator();
    calibration.setMinInitSimTime(minSimStopTime);
    calibration.setMaxSimEndTime(defSimStopTime * MAX_SIM_SIZE_FACTOR);
    calibration.setDesiredWCTime(optWCTime);
    calibration.setMaxWCTime(maxWCTime);
    calibration.setPortfolioSize(calibrationPortfolioSize);
  }

  /**
   * Configure model vars for exploration.
   */
  protected synchronized void configureModelVarsForExploration() {
    if (experimentVariablesForModel != null) {
      return;
    }
    experimentVariablesForModel = simSpaceExplorer.getModelVariables();
    experimentVariablesForModel.add(new IntVariable(Strings
        .dispClassName(BogusSimulatorFactoryA.class), 1, 1, maxParamVal, 1));
    experimentVariablesForModel.add(new IntVariable(Strings
        .dispClassName(BogusSimulatorFactoryB.class), 1, 1, maxParamVal, 1));
    experimentVariablesForModel.add(new IntVariable(Strings
        .dispClassName(BogusSimulatorFactoryC.class), 1, 1, maxParamVal, 1));
  }

  /**
   * Configure experiment steering.
   */
  protected void configureExperimentSteering() {
    ExperimentVariables newExpVars = new ExperimentVariables();
    List<IExperimentSteerer> steerers = new ArrayList<>();
    steerers.add(simSpaceExplorer);
    newExpVars.addVariable(new ExperimentSteererVariable<>("SimSpExSteererVar",
        IExperimentSteerer.class, simSpaceExplorer, new SequenceModifier<>(
            steerers)));
    newExpVars.setSubLevel(new SteeredExperimentVariables<>(
        IExperimentSteerer.class));
    experiment.setExperimentVariables(newExpVars);
  }

  public SimpleSimSpaceExplorerFactory getSimSpExFactory() {
    return simSpExFactory;
  }

  public void setSimSpExFactory(SimpleSimSpaceExplorerFactory simSpExFactory) {
    this.simSpExFactory = simSpExFactory;
  }

  public BaseExperiment getExperiment() {
    return experiment;
  }

  public double getElapsedTime() {
    return elapsedTime;
  }

  public SimpleSimSpaceExplorer getSimSpaceExplorer() {
    return simSpaceExplorer;
  }

  public Set<BaseVariable<?>> getExperimentVariablesForModel() {
    return experimentVariablesForModel;
  }
}
