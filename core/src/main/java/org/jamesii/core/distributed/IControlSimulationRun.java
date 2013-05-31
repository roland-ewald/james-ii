/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed;

import java.rmi.RemoteException;

import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;

/**
 * The Interface IControlSimulationRun. This interface can be provided by
 * classes which can forward method calls to simulation runs identified by an
 * id.<br>
 * This helps to keep the interfaces of these classes lean, but we can still
 * access all {@link org.jamesii.core.processor.IRunnable} methods.
 * 
 * Classes which might need this interface are simulation server classes.
 * 
 * @author Jan Himmelspach
 */
public interface IControlSimulationRun {

  /**
   * Execute the given command on the simulation run identified by the id
   * object.
   * 
   * The allowed commands are those defined in the
   * {@link org.jamesii.core.processor.IRunnable} interface.
   * 
   * @param <D>
   *          the type of the return value
   * 
   * @param simulationRunId
   *          the id of the simulation the runnable processor of shall execute
   *          the passed command
   * @param command
   *          the command to be executed by the processor (simulation algorithm)
   *          of the simulation run
   * @param args
   *          the arguments to be handed over to the method call
   * 
   * @return the result of the method call, might be null: if the method does
   *         not have any return value at all or if the method called just
   *         returns null
   * @throws RemoteException
   */
  <D> D executeRunnableCommand(ComputationTaskIDObject simulationRunId,
      String command, Object[] args) throws RemoteException;

}
