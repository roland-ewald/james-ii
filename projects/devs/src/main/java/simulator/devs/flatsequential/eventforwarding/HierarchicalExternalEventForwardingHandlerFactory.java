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
 * The Class HierarchicalExternalEventForwardingHandlerFactory.
 * 
 * @author Jan Himmelspach
 */
public class HierarchicalExternalEventForwardingHandlerFactory extends
    ExternalEventForwardingHandlerFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6546831084766313384L;

  @Override
  public ExternalEventForwardingHandler create(ParameterBlock parameters) {
    return new HierarchicalExternalEventForwardingHandler();
  }

}
