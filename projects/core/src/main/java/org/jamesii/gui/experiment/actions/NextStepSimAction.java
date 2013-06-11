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
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;

/**
 * Action to move the current simulation one step forward.
 * 
 * @author Stefan Leye
 */
public class NextStepSimAction extends AbstractSimAction {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = -2212825423013103267L;

  /**
   * Instantiates a new run sim action.
   */
  public NextStepSimAction() {
    super("Next Step");
    setEnabled(false);
    putValue(SMALL_ICON, IconManager.getIcon(IconIdentifier.NEXT_SMALL, "Next"));
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

        IProcessor processor = getProcessor();
        if (processor instanceof IRunnable) {
          ((IRunnable) processor).next(1);
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
