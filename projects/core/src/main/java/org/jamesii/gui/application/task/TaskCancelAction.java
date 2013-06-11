/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.jamesii.SimSystem;
import org.jamesii.core.util.ICallBack;

/**
 * A simple {@link ITask} canceling action. Use this action e.g., for cancel
 * buttons that are supposed to abort a specific {@link ITask}.
 * 
 * @author Stefan Rybacki
 */
public class TaskCancelAction extends AbstractAction implements
    ICallBack<ITask> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 112441175059110132L;

  /**
   * The task to cancel if action is performed.
   */
  private ITask task;

  /**
   * The timeout in milliseconds after which the user will be asked to force
   * cancellation or to wait for proper cancellation.
   */
  private int timeout;

  /**
   * Instantiates a new task cancel action.
   * 
   * @param text
   *          the actions text if any
   * @param icon
   *          the icon for the action if any
   * @param task
   *          the task to cancel
   * @param timeout
   *          the timeout after clicking cancel after which the user is prompted
   *          for further actions
   */
  public TaskCancelAction(String text, Icon icon, ITask task, int timeout) {
    super(text, icon);
    this.task = task;
    this.timeout = timeout;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    setEnabled(false);
    TaskManager.cancelTask(task, timeout, this);
  }

  @Override
  public boolean process(ITask parameter) {
    if (parameter != task) {
      return false;
    }

    if (parameter.isFinished()) {
      return true;
    }

    if (JOptionPane.showOptionDialog(null,
        "Canceling timed out. What do you want to do!", "Cancellation timeout",
        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
        new Object[] { "Wait"/*
                              * , "Force Cancellation"
                              */}, "Wait") == 1) {
      if (!task.isFinished()) {
        // TODO sr137: get the thread for the given ITask and stop it
        // (and all subsequent threads as well)

        SimSystem.report(Level.WARNING, "Task cancellation failed!");

      }
      return true;
    }

    return false;
  }

}
