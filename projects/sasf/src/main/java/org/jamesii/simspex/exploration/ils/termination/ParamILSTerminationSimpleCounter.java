/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.termination;

import org.jamesii.simspex.exploration.ils.algorithm.ParamILS;


/**
 * Simple counting termination for {@link ParamILS}. It counts the number of
 * calls to the update function.
 * 
 * @author Robert Engelke
 */
public class ParamILSTerminationSimpleCounter implements
    ITerminationIndicator<ParamILS> {

  static final int DEFAULT_TERMINATION_INDICATOR = 1000;

  private int terminationIndicator;

  /**
   * Instantiates a new param ils termination simple counter. It uses the
   * default value of 1000 updates.
   */
  public ParamILSTerminationSimpleCounter() {
    terminationIndicator = DEFAULT_TERMINATION_INDICATOR;
  }

  /**
   * Instantiates a new param ils termination simple counter.
   * 
   * @param initialTerminationValue
   *          the initial termination value
   */
  public ParamILSTerminationSimpleCounter(int initialTerminationValue) {
    terminationIndicator = initialTerminationValue;
  }

  @Override
  public boolean shallTerminate(ParamILS element) {
    return terminationIndicator < 0;
  }

  @Override
  public void updateTerminationCriterion(ParamILS element) {
    terminationIndicator--;
  }

}
