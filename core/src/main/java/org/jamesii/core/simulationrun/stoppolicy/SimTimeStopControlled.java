/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import org.jamesii.core.experiments.tasks.stoppolicy.AbstractComputationTaskStopPolicy;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.util.ITime;

/**
 * Usually you should use the {@link SimTimeStop} class instead.
 * 
 * This class takes an ITime<?> instance instead of a SimulationRun and can
 * thus be used whenever there is no simulation run object for the thing
 * currently going on. This might be the case if a simulation algorithm shall be
 * tested with a Unit test, for example.
 * 
 * @author Jan Himmelspach
 */
public class SimTimeStopControlled<TimeBase extends Comparable<TimeBase>> extends
    AbstractComputationTaskStopPolicy<ISimulationRun> implements
    ISimulationRunStopPolicySimTime<TimeBase> {

  /** The stop time. Default is null */
  private TimeBase stopTime = null;

  /** The run. */
  private ITime<TimeBase> timeSource;

  /**
   * The Constructor.
   * 
   * @param timeSource
   *          the simulation run which shall be "observed"
   * @param stopTime
   *          the stop time
   */
  public SimTimeStopControlled(ITime<TimeBase> timeSource, TimeBase stopTime) {
    super(null);
    this.timeSource = timeSource;
    this.stopTime = stopTime;
  }

  @Override
  public boolean hasReachedEnd() {
    return stopTime.compareTo(timeSource.getTime()) <= 0;
  }

  /**
   * Returns the stop time.
   * 
   * @return The stop time.
   */
  public TimeBase getStopTime() {
    return this.stopTime;
  }

  /**
   * @param stopTime
   *          the stopTime to set
   */
  public void setStopTime(TimeBase stopTime) {
    this.stopTime = stopTime;
  }

  /**
   * @return the timeSource
   */
  public ITime<TimeBase> getTimeSource() {
    return timeSource;
  }

  /**
   * @param timeSource
   *          the timeSource to set
   */
  public void setTimeSource(ITime<TimeBase> timeSource) {
    this.timeSource = timeSource;
  }

  @Override
  public TimeBase getEstimatedEndTime() {
    return this.stopTime;
  }
}
