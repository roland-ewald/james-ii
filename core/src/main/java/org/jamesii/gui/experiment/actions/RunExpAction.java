/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.IExperimentExecutionListener;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.action.DefaultSwingAction;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.experiment.ExperimentExecutorThreadPool;
import org.jamesii.gui.experiment.execution.ExperimentObservationManager;
import org.jamesii.gui.experiment.execution.ExperimentThread;
import org.jamesii.gui.experiment.execution.GUIExperimentExecutionController;
import org.jamesii.gui.experiment.windows.edit.ExperimentEditor;
import org.jamesii.gui.experiment.windows.overview.ExperimentOverviewWindow;

/**
 * Action to start and stop an experiment.
 * 
 * @author Roland Ewald
 */
public class RunExpAction extends DefaultSwingAction {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6029938165666816946L;

  /** Reference to experiment perspective. */
  private final BaseExperiment exp;

  /** Reference to window manager. */
  private transient final IWindowManager windowManager;

  /** Observation manager to use. */
  private transient final ExperimentObservationManager expObsManager;

  /**
   * Array of experiment execution listeners to be attached to the started
   * experiment.
   */
  private transient final Set<IExperimentExecutionListener> expExecListeners =
      new HashSet<>();

  /** The experiment editor for this experiment. */
  private transient final ExperimentEditor experimentEditor;

  /** The runtime status of the experiment. */
  private ExpStatus expStatus = ExpStatus.DEFAULT;

  /** Thread in which the experiment will be running. */
  private transient ExperimentThread experimentThread;

  /** Execution controller for the experiment. */
  private transient GUIExperimentExecutionController expExecControl;

  /** Overview window for the experiment. */
  private transient ExperimentOverviewWindow expOverviewWindow;

  /**
   * Instantiates a new run exp action.
   * 
   * @param experiment
   *          the experiment
   * @param winManager
   *          the win manager
   * @param expObsMan
   *          the exp obs man
   * @param expExListeners
   *          the exp ex listeners
   * @param expEditor
   *          the exp editor
   */
  public RunExpAction(BaseExperiment experiment, IWindowManager winManager,
      ExperimentObservationManager expObsMan,
      IExperimentExecutionListener[] expExListeners, ExperimentEditor expEditor) {
    super("Run experiment");
    putValue(SHORT_DESCRIPTION, "Run experiment");
    exp = experiment;
    windowManager = winManager;
    expObsManager = expObsMan;
    experimentEditor = expEditor;
    putValue(SMALL_ICON,
        IconManager.getIcon(IconIdentifier.PLAY_SMALL, "Run experiment"));
    for (IExperimentExecutionListener expExecListener : expExListeners) {
      expExecListeners.add(expExecListener);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (experimentEditor != null) {
      experimentEditor.saveToObject();
    }
    synchronized (this) {
      switch (expStatus) {
      case DEFAULT:
        startRun();
        expStatus = ExpStatus.RUNNING;
        putValue(SHORT_DESCRIPTION, "Stop experiment");
        putValue(SMALL_ICON,
            IconManager.getIcon(IconIdentifier.STOP_SMALL, "Stop experiment"));
        break;
      case RUNNING:
        int res =
            JOptionPane.showConfirmDialog(WindowManagerManager
                .getWindowManager().getMainWindow(),
                "Shall all associated simulation runs stop?",
                "Stop running simulation?", JOptionPane.YES_NO_CANCEL_OPTION);
        if (JOptionPane.CANCEL_OPTION == res
            || (JOptionPane.YES_OPTION != res && JOptionPane.NO_OPTION != res)) {
          break;
        }

        boolean stopSims = JOptionPane.YES_OPTION == res;
        exp.stop(stopSims);
        expStatus = ExpStatus.DEFAULT;
        putValue(SHORT_DESCRIPTION, "Start experiment");
        putValue(SMALL_ICON,
            IconManager.getIcon(IconIdentifier.PLAY_SMALL, null));
        break;
      }
    }
  }

  /**
   * Starts experiment execution.
   */
  private void startRun() {
    expExecControl = new GUIExperimentExecutionController(expObsManager);
    if (expOverviewWindow == null) {
      expOverviewWindow = new ExperimentOverviewWindow(exp.getName(), this);
    }
    expExecControl.addExecutionListener(expOverviewWindow);
    for (IExperimentExecutionListener expExecListener : expExecListeners) {
      expExecControl.addExecutionListener(expExecListener);
    }
    windowManager.addWindow(expOverviewWindow);
    exp.setExperimentExecutionController(expExecControl);
    experimentThread = new ExperimentThread(exp);
    ExperimentExecutorThreadPool.getInstance().getExecutor()
        .execute(experimentThread);
  }

  /**
   * Gets the exp obs manager.
   * 
   * @return the exp obs manager
   */
  public ExperimentObservationManager getExpObsManager() {
    return expObsManager;
  }

  /**
   * Gets the exp exec control.
   * 
   * @return the exp exec control
   */
  public GUIExperimentExecutionController getExpExecControl() {
    return expExecControl;
  }

  /**
   * Gets the exp overview window.
   * 
   * @return the exp overview window
   */
  public ExperimentOverviewWindow getExpOverviewWindow() {
    return expOverviewWindow;
  }

  /**
   * Adds the exp exec listener.
   * 
   * @param listener
   *          the listener
   */
  public void addExpExecListener(IExperimentExecutionListener listener) {
    expExecListeners.add(listener);
  }

  /**
   * Removes the exp exec listener.
   * 
   * @param listener
   *          the listener
   */
  public void removeExpExecListener(IExperimentExecutionListener listener) {
    expExecListeners.remove(listener);
    expExecControl.removeExecutionListener(listener);
  }

}

/**
 * Status of the experiment.
 * 
 * @author Roland Ewald
 */
enum ExpStatus {

  /**
   * The default status (experiment is not running).
   */
  DEFAULT,

  /**
   * Experiment is running.
   */
  RUNNING
}