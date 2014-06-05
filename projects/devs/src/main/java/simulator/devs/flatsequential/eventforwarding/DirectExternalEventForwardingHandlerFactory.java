/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.flatsequential.eventforwarding;

import org.jamesii.core.parameters.ParameterBlock;

import simulator.devs.flatsequential.eventforwarding.plugintype.ExternalEventForwardingHandlerFactory;

/**
 * The Class DirectExternalEventForwardingHandlerFactory.
 * 
 * @author Jan Himmelspach
 */
public class DirectExternalEventForwardingHandlerFactory extends
    ExternalEventForwardingHandlerFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  @Override
  public ExternalEventForwardingHandler create(ParameterBlock parameters) {
    return new DirectExternalEventForwardingHandler();
  }

}
