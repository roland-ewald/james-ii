/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import java.io.Serializable;

import org.jamesii.core.experiments.tasks.stoppolicy.AbstractComputationTaskStopPolicy;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * The Class SimTimeEnd. Run control for running simulation runs until a
 * pre-defined simulation time value is reached. <br>
 * This end time object can return the simulation end time, thus it is extending
 * the {@link ISimulationRunStopPolicySimTime} interface. The end time returned
 * via the {@link #getEstimatedEndTime()} is fixed and will never be changed
 * after creation.
 * 
 * @author Jan Himmelspach
 */
public class SimTimeStop<TimeBase extends Comparable<TimeBase>> extends
    AbstractComputationTaskStopPolicy<ISimulationRun> implements
    ISimulationRunStopPolicySimTime<TimeBase, ISimulationRun>, Serializable {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 1L;

  /** The end time. Default is null. */
  private TimeBase endTime = null;

  /**
   * Instantiates a new simulation time end.
   * @param endTime
   *          the end time
   */
  public SimTimeStop(TimeBase endTime) {
    super();
    this.endTime = endTime;
  }

  /**
   * @return the endTime
   */
  public TimeBase getEndTime() {
    return endTime;
  }

  /**
   * @param endTime
   *          the endTime to set
   */
  public void setEndTime(TimeBase endTime) {
    this.endTime = endTime;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean hasReachedEnd(ISimulationRun r) {
    TimeBase currentTime = (TimeBase) r.getTime();
    if (currentTime == null) {
      return true;
    }
    return endTime.compareTo(currentTime) <= 0;
  }

  @Override
  public TimeBase getEstimatedEndTime() {
    return endTime;
  }

  @Override
  public String toString() {
    return "SimTimeStop [endTime=" + endTime + "]";
  }

}
