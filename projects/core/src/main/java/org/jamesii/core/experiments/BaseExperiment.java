/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.NamedEntity;
import org.jamesii.core.cmdparameters.Parameters;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;
import org.jamesii.core.experiments.replication.MaximizingReplicationCriterionFactory;
import org.jamesii.core.experiments.replication.RepNumberCriterionFactory;
import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.experiments.taskrunner.sequential.SequentialSimulationRunnerFactory;
import org.jamesii.core.experiments.tasks.ComputationTaskHook;
import org.jamesii.core.experiments.tasks.setup.IComputationTaskSetup;
import org.jamesii.core.experiments.tasks.setup.internalsimrun.SimulationRunFactory;
import org.jamesii.core.experiments.tasks.setup.plugintype.TaskSetupFactory;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.experiments.util.ExperimentBackup;
import org.jamesii.core.experiments.util.History;
import org.jamesii.core.experiments.variables.DynamicExperimentConfigurator;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.SubLevelStatus;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;
import org.jamesii.core.util.StopWatch;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.id.UniqueIDGenerator;
import org.jamesii.core.util.info.JavaInfo;
import org.jamesii.core.util.misc.ParameterUtils;

/**
 * Base class for experiments.
 * 
 * A computer based experiment with a model (aka simulation) is a set of
 * computations done with a model. Thereby the initial parameters (for the model
 * and the computation algorithms used) may vary from computation to
 * computation.
 * 
 * A base experiment contains the definition how to modify which parameters to
 * be used for the computation / model. Basically the
 * {@link #getExperimentVariables() experimentVariables} serves as a "for" loop.
 * Thereby the items per level are progressed synchronous, and they are constant
 * while the sub levels are modified according to the rules defined in there. <br/>
 * For a simple experiment you only need to pass a fixed set of parameters which
 * might even be empty, and an URI where the model can be found (
 * {@link #setModelLocation(URI)}). If you are interested in the trajectory of the
 * simulation you have to add an instrumenter in addition. Everything else is a
 * can, but not a must. You can specify a computation task start time (this
 * value will be used instead of 0 as initial value for the "simulation time")
 * and a factory to be used for determining the computation task end
 * {@link #setComputationTaskStopPolicyFactory(ComputationTaskStopPolicyFactory)}
 * . You can specify to use a distributed setup or a sequential one (default),
 * the number of replications ( {@link #setRepeatRuns(Integer)},
 * {@link #setReplicationCriterionFactory(ParameterizedFactory)}), and you can
 * specify the data sink to be used (
 * {@link #setDataStorageFactory(DataStorageFactory)}). Many of these can be/or
 * have to be parameterized, but this depends on the factory selected (e.g., you
 * typically need to give a user name etc. for a database used as data sink). <br/>
 * Check out the
 * {@link org.jamesii.core.experiments.variables.ExperimentVariables} class to
 * read more about how to define which variables. <br/>
 * Using this class you can setup
 * <ul>
 * <li>"normal" production runs, sometimes called parameter scans</li>
 * <li>optimization experiments</li>
 * <li>validation experiments</li>
 * <li>benchmark experiments</li>
 * </ul>
 * <br/>
 * A BaseExperiment does not create computation jobs directly. A base experiment
 * only creates {@link TaskConfiguration} objects which are then transferred to
 * the {@link org.jamesii.core.experiments.taskrunner.ITaskRunner} to be used to
 * execute the tasks. This allows to use different execution modes with the same
 * experiment definition (and thus to make the experiment definition scalable),
 * and it eases the parallel creation of job configurations, which is supported
 * as long as the "next" configurations do not depend on results of previous
 * runs (as this might be the case for some optimization algorithms, however of
 * some of those parallel variants exist as well). In addition event the
 * software to be used for computation can be exchanged. By default always the
 * {@link org.jamesii.core.experiments.tasks.setup.internalsimrun.SimulationRunFactory}
 * is used but this can be changed by using the
 * {@link #setSetupFactory(TaskSetupFactory)} method of instances of this class.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 */
public class BaseExperiment extends NamedEntity {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5960924789676396542L;

  /** The default number of replications. */
  public static final int DEFAULT_NUMBER_OF_REPLICATIONS = 1;

  // /**
  // * Parameter block identifier to be used in {@link #recreate()}.
  // */
  // public static final String RECREATE_PARAMS = "recreate";

  /**
   * If cancelOnError is set to true any error during execution will cancel the
   * complete experiment.
   */
  private boolean cancelOnError = false;

  /**
   * The history of the experiment.
   */
  private History history = new History();

  /**
   * Counter to initialise each computation configuration with a unique number.
   */
  private int taskCfgCounter = 0;

  /** Current experiment ID. */
  private IUniqueID currentExperimentID = UniqueIDGenerator.createUniqueID();

  /** Factory to create a data storage. */
  private ParameterizedFactory<DataStorageFactory> dataStorageFactory =
      new ParameterizedFactory<>();

  /** List of all computation task configurations that lead to an error. */
  private List<TaskConfiguration> errorHistory = new ArrayList<>();

  /** Currently registered computation task execution controller. */
  private transient IExperimentExecutionController executionController =
      new DefaultExecutionController();

  /** The tree of experiment variables. */
  private ExperimentVariables experimentVariables = null;

  /**
   * Map of fixed model parameters. Makes experiment configuration for rather
   * 'static' experiments easier.
   */
  private Map<String, Object> fixedModelParameters = new HashMap<>();

  /** The vector of jobs to be executed. */
  private List<TaskConfiguration> jobs = new Vector<>(); // NOSONAR:
                                                         // synchronization!

  // /** Support for console mode. */
  // private transient InteractiveConsole intCon;

  /** Flag that signals if the experiment is finished. */
  private boolean isFinished = false;

  /**
   * Counter for all jobs that have been sent to the computation task runner but
   * are not yet finished.
   */
  private volatile int jobsToBeDoneCounter = 0;

  /** Number of created jobs until {@link BaseExperiment} is backed up. */
  private volatile int jobsUntilBackup = 2;

  /** Maximal size of job list. */
  private int maxSizeJobList = 100;

  /** Counts the number of jobs since the last backup. */
  private volatile int jobsSinceLastBackup = 0;

  /**
   * Reference to the model instrumenter factory (to create the instrumenter).
   */
  private ParameterizedFactory<ModelInstrumenterFactory> modelInstrumenterFactory =
      new ParameterizedFactory<>();

  /** Location of the model which should be used throughout the experiment. */
  private URI modelLocation = null;

  /** Parameter block for model reader/writer. */
  private ParameterBlock modelRWParameters = null;

  /** Stop watch to measure overall experiment runtime. */
  private transient StopWatch overallRuntimeStopWatch = new StopWatch();

  /**
   * Reference to task parameters, factories for computation algorithm a.s.o..
   */
  private Parameters parameters = new Parameters();

  /** Data structure that stores information on computation execution times. */
  private Map<TaskConfiguration, List<RunInformation>> results =
      new HashMap<>();

  /** Reference to a computation instrumenter factory. */
  private ParameterizedFactory<ComputationInstrumenterFactory> computationInstrumenterFactory =
      new ParameterizedFactory<>();

  /** Reference to entity for running computations. */
  private transient ITaskRunner taskRunner = null;

  /** Factory to create task runner instances. */
  private ParameterizedFactory<TaskRunnerFactory> taskRunnerFactory =
      new ParameterizedFactory<TaskRunnerFactory>(
          new SequentialSimulationRunnerFactory(), new ParameterBlock());

  /**
   * The setup factory. By default the system used the internal factory to setup
   * simulations.
   */
  private TaskSetupFactory setupFactory = new SimulationRunFactory();

  /** The replication criterion factory and parameters. */
  private ParameterizedFactory<RepCriterionFactory> repCriterionFactory =
      new ParameterizedFactory<RepCriterionFactory>(
          new RepNumberCriterionFactory());

  /** Error hook for task runner. */
  private ComputationTaskHook<String> taskRunnerErrorHook = null;

  /** Flag that signals a stop. */
  private boolean stopping = false;

  /**
   * Number of calls to {@link #execute()} so far; also relevant for initial
   * {@link #currentExperimentID}
   */
  private int numberOfExecutions = 0;

  /** The default computation start time for this experiment. */
  private double compStartTime = 0;

  /** The default computation task stop policy for this experiment. */
  private ParameterizedFactory<ComputationTaskStopPolicyFactory> computationTaskStopFactory =
      new ParameterizedFactory<ComputationTaskStopPolicyFactory>(
          new SimTimeStopFactory());

  /** If true computation tasks will be started in paused mode. */
  private boolean startComputationTasksPaused = false;

  /**
   * The computation task inter step delay. Time (in ms) to wait before calling
   * nextStep again.
   */
  private long computationTaskInterStepDelay = 0;

  /**
   * Class to take care of backing up the experiment.
   */
  private ExperimentBackup experimentBackup;

  /**
   * Default constructor. See the variables outSystem, cancelOnError and
   * errorHook for their default values.
   */
  public BaseExperiment() {
    this(false, null, null, null);
  }

  /**
   * Full constructor.
   * 
   * @param cancelOnError
   *          Pass true if you want to stop the overall experiment as soon as
   *          one runs fails
   * @param errorHook
   *          Pass a hook if you want to get informed by E-Mail if an error
   *          occurs, null otherwise
   * @param dataStorFactory
   *          factory to create a data storage
   * @param dataStorParameters
   *          parameters for the data storage factory
   */
  public BaseExperiment(boolean cancelOnError,
      ComputationTaskHook<String> errorHook,
      DataStorageFactory dataStorFactory, ParameterBlock dataStorParameters) {
    setRepeatRuns(DEFAULT_NUMBER_OF_REPLICATIONS);
    this.taskRunnerErrorHook = errorHook;
    this.cancelOnError = cancelOnError;
    this.dataStorageFactory.setFactory(dataStorFactory);
    this.dataStorageFactory.setParameter(dataStorParameters);
    this.experimentBackup = new ExperimentBackup(this);
  }

  /**
   * Log the current experiment setup.
   */
  private void logSetup() {
    // if we have a data storage then we'll store on which system the experiment
    // has been started, depending on the computation task runner used this must
    // not be the machine the computations are performed on; further on we save
    // the current state of this instance (of the experiment as such)
    if (getDataStorageFactory() != null) {
      IDataStorage<?> ds = getDataStorageFactory().getInstance();
      if (ds != null) {
        ds.writeExperimentSystemInformation(currentExperimentID, -1,
            SimSystem.VERSION, new JavaInfo());
        ds.storeExperiment(this);
      }
    }

    SimSystem.report(Level.CONFIG, "The experiment with uid "
        + currentExperimentID + " has been started using " + SimSystem.VERSION
        + " on machine " + new JavaInfo());
  }

  /**
   * Execute the experiment by using the internally set parameters.
   */
  public void execute() {

    if (numberOfExecutions > 0) {
      // Generate 'unique' experiment ID (however, the first one is generated
      // with instantiation of the class, so skip this for the first call to
      // this method)
      currentExperimentID = UniqueIDGenerator.createUniqueID();
    }
    numberOfExecutions++;

    // Create and select computatation task runner
    this.taskRunner =
        taskRunnerFactory.getFactoryInstance().create(
            taskRunnerFactory.getParameter());
    this.taskRunner.setErrorHook(taskRunnerErrorHook);

    // Start execution controller thread
    executionController.setExperiment(this);
    Thread execCThread =
        new Thread(executionController, "execution-controller (running: "
            + executionController.getClass().getName() + ")");
    execCThread.start();

    // Start computation task runner thread
    Thread taskRunnerThread =
        new Thread(taskRunner, "task-runner (running: "
            + taskRunner.getClass().getName() + ")");
    taskRunnerThread.start();

    logSetup();

    try {
      // register at central instance (uid - instance)
      ExperimentManager.addExperiment(this);
      runExperiment();
    } finally {
      // unregister at central instance
      ExperimentManager.removeExperiment(this.getUniqueIdentifier());
      if (getDataStorageFactory() != null) {
        IDataStorage<?> ds = getDataStorageFactory().getInstance();
        if (ds != null) {
          ds.experimentDone(getUniqueIdentifier());
        }
      }
    }

    SimSystem.report(Level.INFO, "About to stop the experiment "
        + this.currentExperimentID);
    taskRunner.stop();
    executionController.stop(false);
    SimSystem.report(Level.INFO, "Stopped the experiment "
        + this.currentExperimentID);

  }

  /**
   * @return Number of times {@link #execute()} has been called (including the
   *         current execution of called from within {@link #execute()} or
   *         anything that is called while the experiment execution is running.)
   */
  public final int getNumberOfExecutions() {
    return numberOfExecutions;
  }

  /**
   * Called when the execution of a computation task configuration is completed.
   * 
   * @param taskConfig
   *          the computation task configuration that is finished
   * @param compTaskConfigResults
   *          the run information on the computation
   */
  public synchronized void executionFinished(TaskConfiguration taskConfig,
      RunInformation compTaskConfigResults) {

    if (results.get(taskConfig) == null) {
      history.add(taskConfig);
      results.put(taskConfig, new ArrayList<RunInformation>());
    }
    results.get(taskConfig).add(compTaskConfigResults);
    for (TaskConfiguration config : history.checkHistorySize()) {
      results.remove(config);
    }

    if (compTaskConfigResults.isJobDone()) {
      jobsToBeDoneCounter--;
    }

    changed(taskConfig);
    notifyExperimentVariables(taskConfig, compTaskConfigResults);

    // Check whether there is a next batch ready
    List<TaskConfiguration> newJobs = getJobs(false);
    jobsSinceLastBackup += newJobs.size();
    jobsToBeDoneCounter += newJobs.size();

    // synchronize on the task runner as the task runner may work on this list
    // in concurrency if ITaskRunner#scheduleConfigurations is executed in
    // parallel
    synchronized (this.taskRunner) {
      jobs.addAll(newJobs);
    }

    // If no more jobs are left (experiment finished) or new jobs have been
    // generated, wake up main loop in runExperiment()
    if (jobsToBeDoneCounter == 0 || newJobs.size() > 0) {
      this.notifyAll();
    }

  }

  /**
   * Notify experiment variables.
   * 
   * @param compTaskConfig
   *          the computation task configuration
   * @param runInfo
   *          information regarding the outcomes of the run
   */
  private void notifyExperimentVariables(TaskConfiguration compTaskConfig,
      RunInformation runInfo) {
    ExperimentVariables expVars = this.experimentVariables;
    while (expVars != null) {
      expVars.executionFinished(compTaskConfig, runInfo);
      expVars = expVars.getSubLevel();
    }
  }

  /**
   * Gets the error history.
   * 
   * @return the error history
   */
  public List<TaskConfiguration> getErrorHistory() {
    return errorHistory;
  }

  /**
   * Gets the experiment variables.
   * 
   * @return the experiment variables
   */
  public ExperimentVariables getExperimentVariables() {
    return experimentVariables;
  }

  /**
   * Gets the fixed model parameters.
   * 
   * @return the fixed model parameters
   */
  public Map<String, Object> getFixedModelParameters() {
    return fixedModelParameters;
  }

  /**
   * @return Processor (factory) parameters
   */
  public ParameterBlock getProcessorFactoryParameters() {
    return ParameterBlocks.getSubBlock(parameters.getParameterBlock(),
        ProcessorFactory.class.getName());
  }

  /**
   * Set the processor (factory) parameters (the factory itself not set but
   * determined based on the model).
   * 
   * This method wraps a call to and is supposed to use instead of
   * <code>.getParameters().getParameterBlock().addSubBlock\(ProcessorFactory.class.getName(),...</code>
   * . However, if parameters are also set that way, the later call overrides
   * what was set in the previous one.
   * 
   * @param processorFactoryParameters
   *          Processor (factory) parameters
   */
  public void setProcessorFactoryParameters(
      ParameterBlock processorFactoryParameters) {
    this.parameters.getParameterBlock().addSubBlock(
        ProcessorFactory.class.getName(), processorFactoryParameters);
  }

  /**
   * Set the processor (factory) parameter (the factory itself not set but
   * determined based on the model) if there is only one (and no block).
   * 
   * This method wraps a call to and is supposed to use instead of
   * <code>exp.setProcessorFactoryParameters(param);</code> . However, if
   * parameters are also set that way, the later call overrides what was set in
   * the previous one.
   * 
   * @param param
   *          Processor (factory) parameter
   */
  public void setProcessorFactoryParameters(Object param) {
    this.parameters.getParameterBlock().addSubBlock(
        ProcessorFactory.class.getName(), param);
  }

  /**
   * Get list of jobs.
   * 
   * @param firstSetup
   *          flag to mark whether this is the first setup (then, an empty list
   *          of variables is allowed)
   * 
   * @return list of jobs to be computed
   */
  protected List<TaskConfiguration> getJobs(boolean firstSetup) {
    List<TaskConfiguration> newJobs = new ArrayList<>();
    TaskConfiguration taskConfig = setupRun(firstSetup);
    if (taskConfig == null) {
      return newJobs;
    }
    while (taskConfig != null) {
      configureCompTaskConfig(taskConfig);
      newJobs.add(taskConfig);
      if (newJobs.size() == maxSizeJobList) {
        return newJobs;
      }
      taskConfig = setupRun(false);
    }
    return newJobs;
  }

  /**
   * Configures a computation task configuration with all settings of this
   * experiment.
   * 
   * @param taskConfig
   *          the task configuration to be configured
   */
  protected void configureCompTaskConfig(TaskConfiguration taskConfig) {
    if (taskConfig.getReplicationCriterionFactory() == null) {
      taskConfig.setReplicationCriterionFactory(repCriterionFactory);
    }
    taskConfig.setDataStorageFactory(dataStorageFactory);
    taskConfig.setStartPaused(isStartComputationTasksPaused());
    taskConfig.setInterStepDelay(getComputationTaskInterStepDelay());
    taskConfig.setSetup(getComputationTaskSetup());
  }

  /**
   * Gets the computation task setup.
   * 
   * @return the computation task setup to be used to instantiate the runs
   */
  protected IComputationTaskSetup getComputationTaskSetup() {
    // we could reuse a cached one here as well!
    return getSetupFactory().create(null);
  }

  /**
   * Returns the run informations for the last configuration.
   * 
   * @return run informations for the last configuration, if not existent: null
   */
  public List<RunInformation> getLastResults() {
    if (history.size() == 0) {
      return null;
    }
    return results.get(history.get(history.size() - 1));
  }

  /**
   * Returns the last run information for the last configuration.
   * 
   * @return last run information for the last configuration, if not existent:
   *         null
   */
  public RunInformation getLastRunInformation() {
    List<RunInformation> lastResult = getLastResults();
    if (lastResult == null || lastResult.size() == 0) {
      return null;
    }
    return lastResult.get(lastResult.size() - 1);
  }

  /**
   * Gets the max size job list.
   * 
   * @return the max size job list
   */
  public int getMaxSizeJobList() {
    return maxSizeJobList;
  }

  /**
   * Gets the model location.
   * 
   * @return the model location
   */
  public URI getModelLocation() {
    return modelLocation;
  }

  /**
   * Gets the overall runtime stop watch.
   * 
   * @return the overall runtime stop watch
   */
  public StopWatch getOverallRuntimeStopWatch() {
    return overallRuntimeStopWatch;
  }

  /**
   * @param overallRuntimeStopWatch
   */
  public void setOverallRuntimeStopWatch(StopWatch overallRuntimeStopWatch) {
    this.overallRuntimeStopWatch = overallRuntimeStopWatch;
  }

  /**
   * Gets the parameters.
   * 
   * @return the parameters
   */
  public Parameters getParameters() {
    return parameters;
  }

  /**
   * Returns data structure that manages computation task execution time
   * information.
   * 
   * @return results of computation task runs
   */
  public List<List<RunInformation>> getResults() {

    List<List<RunInformation>> resultList = new ArrayList<>();

    for (TaskConfiguration taskConfig : history.getConfigurations()) {
      resultList.add(results.get(taskConfig));
    }

    return resultList;
  }

  /**
   * Removes parts of the historic RunInformations.
   * 
   * @param key
   *          The task configuration for which the RunInformation are to be
   *          removed.
   */
  public void removeResultOfConfiguration(TaskConfiguration key) {
    results.remove(key);
  }

  /**
   * Returns the history of computation task configurations.
   * 
   * @return history of computation task configurations
   */
  public List<TaskConfiguration> getHistory() {

    return history.getConfigurations();
  }

  /**
   * Checks if is cancel on error.
   * 
   * @return true, if is cancel on error
   */
  public boolean isCancelOnError() {
    return cancelOnError;
  }

  /**
   * Checks if is stopping.
   * 
   * @return true, if is stopping
   */
  public boolean isStopping() {
    return stopping;
  }

  /**
   * Is called from the JAVA serialization API. Added {@link #recreate()} call.
   * 
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    recreate();
  }

  /**
   * Makes this experiment executable again after it has been serialized.
   */
  private void recreate() {
    setExperimentExecutionController(new DefaultExecutionController());
    setOverallRuntimeStopWatch(new StopWatch());
  }

  /**
   * Run the experiment. Thereby each run of the experiment is repeated by the
   * number passed in repeatEachRun (one after other).
   */
  protected void runExperiment() {

    results.clear();
    history.clear();
    errorHistory.clear();
    taskCfgCounter = 0;

    isFinished = false;
    stopping = false;
    overallRuntimeStopWatch.reset();
    overallRuntimeStopWatch.start();

    // Get the parameters for the next run
    if (experimentVariables != null) {
      experimentVariables.init(experimentVariables);
    }

    // Initialise the task runner
    taskRunner.setCancelOnError(cancelOnError);

    List<TaskConfiguration> newJobs = getJobs(true);
    jobsSinceLastBackup = newJobs.size();
    jobs.addAll(newJobs);
    jobsToBeDoneCounter = newJobs.size();

    while (!jobs.isEmpty()) {

      // Save backup of BaseExperiment
      if (experimentBackup.isEnabled()
          && jobsSinceLastBackup >= jobsUntilBackup) {
        experimentBackup.writeExperimentToFile(SimSystem.getTempDirectory()
            + "/backup.exp");
      }

      try {
        synchronized (this) {
          taskRunner.scheduleConfigurations(executionController, jobs);
          jobs.clear();
          if (!stopping && !isFinished) {
            wait();
          }
        }
      } catch (InterruptedException ex) {
        SimSystem.report(ex);
      }
      if (stopping) {
        break;
      }
    }

    isFinished = true;
    overallRuntimeStopWatch.stop();
    if (experimentVariables != null) {
      experimentVariables.reset(experimentVariables);
    }

    changed();

    SimSystem.report(Level.INFO,
        this.currentExperimentID + "\t" + "Seconds needed for all runs: "
            + overallRuntimeStopWatch.elapsedSeconds());
  }

  /**
   * Sets the cancel on error.
   * 
   * @param cancelOnError
   *          the new cancel on error
   */
  public void setCancelOnError(boolean cancelOnError) {
    this.cancelOnError = cancelOnError;
  }

  /**
   * Parse command line args. (replacing previously unused getter and setter of
   * field of poorly documented use)
   * 
   * @param commandLineArgs
   *          command line args
   * @author Arne Bittig
   */
  public void parseCommandLineArgs(String[] commandLineArgs) {
    parameters.parseArgs(commandLineArgs);
  }

  /**
   * Sets the data storage parameters.
   * 
   * @param dataStorageParameters
   *          the new data storage parameters
   */
  @Deprecated
  public void setDataStorageParameters(ParameterBlock dataStorageParameters) {
    this.dataStorageFactory.setParameter(dataStorageParameters);
  }

  /**
   * Gets the data storage factory.
   * 
   * @return the data storage factory
   */
  public ParameterizedFactory<DataStorageFactory> getDataStorageFactory() {
    return dataStorageFactory;
  }

  /**
   * Sets the data storage factory.
   * 
   * @param dataStorageFactory
   *          the new data storage factory
   */
  public void setDataStorageFactory(
      ParameterizedFactory<DataStorageFactory> dataStorageFactory) {
    this.dataStorageFactory = dataStorageFactory;
  }

  /**
   * Sets the data storage factory.
   * 
   * @param dataStorageFactory
   *          the new data storage factory
   */
  @Deprecated
  public void setDataStorageFactory(DataStorageFactory dataStorageFactory) {
    this.dataStorageFactory.setFactory(dataStorageFactory);
  }

  /**
   * Sets the experiment execution controller.
   * 
   * @param execController
   *          the new experiment execution controller
   */
  public void setExperimentExecutionController(
      IExperimentExecutionController execController) {
    executionController = execController;
  }

  /**
   * Sets the experiment variables.
   * 
   * @param experimentVariables
   *          the new experiment variables
   */
  public void setExperimentVariables(ExperimentVariables experimentVariables) {
    this.experimentVariables = experimentVariables;
  }

  /**
   * Sets the fixed model parameters.
   * 
   * @param fixedModelParameters
   *          the fixed model parameters
   */
  public void setFixedModelParameters(Map<String, Object> fixedModelParameters) {
    this.fixedModelParameters = fixedModelParameters;
  }

  /**
   * Sets the max size job list.
   * 
   * @param maxSizeJobList
   *          the new max size job list
   */
  public void setMaxSizeJobList(int maxSizeJobList) {
    this.maxSizeJobList = maxSizeJobList;
  }

  /**
   * Sets the max size of ther history list.
   * 
   * @param maxHistorySize
   *          the new max size of the history list
   */
  public void setMaxHistorySize(int maxHistorySize) {
    this.history.setMaxSize(maxHistorySize);
  }

  /**
   * Sets the model location.
   * 
   * @param modelLocation
   *          the new model location
   */
  public void setModelLocation(URI modelLocation) {
    this.modelLocation = modelLocation;
  }

  /**
   * Sets the parameters.
   * 
   * @param parameters
   *          the new parameters
   */
  public void setParameters(Parameters parameters) {
    this.parameters = parameters;
  }

  /**
   * Clears all other replication criteria and adds a criteria for the
   * replication of an exact number of runs. This function should not be used in
   * sophisticated scenarios, it's better to add replication criteria (@see
   * {@link org.jamesii.core.experiments.replication.IReplicationCriterion})
   * 
   * @param repeatRuns
   *          number of times each run shall be repeated
   */
  public final void setRepeatRuns(Integer repeatRuns) {
    if (repeatRuns <= 0) {
      throw new IllegalArgumentException(
          "Number of replications must be positive, but is: " + repeatRuns);
    }
    repCriterionFactory =
        new ParameterizedFactory<RepCriterionFactory>(
            new RepNumberCriterionFactory(), new ParameterBlock().addSubBl(
                RepNumberCriterionFactory.NUM_REPS, repeatRuns));
  }

  /**
   * Sets a default simulation stop time. This is a convenience method that
   * initialises a simple simulation stopping scheme with a constant end time.
   * Will dismiss any more sophisticated stopping option that was configured
   * previously.
   * 
   * @param stopTime
   *          the new default simulation stop time
   */
  public void setDefaultSimStopTime(double stopTime) {
    computationTaskStopFactory.setFactory(new SimTimeStopFactory());
    computationTaskStopFactory.setParameter(new ParameterBlock(stopTime,
        SimTimeStopFactory.SIMEND));
  }

  /**
   * Gets the default simulation stop time. This is a convenience method that
   * presumes the experiment is initialised with the {@link SimTimeStopFactory},
   * e.g., via {@link BaseExperiment#setDefaultSimStopTime(double)}. Otherwise
   * null will be returned.
   * 
   * @return the default simulation stop time
   */
  public Double getDefaultSimStopTime() {
    if ((computationTaskStopFactory.getFactoryInstance() instanceof SimTimeStopFactory)
        || (ParameterBlocks.getValue(computationTaskStopFactory.getParameter())
            .equals(SimTimeStopFactory.class.getName()))) {
      return ParameterBlocks.getSubBlockValue(
          computationTaskStopFactory.getParameter(), SimTimeStopFactory.SIMEND);
    }
    return null;
  }

  /**
   * Setup a run, gather the parameters to be used for the next computation task
   * configuration.
   * 
   * Thereby these parameters may be calculated by using an optimisation method
   * or by using iterators, etc.
   * 
   * Sets the experimentVariables class attribute to values to be used for the
   * next (for setting up the model and the computation)
   * 
   * @see ExperimentVariables
   * 
   * @param firstSetup
   *          true if this is the first setup (ignore empty experiment variables
   *          then)
   * 
   * @return computation task configuration to be executed, null if experiment
   *         is finished
   */
  protected TaskConfiguration setupRun(boolean firstSetup) {

    Map<String, Object> modelParameters = new HashMap<>(fixedModelParameters);

    SubLevelStatus expVarStatus = SubLevelStatus.DONE;
    if (experimentVariables != null) {
      expVarStatus = experimentVariables.next(experimentVariables);
    }

    if (!firstSetup
        && (experimentVariables == null || expVarStatus != SubLevelStatus.HAS_NEXT)) {
      return null;
    }

    ParameterBlock execConfigParamBlock =
        (new Parameters(parameters)).getParameterBlock();
    execConfigParamBlock.addSubBl(ParameterUtils.SIM_START_TIME, compStartTime);

    DynamicExperimentConfigurator configurator =
        new DynamicExperimentConfigurator(execConfigParamBlock, modelParameters);
    configurator.configureWith(experimentVariables);

    // create the parameter block for the computation task configuration
    TaskConfiguration taskConfig =
        initComputationTaskConfiguration(modelParameters, execConfigParamBlock);
    configureComputationStopping(configurator, taskConfig);
    configureInstrumentationAndReplication(configurator, taskConfig);

    return taskConfig;
  }

  /**
   * @param simStartTime
   */
  public void setSimStartTime(double simStartTime) {
    this.compStartTime = simStartTime;
  }

  /**
   * Configure computation task stop policy.
   * 
   * @param configurator
   *          the configurator
   * @param taskConfig
   *          the computation task configuration
   */
  private void configureComputationStopping(
      DynamicExperimentConfigurator configurator, TaskConfiguration taskConfig) {
    if (configurator.getStopFactory() == null) {
      taskConfig.setComputationTaskStopFactory(computationTaskStopFactory);
    } else {
      taskConfig.setComputationTaskStopFactory(new ParameterizedFactory<>(
          configurator.getStopFactory(), configurator.getStopParameters()));
    }
  }

  /**
   * Initialises the computation task configuration.
   * 
   * @param modelParameters
   *          the model parameters
   * @param execConfigParamBlock
   *          the execution configuration parameter block
   * 
   * @return the task configuration
   */
  private TaskConfiguration initComputationTaskConfiguration(
      Map<String, Object> modelParameters, ParameterBlock execConfigParamBlock) {
    ParameterBlock scpb =
        (new ParameterBlock()).addSubBl(AbstractModelReaderFactory.URI,
            new ParameterBlock(
            modelLocation));
    TaskConfiguration taskConfig =
        new TaskConfiguration(++taskCfgCounter, scpb, modelParameters,
            execConfigParamBlock);
    taskConfig.setExperimentID(currentExperimentID);
    taskConfig.setCustomRWParams(modelRWParameters);
    return taskConfig;
  }

  /**
   * Configure instrumentation and replication.
   * 
   * @param configurator
   *          the configurator
   * @param taskConfig
   *          the computation task configuration
   */
  private void configureInstrumentationAndReplication(
      DynamicExperimentConfigurator configurator, TaskConfiguration taskConfig) {

    // Add model instrumenters
    for (Map.Entry<ModelInstrumenterFactory, ParameterBlock> entry : configurator
        .getModelInstrumenterFactories().entrySet()) {
      taskConfig.addModelInstrumenterFactory(entry.getKey(), entry.getValue());
    }
    // tryModelInstrumenterCreation();
    if (modelInstrumenterFactory != null
        && modelInstrumenterFactory.isInitialized()) {
      taskConfig.addModelInstrumenterFactory(
          modelInstrumenterFactory.getFactoryInstance(),
          modelInstrumenterFactory.getParameter());
    }

    // Add computation instrumenters
    for (Map.Entry<ComputationInstrumenterFactory, ParameterBlock> entry : configurator
        .getComputationInstrumenterFactories().entrySet()) {
      taskConfig.addComputationInstrumenterFactory(entry.getKey(),
          entry.getValue());
    }
    // trySimulationInstrumenterCreation();
    if (computationInstrumenterFactory != null
        && computationInstrumenterFactory.isInitialized()) {
      taskConfig.addComputationInstrumenterFactory(
          computationInstrumenterFactory.getFactoryInstance(),
          computationInstrumenterFactory.getParameter());
    }

    configureReplication(configurator, taskConfig);
  }

  /**
   * Add newly generated replication criterion factories by joining them to a
   * maximizing replication criterion if necessary.
   * 
   * @param configurator
   *          the configurator
   * @param taskConfig
   *          the task configuration
   * @see {@link org.jamesii.core.experiments.replication.MaximizingReplicationCriterionFactory}
   */
  private void configureReplication(DynamicExperimentConfigurator configurator,
      TaskConfiguration taskConfig) {
    if (configurator.getReplicationCriterionFactories().isEmpty()) {
      return;
    }

    List<ParameterizedFactory<RepCriterionFactory>> repCritFactories =
        new ArrayList<>();
    repCritFactories.add(repCriterionFactory);
    repCritFactories.addAll(configurator.getReplicationCriterionFactories());
    taskConfig
        .setReplicationCriterionFactory(new ParameterizedFactory<RepCriterionFactory>(
            new MaximizingReplicationCriterionFactory(),
            new ParameterBlock()
                .addSubBl(
                    MaximizingReplicationCriterionFactory.REPLICATION_CRITERION_FACTORIES,
                    repCritFactories)));
  }

  /**
   * Lazy initialisation. Variables in the list are added to the current
   * experiment, one per level.
   * 
   * @param variables
   *          variables to be set up
   */
  public void setupVariables(List<ExperimentVariable<?>> variables) {
    this.experimentVariables =
        ExperimentVariables.generateExperimentVariables(variables);
  }

  /**
   * Sets the up variables just as {@link BaseExperiment#setupVariables(List)} .
   * 
   * @param variables
   *          the experiment variables for a full factorial
   */
  public void setupVariables(ExperimentVariable<?>... variables) {
    List<ExperimentVariable<?>> expVars = new ArrayList<>();
    for (ExperimentVariable<?> variable : variables) {
      expVars.add(variable);
    }
    this.setupVariables(expVars);
  }

  /**
   * Lazy initialisation. List of list of variables reflects the experiment
   * structure that will be set up.
   * 
   * @param variables
   *          variables to be set up
   */
  public void setupVariablesStructure(
      List<List<ExperimentVariable<?>>> variables) {
    this.experimentVariables =
        ExperimentVariables.generateNestedExperimentVariables(variables);
  }

  /**
   * If this method is called the experiment will be cancelled as soon as
   * possible.
   * 
   * @param stopRunningComputations
   *          flag to determine if currently running computations shall be
   *          stopped as well
   */
  public void stop(boolean stopRunningComputations) {

    if (stopping || isFinished) {
      return;
    }

    synchronized (this) {
      stopping = true;
      this.notifyAll();
    }

    if (taskRunner != null && taskRunner.cancelAllJobs(executionController)) {
      executionController.stop(stopRunningComputations);
    }
  }

  /**
   * Checks if is finished.
   * 
   * @return true, if is finished
   */
  public boolean isFinished() {
    return isFinished;
  }

  /**
   * Sets the finished.
   * 
   * @param isFinished
   *          the new finished
   */
  public void setFinished(boolean isFinished) {
    this.isFinished = isFinished;
  }

  /**
   * Gets the replication criterion factory.
   * 
   * @return the replication criterion factory
   */
  public ParameterizedFactory<RepCriterionFactory> getReplicationCriterionFactory() {
    return repCriterionFactory;
  }

  /**
   * Sets the replication criterion factory.
   * 
   * @param repCriterion
   *          the new replication criterion factory
   */
  public void setReplicationCriterionFactory(
      ParameterizedFactory<RepCriterionFactory> repCriterion) {
    repCriterionFactory = repCriterion;
  }

  /**
   * Sets the replication criterion factory.
   * 
   * <br/>
   * Please use the
   * {@link #setReplicationCriterionFactory(ParameterizedFactory)} method
   * instead.
   * 
   * @param factory
   *          the factory to be set
   */
  @Deprecated
  public void setReplicationCriterionFactory(RepCriterionFactory factory) {
    this.repCriterionFactory.setFactory(factory);
  }

  /**
   * Sets the replication criterion parameters.
   * 
   * <br/>
   * Please use the
   * {@link #setReplicationCriterionFactory(ParameterizedFactory)} method
   * instead.
   * 
   * @param parameters
   *          the new replication criterion parameters
   */
  @Deprecated
  public void setReplicationCriterionParameters(ParameterBlock parameters) {
    this.repCriterionFactory.setParameter(parameters);
  }

  /**
   * Gets the model reader/writer parameters.
   * 
   * @return the model reader/writer parameters
   */
  public ParameterBlock getModelRWParameters() {
    return modelRWParameters;
  }

  /**
   * Sets the model reader/writer parameters.
   * 
   * @param modelRWParameters
   *          the new model reader/writer parameters
   */
  public void setModelRWParameters(ParameterBlock modelRWParameters) {
    this.modelRWParameters = modelRWParameters;
  }

  /**
   * Gets the task runner factory.
   * 
   * @return the task runner factory
   */
  public ParameterizedFactory<TaskRunnerFactory> getTaskRunnerFactory() {
    return taskRunnerFactory;
  }

  /**
   * Sets the task runner factory and the parameters to be used to create the
   * instance of the task runner. By using this method you can preselect a
   * concrete task runner. Please note that you usually do not have to modify
   * anything else in your experiment description if you select a different
   * runner.<br/>
   * A task runner is responsible to schedule the computation of the tasks -
   * this can, for example, be done in a sequential or parallel manner.
   * Sometimes it might be necessary to additionally adopt the data sink to be
   * used -- because there might be datasinks which cannot be used in
   * distributed setups.
   * 
   * @param taskRunnerFactory
   *          the new task runner factory and its parameters
   */
  public void setTaskRunnerFactory(
      ParameterizedFactory<TaskRunnerFactory> taskRunnerFactory) {
    this.taskRunnerFactory = taskRunnerFactory;
  }

  /**
   * Sets the task runner factory. By using this method you can preselect a
   * concrete task runner. Please note that you usually do not have to modify
   * anything else in your experiment description if you select a different
   * runner.<br/>
   * A task runner is responsible to schedule the computation of the tasks -
   * this can, for example, be done in a sequential or parallel manner.
   * Sometimes it might be necessary to additionally adopt the data sink to be
   * used -- because there might be datasinks which cannot be used in
   * distributed setups.
   * 
   * <br/>
   * Please use the {@link #setTaskRunnerFactory(ParameterizedFactory)} method
   * instead.
   * 
   * @param taskRunnerFactory
   *          the new task runner factory
   */
  @Deprecated
  public void setTaskRunnerFactory(TaskRunnerFactory taskRunnerFactory) {
    this.taskRunnerFactory.setFactory(taskRunnerFactory);
  }

  /**
   * Sets the task runner parameters. Depending on the task there might not be
   * the need to set such a block, if there are parameters there is usually a
   * default fall back setting of the parameters (if possible). A parameter
   * which might be in use by several runners is the "masterserver" property,
   * the reference to the management server for distributing computation runs.
   * 
   * @param taskRunnerParameters
   *          the new computation task runner parameters
   */
  @Deprecated
  public void setTaskRunnerParameters(ParameterBlock taskRunnerParameters) {
    taskRunnerFactory.setParameter(taskRunnerParameters);
  }

  /**
   * Gets the computation task stop policy factory. This is the default used for
   * creating the stop policy for the computation of a computation task. This
   * value might not be used for setting up the stop policy of a computation
   * task run: it might be the case that there is an experiment variable which
   * defines "an own" end time factory.
   * 
   * @return the (default) computation stop factory
   */
  public ParameterizedFactory<ComputationTaskStopPolicyFactory> getComputationTaskStopFactory() {
    return computationTaskStopFactory;
  }

  /**
   * Set the task stop factory to be used. This value might be overwritten by a
   * variable modifier. The stop factory is used to decide whether the
   * computation of a task is done or not. A simple stop policy would be to stop
   * the computation if a certain time has reached.
   * 
   * @param computationTaskStopFactory
   */
  public void setComputationTaskStopPolicyFactory(
      ParameterizedFactory<ComputationTaskStopPolicyFactory> computationTaskStopFactory) {
    this.computationTaskStopFactory = computationTaskStopFactory;
  }

  /**
   * Sets the (default) computation task stop factory. This value might be
   * overwritten by a variable modifier. The stop factory is used to decide
   * whether the computation of a task is done or not. A simple stop policy
   * would be to stop the computation if a certain time has reached.
   * 
   * <br/>
   * Please use the
   * {@link #setComputationTaskStopPolicyFactory(ParameterizedFactory)} method
   * instead.
   * 
   * @param computationTaskStopFactory
   *          the new default computation task stop time factory
   */
  @Deprecated
  public void setComputationTaskStopPolicyFactory(
      ComputationTaskStopPolicyFactory computationTaskStopFactory) {
    this.computationTaskStopFactory.setFactory(computationTaskStopFactory);
  }

  /**
   * Sets the default computation task stop factory parameters. The parameters
   * valid may depend on the factory {@link #getComputationTaskStopFactory()
   * computationTaskStopFactory} used. These parameters will be passed over to
   * this factory upon creation of the policy for each computation task.
   * 
   * <br/>
   * Please use the
   * {@link #setComputationTaskStopPolicyFactory(ParameterizedFactory)} method
   * instead.
   * 
   * @param parameters
   *          the new default computation task stop factory parameters
   */
  @Deprecated
  public void setComputationTaskStopPolicyParameters(ParameterBlock parameters) {
    this.computationTaskStopFactory.setParameter(parameters);
  }

  /**
   * Sets the starting computation tasks to paused if true is passed. This means
   * that all computation tasks which are started based on job descriptions
   * created by this experiment definition will be started in "paused" mode -
   * thus a user has to explicitly start the execution on its own. <br>
   * This can be of use if a user wants to be able to observe each single
   * computation (maybe even stepwise) from the very first step on.
   * 
   * @param startComputationTaskPaused
   *          if true new computation tasks will start "paused"
   */
  public void setStartComputationTasksPaused(boolean startComputationTaskPaused) {
    this.startComputationTasksPaused = startComputationTaskPaused;
  }

  /**
   * Checks if new computation tasks will be started in "paused" mode.
   * 
   * @return true, if new computation tasks will be started in paused mode
   */
  public boolean isStartComputationTasksPaused() {
    return startComputationTasksPaused;
  }

  /**
   * Sets the inter step delay for the computation of computation tasks. The
   * passed milliseconds value will be used by the
   * {@link org.jamesii.core.processor.execontrol.ExecutionControl} instance for
   * pausing between two subsequent calls of the
   * {@link org.jamesii.core.processor.IProcessor#executeNextStep} method. This
   * can be used to allow an online observation of the computation in case of
   * too fast computations. For an as fast as possible computation this should
   * be set to 0, which is the default.
   * 
   * @param compuationTaskInterStepDelay
   *          the new computation task inter step delay
   */
  public void setComputationTaskInterStepDelay(long compuationTaskInterStepDelay) {
    this.computationTaskInterStepDelay = compuationTaskInterStepDelay;
  }

  /**
   * Gets the computation tasks inter step delay. A value of 0 means as fast as
   * possible execution, any value > 0 means an artificial slow down of the
   * computation - this can be used to make the computation of a task observable
   * to a human being.
   * 
   * @return the computation task inter step delay
   */
  public long getComputationTaskInterStepDelay() {
    return computationTaskInterStepDelay;
  }

  /**
   * Gets the execution controller. Can be used to retrieve it and attach custom
   * {@link IExperimentExecutionListener} instances to it.
   * 
   * @return the executionController
   */
  public IExperimentExecutionController getExecutionController() {
    return executionController;
  }

  /**
   * Gets the computation instrumenter factory.
   * 
   * @return the computation instrumenter factory
   */
  public ParameterizedFactory<ComputationInstrumenterFactory> getComputationInstrumenterFactory() {
    return computationInstrumenterFactory;
  }

  /**
   * Sets the computation instrumenter factory. This factory is used where the
   * computation is instantiated to get an instrumenter to instrument the
   * computation after is has been created. Thus this can be used to instrument
   * the computation algorithm used.
   * 
   * @param computationInstrumenterFactory
   *          the new computation instrumenter factory
   */
  public void setComputationInstrumenterFactory(
      ParameterizedFactory<ComputationInstrumenterFactory> computationInstrumenterFactory) {
    this.computationInstrumenterFactory = computationInstrumenterFactory;
  }

  /**
   * Sets the computation instrumenter factory. This factory is used where the
   * computation is instantiated to get an instrumenter to instrument the
   * computation after is has been created. Thus this can be used to instrument
   * the computation algorithm used.
   * 
   * @param compInstrumenterFactory
   *          the new computation instrumenter factory
   */
  @Deprecated
  public void setComputationInstrumenterFactory(
      ComputationInstrumenterFactory compInstrumenterFactory) {
    this.computationInstrumenterFactory.setFactory(compInstrumenterFactory);
  }

  /**
   * Sets the computation instrumenter parameters.
   * 
   * @param computationInstrumenterParameters
   *          the simulationInstrumenterParameters to set
   */
  @Deprecated
  public void setComputationInstrumenterParameters(
      ParameterBlock computationInstrumenterParameters) {
    this.computationInstrumenterFactory
        .setParameter(computationInstrumenterParameters);
  }

  /**
   * Gets the model instrumenter factory.
   * 
   * @return Model instrumenter factory
   */
  public ParameterizedFactory<ModelInstrumenterFactory> getModelInstrumenterFactory() {
    return modelInstrumenterFactory;
  }

  /**
   * Sets the model instrumenter factory.
   * 
   * @param modelInstrumenterFactory
   *          the (parameterized) new model instrumenter factory
   */
  public void setModelInstrumenterFactory(
      ParameterizedFactory<ModelInstrumenterFactory> modelInstrumenterFactory) {
    this.modelInstrumenterFactory = modelInstrumenterFactory;
  }

  /**
   * Sets the model instrumenter factory.
   * 
   * @param modInstrFactory
   *          the new model instrumenter factory
   */
  @Deprecated
  public void setModelInstrumenterFactory(
      ModelInstrumenterFactory modInstrFactory) {
    this.modelInstrumenterFactory.setFactory(modInstrFactory);
  }

  /**
   * Sets the model instrumenter parameters.
   * 
   * @param modelInstrumenterParameters
   *          the modelInstrumenterParameters to set
   */
  @Deprecated
  public void setModelInstrumenterParameters(
      ParameterBlock modelInstrumenterParameters) {
    this.modelInstrumenterFactory.setParameter(modelInstrumenterParameters);
  }

  /**
   * Gets the backup enabled - flag.
   * 
   * @return whether experiment backups (as XML) are enabled
   */
  public boolean isBackupEnabled() {
    return experimentBackup.isEnabled();
  }

  /**
   * Sets the backup enabled - flag. This is a flag to determine whether
   * experiment backups (as XML) are enabled.
   * 
   * @param experimentBackupEnabled
   *          the new backup enabled
   */
  public void setBackupEnabled(boolean experimentBackupEnabled) {
    experimentBackup.setEnabled(experimentBackupEnabled);
  }

  /**
   * Get the identifier of the experiment currently executed. The returned value
   * changes from one call of {@link #execute()} to the next! (Note that calling
   * {@link #execute()} several times on the same {@link BaseExperiment}
   * instance is not common practice.) Before the first call to
   * {@link #execute()}, the same value as during the first call is returned.
   * 
   * @return the unique identifier
   */
  public IUniqueID getUniqueIdentifier() {
    return currentExperimentID;
  }

  /**
   * Sets an alternative setup factory. Default is the internal factory for
   * computation tasks.
   * 
   * @param setupFactory
   *          the new factory to be used instead of the internal one
   */
  public void setSetupFactory(TaskSetupFactory setupFactory) {
    this.setupFactory = setupFactory;
  }

  /**
   * Get the setup factory to be used.
   * 
   * @return the setupfactory currently selected.
   */
  public TaskSetupFactory getSetupFactory() {
    return setupFactory;
  }

  @Override
  public String getCompleteInfoString() {
    String lineEnd = "\n";
    StringBuffer sb = new StringBuffer();
    sb.append("Experiment (" + getName() + ")" + lineEnd);
    sb.append("  Unique identifier: ");
    sb.append(this.getUniqueIdentifier());
    sb.append(lineEnd);
    sb.append(this.getModelLocation());
    sb.append(lineEnd);
    sb.append("  Model parameters: ");
    sb.append(this.getFixedModelParameters());
    sb.append(lineEnd);
    sb.append("Data storage: ");
    sb.append(this.getDataStorageFactory());
    sb.append(lineEnd);
    sb.append("Experiment variables: ");
    sb.append(this.getExperimentVariables());
    sb.append(lineEnd);
    sb.append("Replication criterion factory: ");
    sb.append(this.getReplicationCriterionFactory());
    sb.append(lineEnd);
    sb.append("Computation task stop factory: ");
    sb.append(this.getComputationTaskStopFactory());
    sb.append(lineEnd);
    sb.append("Parameters: ");
    sb.append(this.getParameters().getInfoString());
    sb.append(lineEnd);
    return sb.toString();
  }

}
