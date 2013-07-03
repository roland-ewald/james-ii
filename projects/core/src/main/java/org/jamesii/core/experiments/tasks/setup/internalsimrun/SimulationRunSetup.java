/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.setup.internalsimrun;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.data.ISQLDataBase;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.experiments.ExecutionMeasurements;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.instrumentation.IInstrumenter;
import org.jamesii.core.experiments.optimization.parameter.instrumenter.IResponseObserverInstrumenter;
import org.jamesii.core.experiments.taskrunner.InitializedComputationTask;
import org.jamesii.core.experiments.tasks.IInitializedComputationTask;
import org.jamesii.core.experiments.tasks.setup.IComputationTaskSetup;
import org.jamesii.core.model.IModel;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.simulationrun.SimulationRun;

/**
 * Creation of an instance of a simulation run based on a computation task
 * description. This class is for internal simulation runs only.
 * 
 * @author Jan Himmelspach
 * 
 */
public class SimulationRunSetup implements IComputationTaskSetup {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2334311534503631895L;

  /**
   * Stores references to the data storages that have been initialised on the
   * local machines, using execution identifiers (= experiment ID + simulation
   * ID) as key.
   */
  private static Map<String, IDataStorage> localDataStorages = new HashMap<>();

  /**
   * Returns a string describing the details of the current run.
   * 
   * @param simRunConfig
   *          the simulation configuration
   * 
   * @return the description of the run (for error messages etc.)
   */
  protected static String getRunDescription(
      SimulationRunConfiguration simRunConfig) {
    return simRunConfig.toString();
  }

  @Override
  public IInitializedComputationTask initComputationTask(
      IComputationTaskConfiguration computationTaskConfig,
      IModelReader modelReader, RunInformation info, StringBuffer out,
      List<ISimulationServer> resources) {

    // Please do not execute the garbage collector herein - this will kill any
    // parallel performance as this method might be executed in parallel

    ExecutionMeasurements execMeasures = new ExecutionMeasurements(info, out);

    // Instantiate model and simulation
    ISimulationRun simulation = null;

    try {
      IModel model =
          createModel((SimulationRunConfiguration) computationTaskConfig,
              modelReader, execMeasures);
      simulation =
          createSimulation((SimulationRunConfiguration) computationTaskConfig,
              resources, execMeasures, model);
      createObservers((SimulationRunConfiguration) computationTaskConfig, info,
          execMeasures, simulation);
    } catch (Throwable t) {
      return handleError((SimulationRunConfiguration) computationTaskConfig, t,
          out, info);
    }
    String message = "Simulation run with expID:" + info.getExpID()
        + " and simID:" + info.getComputationTaskID() + " initialised at "
        + new SimpleDateFormat().format(new Date());

    if (out != null) {
      out.append(message);
    }
    SimSystem.report(Level.INFO, message);

    return new InitializedComputationTask(simulation, info);
  }

  /**
   * Creates the model.
   * 
   * @param simRunConfig
   *          the sim run config
   * @param modelReader
   *          the model reader
   * @param execMeasures
   *          the exec measures
   * 
   * @return the i model
   * 
   * @throws Throwable
   *           the throwable
   */
  protected static IModel createModel(SimulationRunConfiguration simRunConfig,
      IModelReader modelReader, ExecutionMeasurements execMeasures) {
    IModel model;
    execMeasures.startModelCreation();
    model =
        modelReader.read((URI) simRunConfig.getAbsModelReaderFactoryParams()
            .getSubBlockValue(IURIHandling.URI), simRunConfig.getParameters());
    execMeasures.stopModelCreation();
    return model;
  }

  /**
   * Creates the simulation.
   * 
   * @param simRunConfig
   *          the sim run config
   * @param resources
   *          the resources
   * @param execMeasures
   *          the exec measures
   * @param model
   *          the model
   * 
   * @return the i simulation run
   * 
   * @throws Throwable
   *           the throwable
   */
  protected static ISimulationRun createSimulation(
      SimulationRunConfiguration simRunConfig,
      List<ISimulationServer> resources, ExecutionMeasurements execMeasures,
      IModel model) {
    ISimulationRun simulation;
    execMeasures.startComputationTaskCreation();
    simulation = new SimulationRun("SimRun", model, simRunConfig, resources);
    execMeasures.stopComputationTaskCreation();
    return simulation;
  }

  /**
   * Creates the observers.
   * 
   * @param simRunConfig
   *          the sim run config
   * @param info
   *          the info
   * @param execMeasures
   *          the exec measures
   * @param simulation
   *          the simulation
   * 
   * @throws Throwable
   *           the throwable
   */
  protected static void createObservers(
      SimulationRunConfiguration simRunConfig, RunInformation info,
      ExecutionMeasurements execMeasures, ISimulationRun simulation) {
    // Attach observers
    execMeasures.startObserverConfiguration();

    // Initialise data storage
    IDataStorage ds = simRunConfig.createDataStorage();
    if (ds != null) {
      localDataStorages.put(info.getExecutionIDs(), ds);
    }

    // Instrument model and simulation
    simRunConfig.instrumentModel(simulation.getModel(), ds);
    simRunConfig.instrumentSimulation(simulation, ds);

    // Trigger initial update (before run actually started)
    initialUpdate(simulation.getModel(), simulation);
    execMeasures.stopObserverConfiguration();
  }

  /**
   * Handle error during execution.
   * 
   * @param simRunConfig
   *          the simulation run configuration that failed
   * @param cause
   *          the cause of the error
   * @param out
   *          the output stream for error messages
   * @param info
   *          the runtime information
   * @return an error instance of {@link InitializedComputationTask}
   */
  protected static InitializedComputationTask handleError(
      SimulationRunConfiguration simRunConfig, Throwable cause,
      StringBuffer out, RunInformation info) {
    String message =
        "Model creation failed! \nThis happened while trying to instantiate the model for the simulation run "
            + getRunDescription(simRunConfig)
            + "\nThe exception is: "
            + cause.getMessage()
            + "\nNOTE: The execution will automatically continue with the next run!!!";
    SimSystem.report(cause);
    if (out != null) {
      out.append(message);
    }
    SimSystem.report(Level.INFO, message);
    info.storeFailure(message, cause);
    return new InitializedComputationTask(null, info);
  }

  /**
   * Gather direct responses from instrumenters, store them in run information.
   * 
   * @param runInfo
   *          the run information
   * @param simRunConfig
   *          the simulation run configuration
   */
  protected static void gatherDirectResponses(RunInformation runInfo,
      SimulationRunConfiguration simRunConfig) {
    Map<String, Object> response = new HashMap<>();
    checksInstrumenters(simRunConfig.getModelInstrumenters(), response);
    checksInstrumenters(simRunConfig.getSimulationInstrumenters(), response);
    if (response.size() > 0) {
      runInfo.setResponse(response);
    }
  }

  /**
   * Checks instrumenters.
   * 
   * @param response
   *          the response
   * @param instrumenters
   *          the instrumenters
   */
  protected static void checksInstrumenters(
      List<? extends IInstrumenter> instrumenters, Map<String, Object> response) {
    if (instrumenters != null) {
      for (IInstrumenter instrumenter : instrumenters) {
        if (instrumenter instanceof IResponseObserverInstrumenter) {
          mergeResponses(response, (IResponseObserverInstrumenter) instrumenter);
        }
      }
    }
  }

  /**
   * Merge responses.
   * 
   * @param response
   *          the response
   * @param instrumenter
   *          the instrumenter
   */
  protected static void mergeResponses(Map<String, Object> response,
      IResponseObserverInstrumenter instrumenter) {
    response.putAll(instrumenter.getObservedResponses());
  }

  /**
   * Removes the data storage of the given simulation run.
   * 
   * @param simRunConfig
   *          the simulation run configuration
   * @param runInfo
   *          the run info
   */
  protected static void removeSimRunDataStorage(
      SimulationRunConfiguration simRunConfig, String execIDs) {
    if (!simRunConfig.hasDataStorageFactory()) {
      return;
    }
    try {
      IDataStorage ds = localDataStorages.remove(execIDs);
      if (ds != null) {
        ds.flushBuffers();
        if (ds instanceof ISQLDataBase) {
          ((ISQLDataBase) ds).closeBase();
        }
      }
    } catch (Exception ex) {
      SimSystem.report(ex);
    }
  }

  /**
   * Initial update. Forces observers to update at start time, can hence be used
   * to store the initial state. However, only the top most level objects are
   * marked as "changed" here.
   * 
   * @param model
   *          the model
   * @param simulation
   *          the simulation
   */
  protected static void initialUpdate(IModel model, ISimulationRun simulation) {
    simulation.changed();
    model.changed();
    // // TODO: Test this as alternative
    // Observation.updateObject(simulation);
    // Observation.updateObject(model);
  }

  @Override
  public void cleanUp(IComputationTaskConfiguration simRunConfig,
      RunInformation runInfo) {
    gatherDirectResponses(runInfo, (SimulationRunConfiguration) simRunConfig);
    removeSimRunDataStorage((SimulationRunConfiguration) simRunConfig,
        runInfo.getExecutionIDs());
  }

}
