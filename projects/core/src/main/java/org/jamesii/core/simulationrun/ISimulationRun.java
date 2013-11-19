/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun;

import org.jamesii.core.base.INamedEntity;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.tasks.ComputationTaskHook;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.util.ITime;

/**
 * Interface for classes representing simulation runs. Simulation runs are
 * typically instantiated during the execution of a
 * {@link org.jamesii.core.experiments.BaseExperiment}. A simulation run
 * represents the run time (execution) information of a single model (plus model
 * parameters) and simulation algorithm combination.
 * 
 * @author Stefan Leye
 * 
 */
public interface ISimulationRun extends INamedEntity, IComputationTask,
    ITime {

  /**
   * Get used partition.
   * 
   * @return used partition
   */
  Partition getPartition();

  /**
   * Get start time for simulation (simulation time).
   * 
   * @return start time for simulation (simulation time)
   */
  Double getStartTime();

  /**
   * Returns the stop condition at which the simulation shall stop.
   * 
   * @return the stop condition
   */
  IComputationTaskStopPolicy getStopPolicy();

  /**
   * Get current simulation time of the simulation run. Usually this value will
   * be retrieved from the simulation algorithm used for this simulation run and
   * thus the result should be equal to calling the methods source directly.
   * 
   * @return current simulation time of this run
   */
  @Override
  Comparable<?> getTime();

  /**
   * Frees all resources associated with this simulation.
   */
  void freeRessources();

  /**
   * Gets the wall clock start time (in milliseconds).
   * 
   * @return the wall-clock start time
   */
  Long getWCStartTime();

  /**
   * Gets the value of a simulation run property.
   * 
   * Available properties are:<br>
   * <table>
   * <tr>
   * <td>STARTTIME</td>
   * <td>Double</td>
   * <td>the simulation time start time value</td>
   * </tr>
   * <tr>
   * <td>ENDTIME</td>
   * <td>Double</td>
   * <td>the simulation time end time value</td>
   * </tr>
   * <tr>
   * <td>TIME</td>
   * <td>Double</td>
   * <td>the current time value</td>
   * </tr>
   * <tr>
   * <td>MODEL.CLASS</td>String
   * <td></td>
   * <td>the class of the model of this simulation run</td>
   * </tr>
   * <tr>
   * <td>MODEL.NAME</td>String
   * <td></td>
   * <td>the name of the model of this simulation run</td>
   * </tr>
   * <tr>
   * <td>PROCESSOR.CLASS</td>
   * <td>String</td>
   * <td>the name of the processor / simulation algorithm used in this
   * simulation run</td>
   * </tr>
   * <tr>
   * <td>PROCESSOR.STATE</td>
   * <td>{@link org.jamesii.core.processor.ProcessorState}</td>
   * <td>the current state of the processor / simulation algorithm used in this
   * simulation run; might change frequently, thus the value obtained here might
   * be outdated directly afterward</td>
   * </tr>
   * <tr>
   * <td>CONFIGURATION.NUMBER</td>
   * <td>Long</td>
   * <td>the number of the configuration of the run</td>
   * </tr>
   * <tr>
   * <td>CONFIGURATION.EXPERIMENTNUMBER</td>
   * <td>Long</td>
   * <td>the number of the experiment the configuration belongs to</td>
   * </tr>
   * <tr>
   * <td>STARTTIME.WALLCLOCK</td>
   * <td>Long</td>
   * <td>the (wallclock) start time value (in milliseconds)</td>
   * </tr>
   * 
   * </table>
   * 
   * @param <D>
   *          the type of the property value
   * 
   * @param property
   *          the property ident of which the value shall be returned.
   * 
   * @return the property
   */
  <D> D getProperty(String property);

  @Override
  @Deprecated
  long getSimpleId();
}
