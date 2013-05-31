/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.setup.plugintype;

import org.jamesii.core.experiments.tasks.setup.IComputationTaskSetup;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The base class for all factories for creating task setups. A computation task
 * setup ({@link org.jamesii.core.experiments.tasks.setup.IComputationTaskSetup}
 * is responsible for instantiating a class which implements the
 * {@link org.jamesii.core.experiments.tasks.IInitializedComputationTask}
 * interface. <br/>
 * This means that the framework can integrate any means to compute / analyze
 * models - by using internal methods (the classical IModel - IProcessor pair)
 * or by delegating the computation and model / algorithm instantiation to an
 * external software.
 * 
 * @author Jan Himmelspach
 */
public abstract class TaskSetupFactory extends Factory<IComputationTaskSetup> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -605063205512804529L;

  /**
   * Return a new instance of the computation task to be used.
   * 
   * @param parameter
   *          configuration parameters
   * 
   * @return the class to instantiate the problem and the solution algorithms
   *         for a computation task
   */
  @Override
  public abstract IComputationTaskSetup create(ParameterBlock parameter);

  /**
   * Used to filter factories according to a processor factory.
   * 
   * @param factory
   *          the processor factory
   * @return true, if supporting
   */
  public abstract boolean supportsProcessor(Class<? extends Factory> factory);
}
