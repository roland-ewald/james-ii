/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.algoselect.SelectionInformation;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.IExperimentExecutionListener;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.util.Hook;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.ConstraintException;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IHardwareSetup;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.perfdb.entities.IProblemScheme;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.performance.IPerformanceMeasurer;
import org.jamesii.perfdb.recording.performance.plugintype.AbstractPerformanceMeasurerFactory;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;
import org.jamesii.perfdb.recording.selectiontrees.SelectionHook;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;
import org.jamesii.simspex.exploration.ProblemInstanceSelectionMode;
import org.jamesii.simspex.util.BenchmarkModelType;
import org.jamesii.simspex.util.DatabaseUtils;
import org.jamesii.simspex.util.SimulationProblemDefinition;

/**
 * Recorder for performance DB. Hooks itself into the factory selection
 * mechanism offered by the {@link org.jamesii.core.Registry}, via an instance
 * of {@link SelectionHook}.
 * 
 * TODO: Use {@link org.jamesii.perfdb.recording.PerfDBRecordingHelper}.
 * 
 * @author Roland Ewald
 */
public class PerfDBRecorder implements IExperimentExecutionListener {

  /**
   * The default number of save operations before the database gets flushed (to
   * prevent data loss).
   */
  private static final int DEFAULT_FLUSH_INTERVAL = 20;

  /** Title of error messages. */
  static final String ERR_MSG_TITLE = "Performance DB Error:";

  /** Title of error messages concerning constraint violations. */
  static final String CONSTR_ERR_MSG_TITLE =
      "Performance DB Error (Constraint violation):";

  /** Selection hook to generate selection trees. */
  private SelectionHook selectionHook = null;

  /** Generator for problem instances. */
  private ProblemInstanceGenerator instanceGenerator =
      new ProblemInstanceGenerator();

  /**
   * Set of runtime configuration IDs to mark those that have been altered by a
   * new version of an algorithm.
   */
  private Set<Long> newVerRTConfigIDs = new HashSet<>();

  /** The listeners of the performance recorder. */
  private List<IPerfRecorderListener> listeners =
      new ArrayList<>();

  /** Number of executed runs until the performance database gets flushed. */
  private int flushInterval = DEFAULT_FLUSH_INTERVAL;

  /**
   * Maps the execution IDs to the problem instances that were created on
   * simulation initialization.
   */
  private Map<String, IProblemInstance> recordedProblemInstances =
      new HashMap<>();

  /** The performance measurers that already have been used. */
  private final Map<String, IPerformanceMeasurer<?>> performanceMeasurers =
      new HashMap<>();

  /** Counter for executed runs. */
  private int executionCounter = 0;

  /** The performance database in which the recorded data shall be written. */
  private final IPerformanceDatabase perfDatabase;

  /**
   * Instantiates a new performance DB recorder with the default performance
   * database.
   */
  public PerfDBRecorder() {
    this(SimSpExPerspective.getPerformanceDataBase());
  }

  /**
   * Default constructor.
   */
  public PerfDBRecorder(IPerformanceDatabase performanceDatabase) {

    perfDatabase = performanceDatabase;

    // Check if selection hook is already installed
    Hook<SelectionInformation<?>> hook =
        SimSystem.getRegistry().getFactorySelectionHook();
    while (hook != null) {
      if (hook instanceof SelectionHook) {
        selectionHook = (SelectionHook) hook;
        break;
      }
      hook = hook.getOldHook();
    }

    if (selectionHook == null) {
      selectionHook =
          new SelectionHook(SimSystem.getRegistry().getFactorySelectionHook());
      SimSystem.getRegistry().installFactorySelectionHook(selectionHook);
    }
  }

  @Override
  public synchronized void simulationExecuted(ITaskRunner simRunner,
      ComputationTaskRuntimeInformation crti, boolean jobDone) {

    IProblemInstance probInstance =
        recordedProblemInstances
            .get(crti.getRunInformation().getExecutionIDs());
    if (probInstance == null) {
      return;
    }

    SelectionTree selectionTree =
        selectionHook.getSelectionTree(crti.getComputationTaskID());
    IRuntimeConfiguration rtConfig = registerRuntimeConfig(selectionTree);
    if (rtConfig == null) {
      SimSystem
          .report(Level.WARNING,
              "Could not record performance, runtime configuration could not be resolved.");
      return;
    }

    // TODO register hardware (open dialog for user input or detect
    // automatically)

    try {
      registerPerformance(probInstance, rtConfig, null, crti);
    } catch (Exception ex) {
      SimSystem
          .report(Level.SEVERE,
              ERR_MSG_TITLE + "Performance registration failed because of:"
                  + ex.getMessage(), ex);
    }

    executionCounter++;
    if (executionCounter % flushInterval == 0) {
      try {
        perfDatabase.flush();
      } catch (Exception ex) {
        SimSystem.report(Level.SEVERE, null, ERR_MSG_TITLE
            + "Flushing performance database failed", null, ex);
      }
    }

    selectionHook.clearTask(crti.getComputationTaskID());
  }

  /**
   * Registers runtime configuration.
   * 
   * @param selectionTree
   *          the selection tree
   * @return an existing runtime configuration that matches, null otherwise
   * @throws Exception
   *           if look up/de-serialisation went wrong
   */
  protected IRuntimeConfiguration registerRuntimeConfig(
      SelectionTree selectionTree) {

    IRuntimeConfiguration rtConf = null;
    try {

      rtConf = perfDatabase.newRuntimeConfiguration(selectionTree, false);

      // If the registered runtime configuration belongs to those that
      // are affected by an algorithmic change, tell the performance
      // database to create a new version, and remove the ID from the set (so
      // that this does not happen multiple times)
      if (newVerRTConfigIDs.contains(rtConf.getID())) {
        rtConf = perfDatabase.newRuntimeConfiguration(selectionTree, true);
        newVerRTConfigIDs.remove(rtConf.getID());
      }

    } catch (ConstraintException ex) {
      SimSystem.report(Level.SEVERE, null,
          CONSTR_ERR_MSG_TITLE + ex.getMessage(), null, ex);
      return null;
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, null, ERR_MSG_TITLE
          + "Error while storing runtime configuration.", null, ex);
      return null;
    }

    return rtConf;
  }

  @Override
  public synchronized void simulationInitialized(ITaskRunner simRunner,
      ComputationTaskRuntimeInformation crti) {

    // TODO revise repeatability/RNG system to allow repeatable
    // multi-threaded execution
    IProblemInstance pInstance = null;
    try {
      pInstance = registerProblemInstance(crti);
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, null, ERR_MSG_TITLE
          + "Error while storing simulation problem.", null, ex);
    }

    if (pInstance != null) {
      // TODO this late setting of the seed does not work with randomly created
      // models (see above)
      SimSystem.getRNGGenerator().setSeed(pInstance.getRandomSeed());
      SimSystem.getRNGGenerator().setRNGFactory(
          new ParameterizedFactory<RandomGeneratorFactory>(new ParameterBlock(
              pInstance.getRNGFactoryName())));
      recordedProblemInstances.put(crti.getRunInformation().getExecutionIDs(),
          pInstance);
    }
  }

  /**
   * Looks up the simulation problem that was solved. If not stored in the
   * performance database, it will be created.
   * 
   * @param crti
   *          the simulation runtime information
   * @return the simulation problem instance corresponding to the problem at
   *         hand
   */
  @SuppressWarnings("unchecked")
  // Serialized parameter maps are required
  protected IProblemInstance registerProblemInstance(
      ComputationTaskRuntimeInformation crti) {

    IComputationTaskConfiguration compTaskConfig =
        crti.getSimulationRunConfiguration();
    URI uri =
        compTaskConfig.getAbsModelReaderFactoryParams().getSubBlockValue(
            IURIHandling.URI);

    IProblemScheme scheme = null;

    try {
      scheme = perfDatabase.getProblemScheme(uri);
    } catch (Exception ex) {
      SimSystem
          .report(Level.SEVERE, CONSTR_ERR_MSG_TITLE + ex.getMessage(), ex);
    }

    if (scheme == null) {
      scheme = setupBenchmarkModelProblemScheme(uri);
    }

    Map<String, Serializable> parameters =
        (Map<String, Serializable>) compTaskConfig.getParameters();
    if (parameters == null) {
      parameters = new HashMap<>();
    }

    IProblemDefinition problemDefinition = null;

    // Create new simulation problem or re-use existing one
    try {
      problemDefinition =
          perfDatabase.newProblemDefinition(scheme, SimulationProblemDefinition
              .getDefinitionParameters(
                  compTaskConfig.getStopPolicyFactoryClass(),
                  compTaskConfig.getStopPolicyParameters()), parameters);
    } catch (Exception ex) {
      SimSystem
          .report(Level.SEVERE, CONSTR_ERR_MSG_TITLE + ex.getMessage(), ex);
      return null;
    }

    // Generate/Re-use problem instance
    Pair<Long, String> rngSetup =
        instanceGenerator.requestNewInstance(problemDefinition);
    IProblemInstance pInst =
        perfDatabase.newProblemInstance(problemDefinition,
            rngSetup != null ? rngSetup.getFirstValue() : -1,
            rngSetup != null ? rngSetup.getSecondValue() : null);

    return pInst;
  }

  /**
   * Creates new benchmark model.
   * 
   * @param uri
   *          the URI
   * @return newly created benchmark model
   */
  protected IProblemScheme setupBenchmarkModelProblemScheme(URI uri) {
    // TODO add dialog to input BM model name and description
    IProblemScheme bm =
        perfDatabase.newProblemScheme(uri, "Model name", DatabaseUtils
            .convertModelTypeToSchemeType(BenchmarkModelType.APPLICATION), "-");
    return bm;
  }

  /**
   * Registers the new performance results for the given runtime configuration.
   * 
   * @param probInst
   *          problem instance associated with these performance results
   * @param RUNTIME_CONFIGURATION
   *          the runtime configuration
   * @param setup
   *          the hardware setup used when applying the runtime configuration to
   *          the problem instance
   * @param crti
   *          the runtime information
   */
  protected void registerPerformance(IProblemInstance probInst,
      IRuntimeConfiguration rtConfig, IHardwareSetup setup,
      ComputationTaskRuntimeInformation crti) {

    // Create new application
    // TODO register result provider (ask Registry for factory etc.)
    IApplication application =
        perfDatabase.newApplication(probInst, rtConfig, setup, null);

    // Select suitable performance measurers
    HashMap<Class<? extends PerformanceMeasurerFactory>, IPerformanceType> factoryMapping =
        new HashMap<>();
    for (IPerformanceType perfMeasure : perfDatabase.getAllPerformanceTypes()) {
      factoryMapping.put(perfMeasure.getPerformanceMeasurerFactory(),
          perfMeasure);
    }

    ParameterBlock perfMeasurerParams =
        (new ParameterBlock()).addSubBl(
            PerformanceMeasurerFactory.PERFORMANCE_DATA, rtConfig);

    List<PerformanceMeasurerFactory> suitableFactories =
        SimSystem.getRegistry().getFactoryOrEmptyList(
            AbstractPerformanceMeasurerFactory.class, perfMeasurerParams);

    // Measure performance, and store it to database
    Map<PerformanceMeasurerFactory, Double> performances =
        new HashMap<>();
    for (PerformanceMeasurerFactory factory : suitableFactories) {
      measurePerformance(crti, application, factoryMapping, perfMeasurerParams,
          performances, factory);
    }

    // Notify listeners
    for (IPerfRecorderListener listener : listeners) {
      listener.performanceRecorded(probInst, application, performances);
    }
  }

  /**
   * Applies a performance measure to the current application.
   * 
   * FIXME this is (almost) duplicate code from
   * {@link org.jamesii.perfdb.recording.PerfDBRecordingHelper}, merge both.
   * 
   * @param crti
   *          the crti
   * @param application
   *          the application
   * @param factoryMapping
   *          the factory mapping
   * @param perfMeasurerParams
   *          the perf measurer params
   * @param performances
   *          the performances
   * @param factory
   *          the factory
   */
  @SuppressWarnings("unchecked")
  // Conformance to IPerformanceMeasurer<T> should be guaranteed by the
  // factories, checking for the runtime configuration in the parameter block
  private void measurePerformance(
      ComputationTaskRuntimeInformation crti,
      IApplication application,
      Map<Class<? extends PerformanceMeasurerFactory>, IPerformanceType> factoryMapping,
      ParameterBlock perfMeasurerParams,
      Map<PerformanceMeasurerFactory, Double> performances,
      PerformanceMeasurerFactory factory) {

    String perfMeasurerHash =
        PerformanceMeasurerFactory.getPerformanceMeasurerHash(factory,
            perfMeasurerParams);
    IPerformanceMeasurer<ComputationTaskRuntimeInformation> perfMeasurer =
        (IPerformanceMeasurer<ComputationTaskRuntimeInformation>) performanceMeasurers
            .get(perfMeasurerHash);
    if (perfMeasurer == null) {
      perfMeasurer =
          (IPerformanceMeasurer<ComputationTaskRuntimeInformation>) factory
              .create(perfMeasurerParams);
      performanceMeasurers.put(perfMeasurerHash, perfMeasurer);
    }

    Double performance = perfMeasurer.measurePerformance(crti);

    if (performance.isInfinite() || performance.isNaN()) {
      performance = -Double.MAX_VALUE;
    }
    performances.put(factory, performance);
    perfDatabase.newPerformance(application,
        factoryMapping.get(factory.getClass()), performance);
  }

  @Override
  public void experimentExecutionStarted(BaseExperiment experiment) {
    // Every selection from now on will count
    selectionHook.reset();
    try {
      // Aquire lock to avoid concurrent execution of simulation
      // initialisation handler
      synchronized (this) {
        perfDatabase.open();
      }
    } catch (Exception ex) {
      SimSystem.report(
          Level.SEVERE,
          null,
          ERR_MSG_TITLE
              + "Error while opening performance database. Caused by:"
              + ex.getMessage(), null, ex);
    }
  }

  @Override
  public void experimentExecutionStopped(BaseExperiment experiment) {
    instanceGenerator.reset();
  }

  /**
   * Starts performance recorder.
   */
  public void start() {
    selectionHook.start();
  }

  /**
   * Stops performance recorder.
   */
  public synchronized void stop() {
    executionCounter = 0;
    selectionHook.stop();
  }

  /**
   * Gets the selection hook.
   * 
   * @return the selection hook
   */
  public SelectionHook getSelectionHook() {
    return selectionHook;
  }

  /**
   * Gets the instance generator.
   * 
   * @return the instanceGenerator
   */
  public ProblemInstanceGenerator getInstanceGenerator() {
    return instanceGenerator;
  }

  /**
   * Sets the instance generator.
   * 
   * @param instanceGenerator
   *          the instanceGenerator to set
   */
  public void setInstanceGenerator(ProblemInstanceGenerator instanceGenerator) {
    this.instanceGenerator = instanceGenerator;
  }

  /**
   * Gets the set of IDs of all runtime configuration IDs for which new versions
   * shall be created.
   * 
   * @return newVerRTConfigIDs
   */
  public Set<Long> getNewVerRTConfigIDs() {
    return newVerRTConfigIDs;
  }

  /**
   * Sets the set IDs of all runtime configuration for which new versions shall
   * be created.
   * 
   * @param newVerRTConfigIDs
   *          the newVerRTConfigIDs to set
   */
  public void setNewVerRTConfigIDs(Set<Long> newVerRTConfigIDs) {
    this.newVerRTConfigIDs = newVerRTConfigIDs;
  }

  /**
   * Adds the listener.
   * 
   * @param listener
   *          the listener
   */
  public void addListener(IPerfRecorderListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes the listener.
   * 
   * @param listener
   *          the listener
   */
  public void removeListener(IPerfRecorderListener listener) {
    listeners.remove(listener);
  }

  /**
   * Gets the flush interval.
   * 
   * @return the flushInterval
   */
  public int getFlushInterval() {
    return flushInterval;
  }

  /**
   * Sets the flush interval.
   * 
   * @param flushInterval
   *          the flushInterval to set
   */
  public void setFlushInterval(int flushInterval) {
    this.flushInterval = flushInterval;
  }

  /**
   * Creates the default recorder and registers it with the experiment etc.
   * 
   * @param experiment
   *          the experiment
   * @param performanceDatabase
   *          the performance database to be used
   * @return the perf db recorder
   */
  public static PerfDBRecorder createDefaultRecorder(BaseExperiment experiment,
      IPerformanceDatabase performanceDatabase) {
    PerfDBRecorder perfRecorder =
        performanceDatabase == null ? new PerfDBRecorder()
            : new PerfDBRecorder(performanceDatabase);
    experiment.getExecutionController().addExecutionListener(perfRecorder);
    // No stochastic effects: always use the same seed
    perfRecorder.getInstanceGenerator().setCurrentMode(
        ProblemInstanceSelectionMode.CONSTANT);
    perfRecorder.start();
    return perfRecorder;
  }

  /**
   * Creates the default recorder and registers it with the experimeent.
   * 
   * @param experiment
   *          the experiment
   * @return the perf db recorder
   */
  public static PerfDBRecorder createDefaultRecorder(BaseExperiment experiment) {
    return createDefaultRecorder(experiment, null);
  }

}