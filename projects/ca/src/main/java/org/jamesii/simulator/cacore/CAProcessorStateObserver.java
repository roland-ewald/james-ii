/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * Title:        CoSA: 
 * Description:
 * Copyright:    Copyright (c) 2004
 * Company:      University of Rostock, Faculty of Computer Science
 *               Modeling and Simulation group
 * Created on 09.06.2004
 * @author       Jan Himmelspach
 * @version      1.0
 */
package org.jamesii.simulator.cacore;

import org.jamesii.core.observe.ProcessorStateObserver;

// TODO: Auto-generated Javadoc
/**
 * An asynchronous update interface for receiving notifications about
 * CAProcessorState information as the CAProcessorState is constructed.
 */
public class CAProcessorStateObserver<E extends CAProcessorState> extends
    ProcessorStateObserver<E> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -4234712928940097770L;

  /**
   * Gets the sim time.
   * 
   * @param state
   *          the state
   * 
   * @return the sim time
   */
  @Override
  protected double getSimTime(E state) {
    return state.getTime();
  }

}
