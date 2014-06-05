/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devscore;

import org.jamesii.core.observe.ProcessorStateObserver;

/**
 * DEVSProcessorStateObserver.
 * 
 * @author Jan Himmelspach
 */
public class DEVSProcessorStateObserver<E extends DEVSProcessorState> extends
    ProcessorStateObserver<E> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -8393398228086748633L;

  /**
   * This method is called when information about an DEVSProcessorState which
   * was previously requested using an asynchronous interface becomes available.
   */
  public DEVSProcessorStateObserver() {
    super();
  }

  /**
   * The Constructor.
   * 
   * @param cycle
   *          the cycle
   */
  public DEVSProcessorStateObserver(double cycle) {
    super(cycle);
  }

  @Override
  protected double getSimTime(E state) {
    return state.getTonie();
  }
}
