/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import java.util.Calendar;

import org.jamesii.core.experiments.tasks.stoppolicy.AbstractComputationTaskStopPolicy;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * The Class WallClockTimeEnd. Runs until a pre-defined wall clock time value is
 * reached. <br>
 * <b>Please note:</b> The computation task may pass the pre-defined time value.
 * This might happen if the current computation step takes too long to compute.
 * Consequently the run will be determined as soon as possible after the delta
 * has elapsed.
 * 
 * @author Jan Himmelspach
 */
public class WallClockTimeStop extends
    AbstractComputationTaskStopPolicy<ISimulationRun> {

  /** The stop time delta. Default is 0 - thus the simulation will stop at once. */
  private long stopTimeDelta = 0l;

  /** The wall-clock end time of the simulation run (in milliseconds). */
  private long wcEndTime = -1;

  /**
   * The Constructor.
   * @param stopTimeDelta
   *          the stop time delta (wall clock time, in milliseconds)
   */
  public WallClockTimeStop(Long stopTimeDelta) {
    super();
    this.stopTimeDelta = stopTimeDelta;
  }

  /**
   * @return the stopTimeDelta
   */
  public long getStopTimeDelta() {
    return stopTimeDelta;
  }

  /**
   * @param stopTimeDelta
   *          the stopTimeDelta to set
   */
  public void setStopTimeDelta(long stopTimeDelta) {
    this.stopTimeDelta = stopTimeDelta;
  }

  /**
   * Inits the object. This method will only be called once.
   */
  private void init(ISimulationRun r) {
    wcEndTime = r.getWCStartTime() + stopTimeDelta;
    if (wcEndTime < 0) {
      throw new IllegalArgumentException("current time " + stopTimeDelta
          + " resulted in a negative value, probably due to an overflow?");
    }
  }

  @Override
  public boolean hasReachedEnd(ISimulationRun r) {
    if (wcEndTime == -1l) {
      init(r);
    }
    return Calendar.getInstance().getTimeInMillis() >= wcEndTime;
  }

  @Override
  public String toString() {
    return "WallClockTimeStop [stopTimeDelta=" + stopTimeDelta + "]";
  }

}
