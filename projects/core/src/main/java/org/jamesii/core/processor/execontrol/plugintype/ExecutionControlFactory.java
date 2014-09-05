/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.execontrol.plugintype;

import java.util.List;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.Processor;
import org.jamesii.core.processor.execontrol.ExecutionControl;

/**
 * Super class of all Execution Control Factories.
 * 
 * @author Roland Ewald
 * 
 *         16.04.2007
 */
public abstract class ExecutionControlFactory extends Factory<ExecutionControl> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4803781804110551064L;

  /**
   * Creates execution control.
   * 
   * @param processor
   *          the processor
   * 
   * @return the execution control
   */
  public abstract ExecutionControl createExecutionControl(Processor processor);

  /**
   * Returns list of supported processor interfaces.
   * 
   * @return the supported processors
   */
  public abstract List<Class<? extends IProcessor>> getSupportedProcessors();

  @Override
  public ExecutionControl create(ParameterBlock parameters, Context context) {
    return createExecutionControl((Processor) parameters
        .getSubBlockValue("PROCESSOR"));
  }

}
