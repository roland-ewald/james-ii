/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner.plugintype;

import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The base class for all factories for creating task runners.
 * 
 * @author Stefan Leye
 */
public abstract class TaskRunnerFactory extends Factory<ITaskRunner> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -178716214662191317L;

  /**
   * Return a new instance of the task runner to be used.
 * @param parameter
   *          configuration parameters
 * @return simulation runner
   */
  @Override
  public abstract ITaskRunner create(ParameterBlock parameter, Context context);

}
