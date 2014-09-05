/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks;

import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.util.id.IUniqueIdentifier;

/**
 * The Interface IComputationTask provides basic control about any task being
 * computed using the software.
 * 
 * @author Jan Himmelspach
 */
public interface IComputationTask extends IUniqueIdentifier, IObservable {

  /**
   * Start the computation of the task, i.e., call the processor's run method.
   */
  void start();

  /**
   * Indicates whether the computation of the task is pausing.
   * 
   * @return true if the simulation is pausing.
   */
  boolean isPausing();

  /**
   * Returns whether the main/top-most processor assigned to this computation
   * task is runnable.
   * 
   * For the system all those algorithms are runnable/executable which implement
   * the {@link org.jamesii.core.processor.IRunnable} interface.
   * 
   * @return true if the processor implements
   *         {@link org.jamesii.core.processor.IRunnable}, otherwise false.
   */
  boolean isProcessorRunnable();

  /**
   * Pause the processor.
   */
  void pauseProcessor();

  /**
   * Stop the processor.
   */
  void stopProcessor();

  /**
   * Get the instance of the model the computation task instance is working on.
   * 
   * @return the model
   */
  IModel getModel();

  /**
   * Get processor information.
   * 
   * @return processor information for this simulation
   */
  ProcessorInformation getProcessorInfo();

  /**
   * Sets the processor.
   * 
   * @param processor
   *          the new processor
   */
  void setProcessorInfo(ProcessorInformation processor);

  /**
   * Get the object containing the universal ID of this simulation run.
   * 
   * FIXME This method hides a method from the interface this one extends :-(
   * 
   * @return the ID object
   */
  @Override
  ComputationTaskIDObject getUniqueIdentifier();

  /**
   * Gets the configuration used for the instantiation of this computation task
   * instance.
   * 
   * @return the configuration used for creation
   */
  IComputationTaskConfiguration getConfig();

}
