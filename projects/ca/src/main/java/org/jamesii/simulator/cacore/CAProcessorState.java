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

import org.jamesii.core.processor.ProcessorState;

// TODO: Auto-generated Javadoc
/**
 * The Class CAProcessorState.
 */
public class CAProcessorState extends ProcessorState {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 3113983754849043320L;

  /** The time. */
  private double time = 0;

  /**
   * ***************************************************************************
   * .
   * 
   * @return the time
   */
  public double getTime() {
    return time;
  }

  /**
   * Sets the time.
   * 
   * @param time
   *          the time
   */
  public void setTime(double time) {
    this.time = time;
    changed();
  }

}
