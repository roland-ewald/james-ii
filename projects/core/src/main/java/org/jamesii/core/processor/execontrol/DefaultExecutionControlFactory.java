/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.execontrol;

import java.util.List;

import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.Processor;
import org.jamesii.core.processor.execontrol.plugintype.ExecutionControlFactory;

/**
 * The default execution control factory stays in the core, to ensure that all
 * processor can be initialised with *some* execution controller.
 * 
 * @author Roland Ewald
 * 
 *         16.04.2007
 */
public class DefaultExecutionControlFactory extends ExecutionControlFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1720733065366790803L;

  @Override
  public ExecutionControl createExecutionControl(Processor processor) {
    return new ExecutionControl(processor);
  }

  @Override
  public List<Class<? extends IProcessor>> getSupportedProcessors() {
    return null;
  }

}
