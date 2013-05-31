/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.model.IModel;
import org.jamesii.core.util.ITime;

/**
 * The Interface IProcessor. This interface has to be implemented by ALL all
 * model computation / simulation parameter combination computation algorithms /
 * analysis algorithms to be usable from the software. <br>
 * The (most important) method of the interface is the
 * {@link #executeNextStep()} method. Whenever possible computation algorithms
 * should partition the computation of the complete simulation runs into "steps"
 * - which is not a problem for most algorithms in anyway: nearly all common
 * computation protocols are realized in loops, and then a step is just one pass
 * of the main loop. The advantage here is that the software can principally
 * proceed the computation tasks stepwise later on, and that pausing / stopping
 * is fairly easy to integrate. If you can't partition your processing than its
 * fine if you just compute the task after the first (and single) call of this
 * method.
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IProcessor<TimeBase extends Comparable<TimeBase>> extends
    IEntity, ITime<TimeBase> {

  /**
   * Every processor has to process the next computation step if this method is
   * called. If possible a processor should need more than call of this method
   * to compute the complete computation task. The loop to do so is required by
   * the {@link IRunnable} interface and can be easily added by using the
   * {@link org.jamesii.core.processor.execontrol.ExecutionControl} class. <br>
   * Basically such a step can be
   * <ul>
   * <li>in the discrete - step wise case: the computation of the next time step
   * </li>
   * <li>in the discrete - event case: the computation of the next event</li>
   * <li>in the continuous case: the computation of the next integration step</li>
   * <li>...</li>
   * </ul>
   * Typically the time has to be incremented at the end of a call of this
   * method.
   */
  void executeNextStep();

  /**
   * Gets the class name. This is a helper function needed in a distributed
   * setup where the original class name of the processor is hidden behind the
   * remote handling references, and proxies.
   * 
   * @return the class name
   */
  String getClassName();

  /**
   * Returns a reference to the associated model`s interface.
   * 
   * @param <M>
   * 
   * @return pointer to the associated interface
   */
  <M extends IModel> M getModel();

  /**
   * Returns the state of this processor.
   * 
   * @return the state of this processor
   */
  ProcessorState getState();

  /**
   * Gets the current (simulation) "time".
   * 
   * @return the time
   */
  @Override
  TimeBase getTime();

  /**
   * Sets the model to be computed by using this processor. <b>This method
   * should not be called again after the processor has started to compute the
   * model.</b> Setting a model might imply expensive initialization calls,
   * e.g., to create optimized data structures to operate on - thus you should
   * usually use this method only once at all.
   * 
   * @param model
   *          to be computed
   */
  void setModel(IModel model);

  /**
   * Sets the computation task object this processor will work in.
   * 
   * @param computationTask
   *          run this processor is part of
   */
  void setComputationTask(IComputationTask computationTask);

  /**
   * Will be called at the end of a simulation run (after the run stopped).
   */
  void cleanUp();

}
