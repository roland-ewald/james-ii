/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.util.StopWatch;

/**
 * This class is used by the
 * {@link org.jamesii.core.experiments.taskrunner.ComputationTaskHandler} to
 * take time and memory measurements that are stored in the
 * {@link RunInformation}.
 * 
 * @author Roland Ewald
 * 
 */
public class ExecutionMeasurements extends Entity {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7273635182579519269L;

  /** The run information to which the measured data shall be written. */
  private final RunInformation info;

  /** The watch to take the time. */
  private final StopWatch watch = new StopWatch();

  /**
   * Instantiates a new object for execution measurements.
   * 
   * @param runInfo
   *          the run info
   */
  public ExecutionMeasurements(RunInformation runInfo) {
    info = runInfo;
  }

  /**
   * Gets the identification.
   * 
   * @return the identification
   */
  private String getIdentification() {
    return info.getExpID() + "." + info.getComputationTaskID() + "\t";
  }

  /**
   * Signals start of model creation.
   */
  public void startModelCreation() {
    resetAndStartWatch();
    updateCurrentMemory();
    String message =
        getIdentification() + "Memory (Total / Free / Used): "
            + displayMemInKB(info.getTotalMemory()) + " / "
            + displayMemInKB(info.getFreeMemory()) + " / "
            + displayMemInKB(info.getUsedMemory());
    SimSystem.report(Level.FINEST, message);
  }

  /**
   * Signals stop of model creation.
   */
  public void stopModelCreation() {
    watch.stop();
    info.setModelCreationTime(watch.elapsedMilliseconds());
    String message =
        getIdentification() + "Seconds needed for creating the model: "
            + info.getModelCreationTime();
    SimSystem.report(Level.FINEST, message);
    long oldUsedMem = info.getUsedMemory();
    updateCurrentMemory();
    info.setModelMemory(info.getUsedMemory() - oldUsedMem);
    String message1 =
        getIdentification() + "Memory (Total / Free / Used / Used by Model): "
            + displayMemInKB(info.getTotalMemory()) + " / "
            + displayMemInKB(info.getFreeMemory()) + " / "
            + displayMemInKB(info.getUsedMemory()) + " / "
            + displayMemInKB(info.getModelMemory());
    SimSystem.report(Level.FINEST, message1);
  }

  /**
   * Signals start of simulation creation.
   */
  public void startComputationTaskCreation() {
    resetAndStartWatch();
  }

  /**
   * Signals stop of simulation creation.
   */
  public void stopComputationTaskCreation() {
    watch.stop();
    info.setComputationTaskCreationTime(watch.elapsedMilliseconds());
    String message =
        getIdentification() + "Seconds needed for creating the simulation: "
            + info.getComputationTaskCreationTime();
    SimSystem.report(Level.FINEST, message);

    long oldUsedMem = info.getUsedMemory();
    updateCurrentMemory();
    info.setComputationTaskMemory(info.getUsedMemory() - oldUsedMem);
    String message1 =
        getIdentification()
            + "Memory (Total / Free / Used / Used by Simulation): "
            + displayMemInKB(info.getTotalMemory()) + " / "
            + displayMemInKB(info.getFreeMemory()) + " / "
            + displayMemInKB(info.getUsedMemory()) + " / "
            + displayMemInKB(info.getComputationTaskMemory());
    SimSystem.report(Level.FINEST, message1);
  }

  /**
   * Signals start of observer configuration.
   */
  public void startObserverConfiguration() {
    resetAndStartWatch();
  }

  /**
   * Signals stop of observer configuration.
   */
  public void stopObserverConfiguration() {
    watch.stop();
    info.setObserverConfigurationTime(watch.elapsedMilliseconds());
    String message =
        getIdentification() + "Seconds needed for configuring the observers: "
            + info.getObserverConfigurationTime();
    SimSystem.report(Level.FINEST, message);
    String message1 =
        getIdentification()
            + "Memory after model + simulation + observer creation (Total / Free): "
            + displayMemInKB(Runtime.getRuntime().totalMemory()) + " / "
            + displayMemInKB(Runtime.getRuntime().freeMemory());
    SimSystem.report(Level.FINEST, message1);
  }

  /**
   * Signals start of the computation task to the internal watch.
   */
  public void startComputationTask() {
    resetAndStartWatch();
  }

  /**
   * Signals stop of the computation task to the internal watch.
   */
  public void stopComputationTask() {
    watch.stop();
    info.setComputationTaskRunTime(watch.elapsedMilliseconds());
    String message =
        getIdentification() + "Seconds needed for running the simulation: "
            + info.getComputationTaskRunTime();
    SimSystem.report(Level.INFO, message);
  }

  /**
   * Resets and starts watch.
   */
  void resetAndStartWatch() {
    watch.reset();
    watch.start();
  }

  /**
   * Display memory in kilobytes.
   * 
   * @param memory
   *          the memory in bytes
   * 
   * @return the amount of memory in kilobytes as a string composed by the vlaue
   *         of the amount plus " KB".
   */
  String displayMemInKB(long memory) {
    return (memory >> 10) + " KB ";
  }

  /**
   * Update current memory size (free / total).
   */
  void updateCurrentMemory() {
    info.setTotalMemory(Runtime.getRuntime().totalMemory());
    info.setFreeMemory(Runtime.getRuntime().freeMemory());
  }

}
