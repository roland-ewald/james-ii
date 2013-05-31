/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.actions;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.experiment.dialogs.CreateNewExperimentDialog;
import org.jamesii.gui.experiment.windows.edit.ExperimentEditor;

/**
 * Action to create a new experiment.
 * 
 * @author Roland Ewald
 */
public class NewExperimentAction extends AbstractAction {

  /** The window manager. */
  private final IWindowManager winManager;

  /**
   * Instantiates a new new experiment action.
   * 
   * @param windowManager
   *          the window manager
   * @param window
   *          the window the action is used in
   */
  public NewExperimentAction(IWindowManager windowManager, IWindow window) {
    super("org.jamesii.experiment.new", "Experiment", new String[] {
        "org.jamesii.menu.main/org.jamesii.file/org.jamesii.new",
        "org.jamesii.toolbar.main/org.jamesii.new" }, KeyStroke.getKeyStroke(
        KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())
        .toString(), null, window);
    winManager = windowManager;
  }

  @Override
  public void execute() {
    CreateNewExperimentDialog newExpDialog =
        new CreateNewExperimentDialog(null, null);
    BaseExperiment experiment = newExpDialog.showDialog();
    if (experiment == null) {
      return;
    }
    winManager.addWindow(new ExperimentEditor(experiment, null, null));
  }

}
