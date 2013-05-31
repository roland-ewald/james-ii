/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.actions;

import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.ComputationRuntimeState;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;

/**
 * Action to move the current simulation forward with a delay during the steps.
 * 
 * @author Stefan Leye
 * 
 */
public class SliderSimAction extends AbstractSimAction {

  /** The default amount of ms between two subsequent simulation steps. */
  public static long DEFAULT_SIMSTEP_DELAY_MS = 50;

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 3478403937357417355L;

  /** The delay the user entered last time. */
  private Long delay = null;

  /**
   * Instantiates a new run sim action.
   */
  public SliderSimAction() {
    super("Adjust delay");
    setEnabled(false);
    putValue(SMALL_ICON,
        IconManager.getIcon(IconIdentifier.CLOCK_SMALL, "Adjust delay"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    synchronized (this) {
      try {
        IProcessor processor =
            getSimRuntimeInfo().getComputationTask().getProcessorInfo()
                .getLocal();
        IRunnable run = null;
        if (processor instanceof IRunnable) {
          run = (IRunnable) processor;
        } else {
          return;
        }
        if (!simControllingPossible()) {
          SimSystem.report(Level.INFO,
              "Computing the computation task stepwise is not possible.");
          return;
        }
        ComputationRuntimeState state = getSimRuntimeInfo().getState();
        if (state == ComputationRuntimeState.RUNNING) {
          run.pause();
        }

        // show input dialog where the user can enter the steps amount to
        // forward
        String slowDown =
            JOptionPane.showInputDialog(
                "Please enter the delay in ms after each computation step.",
                delay == null ? DEFAULT_SIMSTEP_DELAY_MS : delay);

        // TODO fill with the current pause value set in the run object
        if (slowDown == null) {
          if (state == ComputationRuntimeState.RUNNING) {
            run.pause();
          }
          return;
        }

        try {
          delay = new Long(slowDown);
        } catch (Exception ex) {
          SimSystem.report(Level.INFO, "No valid delay given!");
        }
        run.setDelay(delay);
        if (state == ComputationRuntimeState.RUNNING) {
          run.pause();
          // getSimRuntimeInfo().setState(SimulationRuntimeState.RUNNING);
        }
      } catch (Exception e2) {
        return;
      }
    }

  }

  @Override
  public void refreshIcon(ComputationTaskRuntimeInformation srti) {
    synchronized (this) {
      ComputationRuntimeState state = srti.getState();
      switch (state) {
      case PAUSED:
      case RUNNING:
        setEnabled(true);
        break;

      default:
        setEnabled(false);
        break;
      }
    }
  }
}
