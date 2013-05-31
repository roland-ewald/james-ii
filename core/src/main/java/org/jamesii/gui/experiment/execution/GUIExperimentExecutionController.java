/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.execution;

import java.util.List;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.DefaultExecutionController;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.observe.INotifyingObserver;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.gui.experiment.ExperimentPerspective;

/**
 * This is a GUI-based execution controller, it propagate the registration
 * queries to the observer manager which then may query the user before
 * selecting observer listeners.
 * 
 * @author Roland Ewald
 */
public class GUIExperimentExecutionController extends
    DefaultExecutionController {

  /** The observation manager. */
  private ExperimentObservationManager expObsManager;

  /**
   * Instantiates a new gUI experiment execution controller.
   */
  public GUIExperimentExecutionController() {
  }

  /**
   * The Constructor.
   * 
   * @param expObsMan
   *          the exp obs man
   */
  public GUIExperimentExecutionController(ExperimentObservationManager expObsMan) {
    expObsManager = expObsMan;
  }

  /**
   * Register different types of observers.
   * 
   * @param observers
   *          the observers
   * @param srti
   *          the srti
   * @param observerType
   *          the observer type
   */
  protected void registerObservers(
      List<IObserver<? extends IObservable>> observers,
      ComputationTaskRuntimeInformation srti,
      ExperimentObserverType observerType) {
    for (IObserver<?> observer : observers) {
      if (observer instanceof INotifyingObserver) {
        switch (observerType) {
        case MODEL:
          getExpObsMan().registerModelObserver(srti,
              (INotifyingObserver<?>) observer);
          break;
        case SIMULATION:
          getExpObsMan().registerSimulationObserver(srti,
              (INotifyingObserver<?>) observer);
          break;
        case EXPERIMENT:
          getExpObsMan().registerExperimentObserver(srti,
              (INotifyingObserver<?>) observer);
          break;
        }
      }
    }
  }

  @Override
  public void computationTaskExecuted(ITaskRunner simRunner,
      ComputationTaskRuntimeInformation srti, RunInformation runInfo) {
    // TODO Notify getExperimentObservationManager
    expObsManager.runFinished(srti);
    super.computationTaskExecuted(simRunner, srti, runInfo);
  }

  @Override
  public void computationTaskInitialized(ITaskRunner simRunner,
      ComputationTaskRuntimeInformation runtimeInfo) {

    List<IObserver<? extends IObservable>> modelObserver =
        runtimeInfo.getModelObservers();
    if (modelObserver != null) {
      registerObservers(modelObserver, runtimeInfo,
          ExperimentObserverType.MODEL);
    }

    List<IObserver<? extends IObservable>> compTaskObserver =
        runtimeInfo.getComputationTaskObservers();
    if (compTaskObserver != null) {
      registerObservers(compTaskObserver, runtimeInfo,
          ExperimentObserverType.SIMULATION);
    }

    super.computationTaskInitialized(simRunner, runtimeInfo);
  }

  /**
   * This function may be removed if the old GUI gets dismissed.
   * 
   * @return the exp obs man
   */
  @Deprecated
  private ExperimentObservationManager getExpObsMan() {
    return expObsManager != null ? expObsManager : ExperimentPerspective
        .getExperimentObservationManager();
  }
}
