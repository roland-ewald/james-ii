/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.flatsequential.eventforwarding.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

import simulator.devs.flatsequential.eventforwarding.ExternalEventForwardingHandler;

/**
 * The Class ExternalEventForwardingHandlerFactory.
 * 
 * @author Jan Himmelspach *
 */
public abstract class ExternalEventForwardingHandlerFactory extends
    Factory<ExternalEventForwardingHandler> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 9123368460433503512L;

  /**
   * Gets the handler.
   * 
   * @param parameters
   *          TODO
   * 
   * @return the handler
   */
  public abstract ExternalEventForwardingHandler create(
      ParameterBlock parameters, Context context);

}
