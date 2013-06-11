/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.actions;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.ComputationRuntimeState;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.experiment.windows.overview.SimRunTableModel;

/**
 * Action to pause/resume the current simulation.
 * 
 * @author Roland Ewald
 */
public class RunSimAction extends AbstractSimAction {

  /** Serialization ID. */
  private static final long serialVersionUID = -6103415326352681266L;

  private static final String RUN_SIMULATION = "Run simulation";

  /**
   * The weak reference to srtm.
   */
  private final WeakReference<SimRunTableModel> srtmRef;

  /**
   * Instantiates a new run sim action.
   * 
   * @param srtm
   *          the simulation table model
   */
  public RunSimAction(SimRunTableModel srtm) {
    super(RUN_SIMULATION);
    setEnabled(false);
    srtmRef = new WeakReference<>(srtm);
    putValue(SMALL_ICON,
        IconManager.getIcon(IconIdentifier.PAUSE_SMALL, RUN_SIMULATION));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    synchronized (this) {
      ComputationRuntimeState state = getSimRuntimeInfo().getState();
      IComputationTask task = getSimRuntimeInfo().getComputationTask();
      if (task == null) {
        // nothing to control
        return;
      }
      switch (state) {
      case RUNNING:
        if (!simControllingPossible()) {
          SimSystem.report(Level.INFO,
              "Pausing the computation is not possible.");
          break;
        }
        task.pauseProcessor();
        getSimRuntimeInfo().setState(ComputationRuntimeState.PAUSED);
        break;
      case PAUSED:
        if (!simControllingPossible()) {
          SimSystem.report(Level.INFO,
              "Unpausing the computation is not possible.");
          break;
        }
        // Pause twice to restart the simulation
        task.pauseProcessor();
        getSimRuntimeInfo().setState(ComputationRuntimeState.RUNNING);
        break;
      }
      refreshIcon(getSimRuntimeInfo());
      SimRunTableModel model = srtmRef.get();
      if (model != null) {
        model.propagateUpdate(getSimRuntimeInfo());
      }
    }
  }

  @Override
  public void init(ComputationTaskRuntimeInformation srti) {
    super.init(srti);
    refreshIcon(srti);
  }

  @Override
  public void refreshIcon(ComputationTaskRuntimeInformation srti) {
    synchronized (this) {
      ComputationRuntimeState state = srti.getState();
      setEnabled(true);
      switch (state) {
      case RUNNING:
        putValue(SHORT_DESCRIPTION, "Pause simulation");
        putValue(SMALL_ICON,
            IconManager.getIcon(IconIdentifier.PAUSE_SMALL, "Pause simulation"));
        break;
      case PAUSED:
        putValue(SHORT_DESCRIPTION, RUN_SIMULATION);
        putValue(SMALL_ICON,
            IconManager.getIcon(IconIdentifier.PLAY_SMALL, RUN_SIMULATION));
        break;
      default:
        setEnabled(false);
      }
    }
  }
}
