/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.NamedEntity;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.distributed.partitioner.Partitioner;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskHook;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.AbstractComputationTaskStopPolicyFactory;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.ModelInformation;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.processor.InvalidProcessorException;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.simulation.distributed.NeighbourInformation;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStop;
import org.jamesii.core.util.StopWatch;
import org.jamesii.core.util.misc.Strings;

/**
 * A SimulationRun contains the model to be simulated as well as the
 * corresponding processor. A SimulationRun is responsible for creating the
 * processor. Thereby, if the model is hierarchical and has maybe to be
 * simulated by using more than one or processors or has even to be distributed
 * this creation has to be delegated to a model specific processor creation
 * algorithm which takes the characteristics of the available computers, of the
 * modeling formalism as well as additional user settings into account.
 * 
 * @author Jan Himmelspach
 */
public class SimulationRun extends NamedEntity implements ISimulationRun {
  /** The serialization ID. */
  static final long serialVersionUID = -8719111742927312577L;

  /** Identifier of parameter sub-block for the partitioning framework. */
  public static final String PARTITIONER_SUBBLOCK = "partitioner";

  /**
   * The ID object.
   */
  private final ComputationTaskIDObject ID;

  /** The simulation model's model. */
  private ModelInformation model = new ModelInformation();

  /**
   * The information about the neighbours of models and processors in this
   * simulation run
   */
  private NeighbourInformation neighbourInformation = new NeighbourInformation(
      new HashMap<String, ModelInformation>(),
      new HashMap<String, ProcessorInformation>());

  /** The configuration of this simulation run. */
  private final SimulationRunConfiguration config;

  /** The partition. */
  private Partition partition = null;

  /**
   * The processor used by the simulation to simulate the model. This processor
   * can implement the runnable interface (it can be executable). In addition
   * the processor class may depend on the model class. So which processor to be
   * used has to be selected carefully (either by hand or in an automatic way)
   */
  private ProcessorInformation processorInfo;

  /** The start hook. */
  private ComputationTaskHook<ProcessorInformation> startHook = null;

  /** The hook for a simulation end. */
  private ComputationTaskHook<ProcessorInformation> endHook = null;

  /** The wall clock start time in milliseconds. */
  private long wcStartTime;

  /** The simulation start time. */
  private double startTime = 0;

  /**
   * The simulation stop condition. A simulation run might run to "infinity", to
   * any fixed sim time value, to any fixed wallclock time value, or until any
   * arbitrary condition is met (e.g., based on the simulation trajectory, ...).
   */
  private transient IComputationTaskStopPolicy<ISimulationRun> simRunStopPolicy =
      new SimTimeStop<>(Double.POSITIVE_INFINITY);

  /** The stop watch. */
  private transient StopWatch stopWatch = new StopWatch();

  /**
   * Instantiates a new simulation run. Special constructor which should only be
   * called by SimulationHost instances.
   * 
   * @param part
   *          the part
   * @param name
   *          the name
   * @param id
   *          the ID object
   * @param neighbourInformation
   *          the neighbourinformation
   * @param simRunConfig
   *          the sim config
   */
  public SimulationRun(Partition part, String name, ComputationTaskIDObject id,
      NeighbourInformation neighbourInformation,
      SimulationRunConfiguration simRunConfig) {
    super(name);
    config = simRunConfig;
    initFromConfig();
    ID = id;

    setModel(model.getLocal());

    /*
     * this.neighbourModelInformation.putAll(neighbourModelInformation);
     * this.neighbourProcessorInformation.putAll(neighbourProcessorInformation);
     */

    /* processor = */

    // ##################Added by nf028 to accept Extended neighbourInfromation
    this.neighbourInformation = neighbourInformation;
    // ###################################

    SimulationRun simu =
        part.createProcessor(
            this,
            simRunConfig.getExecParams().getSubBlock(
                ProcessorFactory.class.getName()));

    this.neighbourInformation.getModelInfos().putAll(
        simu.getNeighbourModelInformation());
    this.neighbourInformation.getProcessorInfos().putAll(
        simu.getNeighbourProcessorInformation());
  }

  /**
   * Gets the identification.
   * 
   * @return the identification
   */
  private String getIdentification() {
    return config.getExperimentID() + "." + config.getComputationTaskID()
        + "\t";
  }

  /**
   * Instantiates a new simulation run.
   * 
   * @param name
   *          the name
   * @param model
   *          the model
   * @param simRunConfig
   *          the simulation run configuration
   * @param resources
   *          the resources
   */
  public SimulationRun(String name, IModel model,
      SimulationRunConfiguration simRunConfig, List<ISimulationServer> resources) {
    setModel(model);
    setName(name);
    config = simRunConfig;
    ID = simRunConfig.getComputationTaskID();
    initFromConfig();

    // Partition the model
    Partitioner partitioner = new Partitioner();
    partition =
        partitioner.partitionize(model, resources, ParameterBlocks
            .getSBOrEmpty(config.getExecParams(), PARTITIONER_SUBBLOCK));

    // Get the parameters regarding processor configuration
    ParameterBlock apfp =
        ParameterBlocks.getSBOrEmpty(config.getExecParams(),
            ProcessorFactory.class.getName());
    apfp.addSubBlock(AbstractProcessorFactory.PARTITION, partition);
    ProcessorFactory pf =
        SimSystem.getRegistry()
            .getFactory(AbstractProcessorFactory.class, apfp);
    SimSystem.report(Level.CONFIG, getIdentification()
        + "We are going to use the " + pf + " processor factory");

    // set the factory
    partition.setProcessorFactoryRecursively(pf);

    // Each element of parts should be run by an own simulation, i.e.
    // only one part (the top most) will be simulated here.
    SimulationRun simu = partition.createProcessor(this, apfp);

    neighbourInformation.getModelInfos().putAll(
        simu.getNeighbourModelInformation());
    neighbourInformation.getProcessorInfos().putAll(
        simu.getNeighbourProcessorInformation());
    setProcessorInfo(simu.getProcessorInfo());
  }

  /**
   * Initialization the from the given {@link SimulationRunConfiguration}.
   */
  private void initFromConfig() {
    startTime = config.getSimStartTime();

    ParameterBlock pb = config.getStopPolicyParameters();
    if (pb == null) {
      pb = new ParameterBlock();
    }
    pb.addSubBlock(ComputationTaskStopPolicyFactory.COMPTASK, this);

    // It is necessary to consult the registry here (so that it, e.g., can
    // document which stop policy was chosen)
    pb.setValue(config.getStopPolicyFactoryClass().getCanonicalName());
    ComputationTaskStopPolicyFactory stopPolicyFactory =
        SimSystem.getRegistry().getFactory(
            AbstractComputationTaskStopPolicyFactory.class, pb);

    // In case the selection of the intended factory fails, choose the one that
    // was configured originally
    if (!stopPolicyFactory.getClass()
        .equals(config.getStopPolicyFactoryClass())) {
      try {
        stopPolicyFactory = config.getStopPolicyFactoryClass().newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        SimSystem.report(Level.SEVERE,
            "Could not initialize stop policy factory.", e);
      }
    }

    simRunStopPolicy = stopPolicyFactory.create(pb);

  }

  /**
   * Enables exact thread monitoring if MONITORING_ENABLED is true.
   */
  private void enableMonitoring() {
    if (config.isMonitoringEnabled()) {
      SimSystem.report(Level.INFO, "Can monitor thread's CPU time: "
          + ManagementFactory.getThreadMXBean().isThreadCpuTimeSupported());
      SimSystem.report(Level.INFO, "Can monitor thread's contention: "
          + ManagementFactory.getThreadMXBean()
              .isThreadContentionMonitoringSupported());
      SimSystem.report(Level.INFO, "Can monitor currents thread CPU time: "
          + ManagementFactory.getThreadMXBean()
              .isCurrentThreadCpuTimeSupported());
      ManagementFactory.getThreadMXBean().setThreadCpuTimeEnabled(true);
    }
  }

  @Override
  public String getCompleteInfoString() {
    return "Simulation\nModel: " + getModel().getName()
        + "\nList of parameter settings:\n"
        + Strings.indent(config.getExecParams().toString(), " ");
  }

  @Override
  public IModel getModel() {
    return model.getLocal();
  }

  /**
   * Gets the model info.
   * 
   * @return the model info
   */
  public ModelInformation getModelInfo() {
    return model;
  }

  /**
   * Gets the neighbour model information.
   * 
   * @return the neighbour model information
   */
  public Map<String, ModelInformation> getNeighbourModelInformation() {
    return neighbourInformation.getModelInfos();
  }

  /**
   * Gets the neighbour processor information.
   * 
   * @return the neighbour processor information
   */
  public Map<String, ProcessorInformation> getNeighbourProcessorInformation() {
    return neighbourInformation.getProcessorInfos();
  }

  @Override
  public Partition getPartition() {
    return partition;
  }

  /**
   * Gets the processor factory.
   * 
   * @return the processor factory
   */
  public ProcessorFactory getProcessorFactory() {
    return partition.getProcessorFactory();
  }

  @Override
  public ProcessorInformation getProcessorInfo() {
    return processorInfo;
  }

  /**
   * Gets the start hook.
   * 
   * @return the start hook
   */
  public ComputationTaskHook<?> getStartHook() {
    return startHook;
  }

  /**
   * Gets the end hook.
   * 
   * @return the end hook
   */
  public ComputationTaskHook<?> getEndHook() {
    return endHook;
  }

  /**
   * Returns the start time to be used as initial simulation time value for this
   * simulation run. This time value can be used for initializing a model.
   * 
   * @return the start time
   */
  @Override
  public Double getStartTime() {
    return startTime;
  }

  @Override
  public IComputationTaskStopPolicy<ISimulationRun> getStopPolicy() {
    return simRunStopPolicy;
  }

  /**
   * Gets the stop watch.
   * 
   * @return the stop watch
   */
  public StopWatch getStopWatch() {
    return stopWatch;
  }

  @Override
  public Comparable<?> getTime() {
    return processorInfo.getLocal().getTime();
  }

  /**
   * Checks if is paused on startup.
   * 
   * @return true, if is start paused
   */
  public boolean isStartPaused() {
    return config.isStartPaused();
  }

  /**
   * Gets the delay between two steps.
   * 
   * @return the delay
   */
  public long getDelay() {
    return config.getInterStepDelay();
  }

  /**
   * Sets the start time.
   * 
   * @param startTime
   *          the new start time
   */
  public void setStartTime(double startTime) {
    this.startTime = startTime;
  }

  /**
   * This method initializes the WC time. This means it sets the starttime.
   */
  public void initializeWallClockTime() {
    wcStartTime = Calendar.getInstance().getTimeInMillis();
    // System.out.println("Actual systemtime at initialization:
    // "+System.currentTimeMillis());
    // System.out.println("WC Starttime: "+wcStartTime);
  }

  /**
   * Determines whether the simulation is running or not.
   * 
   * @return true if the simulation is running, false otherwise
   */
  public boolean isRunning() {

    if (processorInfo == null) {
      return false;
    }

    // the simulation is "running" if the associated processor is running
    IProcessor<?> proc = processorInfo.getLocal();

    // if the processor is runnable this can be determined by directly
    // asking the interface
    if (proc instanceof IRunnable) {
      return ((IRunnable) proc).isRunning();
    }

    // if the processor is not runnable it is triggered by calls /
    // messages and we can assume that it is running ... (at least we have no
    // hint that's that not the case)
    return true;
  }

  /**
   * Determines whether the simulation is pausing or not.
   * 
   * @return true if the simulation is pausing, false otherwise
   */
  @Override
  public boolean isPausing() {
    boolean b = false;
    // the simulation is "pausing" if the associated processor is pausing

    IProcessor<?> proc = processorInfo.getLocal();
    if (proc instanceof IRunnable) {
      // if the processor is runnable this can be detremined by directly
      // asking
      // the interface

      b = ((IRunnable) proc).isPausing();

    } else {
      // if the processor is not runnable it is triggered by calls /
      // messages
      // and we can assume that it is running ... (at least we have no
      // hint
      // that's that not the case)
      b = false;
    }
    return b;
  }

  @Override
  public boolean isProcessorRunnable() {
    return processorInfo.getLocal() instanceof IRunnable ? true : false;
  }

  @Override
  public void pauseProcessor() {
    IProcessor<?> processor = processorInfo.getLocal();
    if (processor instanceof IRunnable) {
      ((IRunnable) processor).pause();
    } else {
      SimSystem.report(Level.SEVERE, "Processor '"
          + processorInfo.getLocal().getClassName()
          + "' does not implement IRunnable and can therefore not be paused.");
    }

  }

  @Override
  public void stopProcessor() {
    IProcessor<?> processor = processorInfo.getLocal();
    if (processor instanceof IRunnable) {
      ((IRunnable) processor).stop();
    } else {
      SimSystem.report(Level.SEVERE, "Processor '"
          + processorInfo.getLocal().getClassName()
          + "' does not implement IRunnable and can therefore not be stopped.");
    }
  }

  /**
   * Prints the results monitored if MONITORING_ENABLED is true The monitoring
   * must have been enabled before (e.g. by using the enableMonitoring method).
   */
  private void printMonitoringResults() {
    if (config.isMonitoringEnabled()) {
      // long secs = 0;

      long[] ids = ManagementFactory.getThreadMXBean().getAllThreadIds();
      for (int i = 0; i < ids.length; i++) {

        /*
         * if (ManagementFactory.getThreadMXBean().getThreadInfo(ids[i])
         * .getThreadName().startsWith("org.jamesii.core")) { secs +=
         * ManagementFactory.getThreadMXBean().getThreadCpuTime(ids[i]); }
         */

        SimSystem.report(Level.INFO, "Thread " + ids[i] + " has used "
            + ManagementFactory.getThreadMXBean().getThreadCpuTime(ids[i])
            + " nanoseconds");
        SimSystem.report(Level.INFO, " name        :"
            + ManagementFactory.getThreadMXBean().getThreadInfo(ids[i])
                .getThreadName());
        SimSystem.report(Level.INFO, " waited #    :"
            + ManagementFactory.getThreadMXBean().getThreadInfo(ids[i])
                .getWaitedCount());
        SimSystem.report(Level.INFO, " waited time :"
            + ManagementFactory.getThreadMXBean().getThreadInfo(ids[i])
                .getWaitedTime());
        SimSystem.report(Level.INFO, " blocked #   :"
            + ManagementFactory.getThreadMXBean().getThreadInfo(ids[i])
                .getBlockedCount());
        SimSystem.report(Level.INFO, " blocked time:"
            + ManagementFactory.getThreadMXBean().getThreadInfo(ids[i])
                .getBlockedTime());
        // ManagementFactory.getThreadMXBean().getThreadInfo(ids[i]).getThreadName
        // ()
      }
      // System.out.println("Secs: "+(double)(secs/1000000000));
    }
  }

  /**
   * Sets the root / top most model class. This maybe the only existing class or
   * just a root class, however that's of no interest here.
   * 
   * @param model
   *          the model
   */
  public final void setModel(IModel model) {
    this.model.setModel(model);
  }

  /**
   * Sets the processor.
   * 
   * @param processor
   *          the new processor
   */
  @Override
  public void setProcessorInfo(ProcessorInformation processor) {
    processorInfo = processor;
  }

  /**
   * Sets the start hook.
   * 
   * @param hook
   *          the new start hook
   */
  public void setStartHook(ComputationTaskHook<ProcessorInformation> hook) {
    startHook = hook;
  }

  /**
   * Sets the end hook for the simulation.
   * 
   * @param hook
   *          for executing last operations when the simulation is finished
   */
  public void setEndHook(ComputationTaskHook<ProcessorInformation> hook) {
    endHook = hook;
  }

  /**
   * Simulation time to wall clock time.
   * 
   * @param time
   *          the time
   * @param scale
   *          the scale
   * 
   * @return the double
   */
  public double simulationTimeToWallClockTime(double time, double scale) {
    // System.out.println(wcStartTime);
    return (wcStartTime + ((time - getStartTime()) * scale));
  }

  /**
   * Simulation time to wall clock time.
   * 
   * @param run
   *          the simulation run
   * @param time
   *          the time
   * @param scale
   *          the scale
   * 
   * @return the double
   */
  public static double simulationRunTimeToWallClockTime(ISimulationRun run,
      double time, double scale) {
    return (run.getWCStartTime() + ((time - run.getStartTime()) * scale));
  }

  /**
   * Start the simulation, i.e. call the processors run method.
   */
  @Override
  public void start() {

    SimSystem.report(Level.CONFIG, getIdentification() + "Using "
        + ManagementFactory.getRuntimeMXBean().getVmName() + ", version "
        + ManagementFactory.getRuntimeMXBean().getVmVersion() + " from "
        + ManagementFactory.getRuntimeMXBean().getVmVendor()
        + " as virtual machine");

    enableMonitoring();

    // check whether we have a processor for executing the model
    if (processorInfo == null) {
      throw new InvalidProcessorException(getIdentification()
          + "Ooops ... no processor to start with ...");
    }

    if (processorInfo.getLocal() == null) {
      throw new InvalidProcessorException(getIdentification()
          + "No local processor available!!!");
    }

    SimSystem.report(Level.CONFIG,
        "[Classname of the main/top most processor: "
            + processorInfo.getLocal().getClassName() + "]");

    if (processorInfo.getLocal() instanceof IRunnable) {

      if (startHook != null) {
        startHook.execute(processorInfo);
      }

      if (!config.isSilent()) {
        SimSystem.report(Level.FINER, getIdentification() + "Simulating ...");
      }

      stopWatch.start();
      initializeWallClockTime();

      ((IRunnable) processorInfo.getLocal()).run(getStopPolicy(), getDelay(),
          isStartPaused());

      stopWatch.stop();

      if (endHook != null) {
        endHook.execute(processorInfo);
      }

      printMonitoringResults();

      // write time needed for running the model into a file (if enabled)
      if (config.isLogTime()) {
        try {
          FileOutputStream fs = new FileOutputStream("./simprot.txt", true);
          try (PrintStream p = new PrintStream(fs)) {
            p.println(model.getLocal().getName() + ";"
                + stopWatch.elapsedSeconds());
          }
          // fs.close();
        } catch (Exception e) {
          SimSystem.report(e);
        }
      }

    } else {
      throw new InvalidProcessorException(getIdentification()
          + "Oooops ... the top most processor is not runnable!!!");
    }
  }

  /**
   * Get the information about the neighbours of models and processors
   * 
   * @return
   */
  public NeighbourInformation getNeighbourInformation() {
    return neighbourInformation;
  }

  /**
   * Gets the wall clock start time.
   * 
   * @return the wC start time
   */
  @Override
  public Long getWCStartTime() {
    return wcStartTime;
  }

  /**
   * Wall clock time to simulation time.
   * 
   * @param scale
   *          the scale ascertain the scaling factor how much faster the
   *          simulation time should run than the wall clock time.
   * 
   * @return the double
   */
  public double wallClockTimeToSimulationTime(double scale) {
    // scale * (twcnow - twcstart)
    // System.out.println("Start time: "+getStartTime());
    // System.out.println("WC Start time: "+wcStartTime);
    // System.out.println("Actual time: "+System.currentTimeMillis());
    // returns seconds
    return (getStartTime() + ((1 / scale) * (System.currentTimeMillis() - wcStartTime)));
  }

  @Override
  public void freeRessources() {
    getProcessorInfo().getLocal().cleanUp();
    getModel().cleanUp();
  }

  @Override
  public ComputationTaskIDObject getUniqueIdentifier() {
    return ID;
  }

  @Override
  public long getSimpleId() {
    return (Long) ID.getExternalID();
  }

  /**
   * Gets the config.
   * 
   * @return the config
   */
  @Override
  public SimulationRunConfiguration getConfig() {
    return config;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <D> D getProperty(String property) {
    Object result = null;
    if (property.compareTo("TIME") == 0) {
      result = getTime();
    }
    if (property.compareTo("STARTTIME") == 0) {
      result = getStartTime();
    }
    if (property.compareTo("ENDTIME") == 0) {
      result = getStopPolicy();
    }
    if (property.compareTo("MODEL.CLASS") == 0) {
      result = getModel().getClass();
    }
    if (property.compareTo("MODEL.NAME") == 0) {
      result = getModel().getName();
    }
    if (property.compareTo("PROCESSOR.CLASS") == 0) {
      result = getProcessorInfo().getLocal().getClass();
    }
    if (property.compareTo("PROCESSOR.STATE") == 0) {
      result = getProcessorInfo().getLocal().getState();
    }
    if (property.compareTo("CONFIGURATION.NUMBER") == 0) {
      result = Long.valueOf(getConfig().getNumber());
    }
    if (property.compareTo("CONFIGURATION.EXPERIMENTNUMBER") == 0) {
      result = getConfig().getExperimentID();
    }
    if (property.compareTo("STARTTIME.WALLCLOCK") == 0) {
      result = wcStartTime;
    }
    return (D) result;
  }

}
