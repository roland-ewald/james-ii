/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.actions;

import java.awt.event.ActionEvent;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.ComputationRuntimeState;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;

/**
 * Action to stop the current simulation.
 * 
 * @author Jan Himmelspach
 */
public class StopSimAction extends AbstractSimAction {

  /** Serialization ID. */
  private static final long serialVersionUID = -6103415326352681266L;

  /**
   * Instantiates a new run sim action.
   */
  public StopSimAction() {
    super("Stop simulation");
    setEnabled(false);
    putValue(SMALL_ICON,
        IconManager.getIcon(IconIdentifier.STOP_SMALL, "Stop simulation"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    synchronized (this) {
      ComputationRuntimeState state = getSimRuntimeInfo().getState();

      switch (state) {
      case PAUSED:
      case RUNNING:
        if (!simControllingPossible()) {
          SimSystem.report(Level.INFO,
              "Stopping the simulation is not possible.");
          break;
        }
        if (getSimRuntimeInfo().getComputationTask() != null) {
          getSimRuntimeInfo().getComputationTask().stopProcessor();
          getSimRuntimeInfo().setState(ComputationRuntimeState.CANCELLED);
        }
        break;
      }
      refreshIcon(getSimRuntimeInfo());
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
      case INITIALIZED:
        case PAUSED:
        case RUNNING:
        putValue(SMALL_ICON,
            IconManager.getIcon(IconIdentifier.STOP_SMALL, "Stop"));
        break;
      default:
        setEnabled(false);
      }
    }
  }
}
