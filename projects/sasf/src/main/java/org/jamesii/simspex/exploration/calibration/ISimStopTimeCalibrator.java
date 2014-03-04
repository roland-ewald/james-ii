/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.calibration;


import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;

/**
 * Interface for model calibration. Each
 * {@link org.jamesii.simspex.exploration.ISimSpaceExplorer} that relies on model
 * calibration should employ the following protocol: first,
 * {@link ISimStopTimeCalibrator#setNewModelSetup(java.util.Set)} is called to
 * signal a new model setup for which the model needs to be calibrated. Second,
 * {@link ISimStopTimeCalibrator#getNextSimTime()} is called and the given setup
 * is executed. The wall-clock time required to simulate the model with the
 * current setup and the configuration as defined by the returned
 * {@link SelectionTreeSetElement} until the given simulation end time is
 * returned via {@link ISimStopTimeCalibrator#simFinished(double)}. Afterwards,
 * the {@link ISimStopTimeCalibrator} will be queried if the calibration is
 * done, via {@link ISimStopTimeCalibrator#done()}. If this returns true, the
 * most suitable simulation end time that was determined can be retrieved via
 * {@link ISimStopTimeCalibrator#getCalibratedEndTime()}.
 * 
 * @see org.jamesii.simspex.exploration.ISimSpaceExplorer
 * 
 * @author Roland Ewald
 * 
 */
public interface ISimStopTimeCalibrator extends Serializable {

  /**
   * Sets the current model setup.
   * 
   * @param modelSetup
   *          the current setup of model variables
   */
  void setNewModelSetup(Map<String, Serializable> modelSetup);

  /**
   * Retrieve next simulation time to choose.
   * 
   * @return a pair with the tuple (suggested simulation stop time, maximal
   *         wall-clock time in ms) as first element and the configuration to be
   *         selected as second element
   */
  Pair<Pair<Double, Long>, ParameterBlock> getNextSimTime();

  /**
   * Signals that a simulation run has been finished, returns the wall-clock
   * time duration of the suggested simulation end time and configuration.
   * 
   * @param duration
   */
  void simFinished(double duration);

  /**
   * Signals the end of the calibration for the model setup given before.
   * 
   * @return true if this calibration phase is done
   */
  boolean done();

  /**
   * Get the calibrated simulation end time.
   * 
   * @return the calibrated simulation end time
   */
  double getCalibratedEndTime();
}
