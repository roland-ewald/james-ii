/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.setup;

import java.io.Serializable;
import java.util.List;

import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.experiments.ExperimentException;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.tasks.IInitializedComputationTask;

/**
 * The Interface IComputationTaskSetup. Has to be implemented by choices to
 * compute a solution for a problem. The problem's parameterization will be
 * given by the experimentation layer of the software which expects any classes
 * implementing this interface to instantiate the means to work on the problem
 * as it has been specified.
 * 
 * @author Jan Himmelspach
 */
public interface IComputationTaskSetup extends Serializable {

  /**
   * Clean up. Cleaning up everything. E.g., unregister observers, databases,
   * etc ...
   * 
   * @param compTaskConfig
   *          the computation task configuration
   * @param runInfo
   *          the run info
   */
  void cleanUp(IComputationTaskConfiguration compTaskConfig,
      RunInformation runInfo);

  /**
   * Creates an InitializedSimulation object, using a SimulationConfiguration
   * and a ModelReader. Information about the initialisation process are stored
   * in RunInformation and StringBuffer.
   * 
   * @param compTaskConfig
   *          the computation task configuration
   * @param modelReader
   *          the model reader to be used to instantiate the model;
   * @param info
   *          the run information "result" - this one has to be updated by this
   *          method (insert times needed for the different phases of
   *          instantiation)
   * @param out
   *          the StringBuffer to write output to
   * @param resources
   *          the available resources
   * 
   * @return initialised computation task
   * 
   * @throws ExperimentException
   *           the experiment exception will be thrown if the instantiation of
   *           the run did fail
   */
  IInitializedComputationTask initComputationTask(
      IComputationTaskConfiguration compTaskConfig, IModelReader modelReader,
      RunInformation info, StringBuffer out, List<ISimulationServer> resources);

}
