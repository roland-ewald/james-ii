/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.util;

import org.jamesii.SimSystem;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.simulationrun.SimulationRun;

/**
 * The class PaceProcessor can be used whenever a processor has to be paced with
 * wall clock time.
 * 
 * @author Jan Himmelspach
 * 
 */
public final class PaceProcessor extends BasicHandler {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 4653264401389801426L;

  /**
   * Instantiates a new pace processor.
   */
  public PaceProcessor() {
    super();
  }

  /**
   * This method will send a calling Thread to sleep, if the conversion method
   * of the simulation time to wall clock time returns a positive value.
   * Otherwise it will immediately return.
   * 
   * @param simulation
   *          The simulation the processor to be paced belongs to
   * @param time
   *          The time of the model to be paced
   * @param scale
   *          the scale
   */
  public void pace(ISimulationRun simulation, double time, double scale) {

    try {
      long sleepTime;
      // sleeptime = tnextEvent - t

      sleepTime =
          (long) (SimulationRun.simulationRunTimeToWallClockTime(simulation,
              time, scale) - System.currentTimeMillis());

      if (sleepTime > 0) {
        Thread.sleep(sleepTime);
      }

    } catch (InterruptedException e) {
      SimSystem.report(e);
    }
  }
}
