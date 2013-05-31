/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Semaphore;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.task.ITask;
import org.jamesii.gui.application.task.ITaskManagerListener;
import org.jamesii.gui.application.task.TaskCancelAction;
import org.jamesii.gui.application.task.TaskManager;
import org.jamesii.gui.application.task.TaskManagerAdapter;
import org.jamesii.gui.decoration.Decorator;
import org.jamesii.gui.decoration.MirrorDecoration;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Basic progress dialog that implements the {@link IProgressListener} interface
 * and therefore can be used for displaying progress information during a
 * running task.
 * 
 * @author Stefan Rybacki
 */
public final class ProgressDialog extends ApplicationDialog implements
    ITaskManagerListener {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -3007172731010002223L;

  /**
   * progress label that can display information sent via
   * {@link #taskInfo(Object, String)}
   */
  private final JLabel progressLabel;

  /**
   * progress bar being undetermined until a progress value is provided via
   * {@link #progress(Object, float)}
   */
  private final JProgressBar progressBar;

  /**
   * Creates a new progress dialog using the given title.
   * 
   * @param title
   *          the dialog's title
   * @param task
   *          the task to execute
   * @param cancelable
   *          if true a cancel button is displayed
   */
  private ProgressDialog(String title, ITask task, boolean cancelable) {
    super(
        WindowManagerManager.getWindowManager() != null ? WindowManagerManager
            .getWindowManager().getMainWindow() : null);

    setModal(true);
    setResizable(false);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setSize(410, 120);
    setLocationRelativeTo(null);
    setTitle(title);

    getContentPane().setLayout(new BorderLayout());

    JPanel progressPanel = new JPanel(new GridBagLayout());

    progressLabel = new JLabel(" ");
    progressLabel.setBorder(new EmptyBorder(2, 5, 2, 5));

    progressBar = new JProgressBar(0, 100);
    progressBar.setPreferredSize(new Dimension(400, 15));
    progressBar.setIndeterminate(true);
    GridBagConstraints c;
    c = new GridBagConstraints();
    c.gridy = 0;
    c.anchor = GridBagConstraints.WEST;
    progressPanel.add(progressLabel, c);
    c = new GridBagConstraints();
    c.gridy = 1;
    progressPanel.add(progressBar, c);

    Decorator deco = new Decorator(progressPanel, new MirrorDecoration(50, 0));
    getContentPane().add(deco, BorderLayout.PAGE_START);

    if (cancelable) {
      JButton cancelButton =
          new JButton(new TaskCancelAction("Cancel", null, task, 10000));

      Box hBox = Box.createHorizontalBox();
      hBox.add(Box.createHorizontalGlue());
      hBox.add(cancelButton);

      hBox.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

      getContentPane().add(hBox, BorderLayout.PAGE_END);
    }

    if (task != null) {
      progress(task, task.getProgress());
      taskInfo(task, task.getTaskInfo());
    }

    // TODO sr137: add cancel option
  }

  @Override
  public void progress(Object sender, final float progress) {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        progressBar.setIndeterminate(progress < 0);
        progressBar.setValue((int) (progress * 100));
      }
    });
  }

  @Override
  public void taskInfo(Object source, final String info) {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        progressLabel.setText(info);
      }
    });
  }

  @Override
  public void finished(Object source) {
  }

  @Override
  public void started(Object source) {
  }

  /**
   * Use this method to run a task observed by the progress dialog. In contrast
   * to {@link #runTaskAndWait(ITask)} this method returns immediately to the
   * caller.
   * 
   * @param task
   *          the task to execute
   */
  public static void runTask(final ITask task) {
    runTask(task, false);
  }

  /**
   * Use this method to run a task observed by the progress dialog. In contrast
   * to {@link #runTaskAndWait(ITask)} this method returns immediately to the
   * caller.
   * 
   * @param task
   *          the task to execute
   * @param cancelable
   *          if true a cancel button is displayed offering the option to cancel
   *          the specified {@link ITask}
   */
  public static void runTask(final ITask task, final boolean cancelable) {

    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        final ProgressDialog d =
            new ProgressDialog(String.format("Running %s...", task.getName()),
                task, cancelable);
        TaskManager.addTaskListener(d);

        final Runnable r = new Runnable() {

          @Override
          public void run() {
            TaskManager.addTaskAndWait(task);
            TaskManager.removeTaskListener(d);
            BasicUtilities.invokeLaterOnEDT(new Runnable() {
              @Override
              public void run() {
                d.setVisible(false);
              }
            });
          }

        };

        d.addWindowListener(new WindowAdapter() {
          @Override
          public void windowOpened(WindowEvent e) {
            new Thread(r).start();
          }
        });
        d.setVisible(true);
      }

    });
  }

  @Override
  public void taskAdded(ITask task1) {
  }

  @Override
  public void taskFinished(ITask task1) {
  }

  @Override
  public void taskStarted(final ITask task1) {
  }

  /**
   * Use this method if you want to run a task and have it observed by the
   * {@link ProgressDialog}. The difference to {@link #runTask(ITask)} is that
   * this method does not return to the called until the supplied task has
   * finished.
   * 
   * @param task
   *          the task to run and wait for
   */
  public static void runTaskAndWait(final ITask task) {
    runTaskAndWait(task, false);
  }

  /**
   * Use this method if you want to run a task and have it observed by the
   * {@link ProgressDialog}. The difference to {@link #runTask(ITask)} is that
   * this method does not return to the called until the supplied task has
   * finished.
   * 
   * @param task
   *          the task to run and wait for
   * @param cancelable
   *          if true a cancel button is displayed offering the option to cancel
   *          the specified {@link ITask}
   */
  public static void runTaskAndWait(final ITask task, final boolean cancelable) {
    final Semaphore waitSemaphore = new Semaphore(0);
    TaskManager.addTaskListener(new TaskManagerAdapter() {
      @Override
      public void taskFinished(ITask t) {
        if (task == t) {
          waitSemaphore.release();
        }
      }
    });

    runTask(task, cancelable);

    try {
      waitSemaphore.acquire();
    } catch (InterruptedException e) {
      SimSystem.report(e);
    }
  }

}
