/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.util;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IExperimentExecutionListener;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.observe.IObserver;

/**
 * Simple {@link IExperimentExecutionListener} that stores the
 * {@link ComputationTaskRuntimeInformation} of executed tasks, to facilitate
 * accessing some runtime information that is not stored in the history of
 * {@link BaseExperiment}:
 * 
 * <b>NOTE</b>: This listener stores <i>all<i/>
 * {@link ComputationTaskRuntimeInformation} objects associated with a certain
 * task, and will hence require quite some memory.
 * 
 * @author Roland Ewald
 * 
 */
public class TaskRuntimeInformationListener implements
    IExperimentExecutionListener {

  /** The list of all data associated with the executed runs. */
  private List<ComputationTaskRuntimeInformation> runtimeInformations =
      new ArrayList<>();

  @Override
  public void simulationExecuted(ITaskRunner taskRunner,
      ComputationTaskRuntimeInformation crti, boolean jobDone) {
    runtimeInformations.add(crti);
  }

  /**
   * Gets the all model observers.
   * 
   * @return list of all model observers
   */
  public List<IObserver<?>> getAllModelObservers() {
    List<IObserver<?>> observers = new ArrayList<>();
    for (ComputationTaskRuntimeInformation crti : runtimeInformations) {
      observers.addAll(crti.getModelObservers());
    }
    return observers;
  }

  /**
   * Gets the all computation task observers.
   * 
   * @return list of all computation task observers
   */
  public List<IObserver<?>> getAllComputationTaskObservers() {
    List<IObserver<?>> observers = new ArrayList<>();
    for (ComputationTaskRuntimeInformation crti : runtimeInformations) {
      observers.addAll(crti.getComputationTaskObservers());
    }
    return observers;
  }

  @Override
  public void experimentExecutionStarted(BaseExperiment experiment) {
  }

  @Override
  public void experimentExecutionStopped(BaseExperiment experiment) {
  }

  @Override
  public void simulationInitialized(ITaskRunner simRunner,
      ComputationTaskRuntimeInformation crti) {
  }

}
