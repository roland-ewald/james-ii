/*
 * The general modelling and simulation framework JAMES II.
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
 * Action to move the current simulation n steps forward.
 * 
 * @author Stefan Leye
 */
public class NStepsSimAction extends AbstractSimAction {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = -3385035310057169444L;

  /**
   * Instantiates a new run sim action.
   */
  public NStepsSimAction() {
    super("Next n Steps");
    setEnabled(false);
    putValue(SMALL_ICON,
        IconManager.getIcon(IconIdentifier.FORWARD_SMALL, "Next n"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    synchronized (this) {
      ComputationRuntimeState state = getSimRuntimeInfo().getState();
      switch (state) {
      case RUNNING:
        SimSystem.report(Level.INFO,
            "Pause the simulation to be able to run it stepwise!");
        break;
      case PAUSED:
        if (!simControllingPossible()) {
          SimSystem.report(Level.INFO,
              "Running the simulation stepwise is not possible.");
          break;
        }

        // show input dialog where the user can enter the steps amount to
        // forward
        String steps =
            JOptionPane.showInputDialog(
                "Please enter the steps amount to forward.", 1);
        if (steps == null) {
          return;
        }
        int n = 0;

        try {
          n = Integer.valueOf(steps);
        } catch (Exception ex) {
          SimSystem.report(Level.INFO, "No valid step count given!");
        }

        final IProcessor processor = getProcessor();
        final int c = n;
        if (processor instanceof IRunnable) {
          // calling next from separate thread because this is the EDT and next
          // blocks and therefore blocks the EDT
          // TODO next should block "run" and other "next btns"
          new Thread(new Runnable() {
            @Override
            public void run() {
              ((IRunnable) processor).next(c);
            }
          }).start();
        }
        break;
      }
    }

  }

  @Override
  public void refreshIcon(ComputationTaskRuntimeInformation srti) {
    synchronized (this) {
      ComputationRuntimeState state = srti.getState();
      switch (state) {
      case PAUSED:
        setEnabled(true);
        break;
      default:
        setEnabled(false);
        break;
      }
    }
  }
}
