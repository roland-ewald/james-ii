/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.overview;

import org.jamesii.core.experiments.ComputationRuntimeState;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.RunInformation;

/**
 * Object to display/control the status of a single simulation run in the table
 * of {@link ExperimentOverviewWindow}.
 * 
 * @author Roland Ewald
 */
public class SimulationTableInformation {

  /** Run information for this simulation run. */
  private RunInformation runInfo = null;

  /** The simulation run's runtime information. */
  private final ComputationTaskRuntimeInformation srti;

  /** The number of this simulation run. */
  private final int number;

  /**
   * Default constructor.
   * 
   * @param srti
   *          simulation runtime information
   * @param num
   *          the num
   */
  public SimulationTableInformation(ComputationTaskRuntimeInformation srti,
      int num) {
    this.srti = srti;
    number = num;
  }

  /**
   * Gets the run info.
   * 
   * @return the run info
   */
  public RunInformation getRunInfo() {
    return runInfo;
  }

  /**
   * Sets the run info.
   * 
   * @param runInfo
   *          the new run info
   */
  public void setRunInfo(RunInformation runInfo) {
    this.runInfo = runInfo;
  }

  /**
   * Gets the sim state.
   * 
   * @return the sim state
   */
  public ComputationRuntimeState getSimState() {
    return srti.getState();
  }

  /**
   * @return the srti
   */
  protected final ComputationTaskRuntimeInformation getSrti() {
    return srti;
  }

  /**
   * @return the number
   */
  protected final int getNumber() {
    return number;
  }

}
