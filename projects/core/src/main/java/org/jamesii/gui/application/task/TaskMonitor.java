/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jamesii.gui.utils.BasicUtilities;

/**
 * Simple component that shows the progress and task information of the most
 * recent started/or still running task.
 * 
 * @author Stefan Rybacki
 */
public final class TaskMonitor extends JComponent implements
    ITaskManagerListener {

  /** Serialization ID. */
  private static final long serialVersionUID = -3696304587340202039L;

  /** monitored tasks. */
  private final List<ITask> tasks = new ArrayList<>();

  /** progress bar that shows progress information. */
  private final JProgressBar progressBar = new JProgressBar();

  /** label that shows task information. */
  private final JLabel taskInfo = new JLabel();

  /** lock used when writing to or reading from tasks list. */
  private final Lock tasksLock = new ReentrantLock();

  /**
   * flag indicating whether this component shows progress and task information
   * or just displays blank (this is preferred than setVisible, because no
   * revalidation is needed since the component just displays as blank if not
   * needed).
   */
  private boolean hide = true;

  /**
   * Creates the task monitor.
   */
  public TaskMonitor() {
    super();
    TaskManager.addTaskListener(this);

    super.setLayout(new BorderLayout());
    taskInfo.setFont(taskInfo.getFont().deriveFont(Font.PLAIN));
    taskInfo.setHorizontalAlignment(SwingConstants.LEADING);
    taskInfo.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    super.addImpl(taskInfo, BorderLayout.CENTER, 0);

    progressBar.setStringPainted(true);
    progressBar.setString("");
    progressBar.setMaximumSize(progressBar.getPreferredSize());
    progressBar.setPreferredSize(progressBar.getPreferredSize());
    super.addImpl(progressBar, BorderLayout.EAST, 0);
  }

  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    throw new UnsupportedOperationException(
        "You can't add components to this component.");
  }

  @Override
  public void setLayout(LayoutManager mgr) {
    // nothing to do
  }

  @Override
  public void paint(Graphics g) {
    if (!hide) {
      super.paint(g);
    }
  }

  /**
   * Helper method that shortens a string until it fits into maxWidth width
   * while adding "..." when shortened.
   * 
   * @param text
   *          the text to shorten
   * @param fm
   *          the font metrics used to calculate the text width
   * @param maxWidth
   *          maximum allowed width for text
   * 
   * @return shortened text
   */
  private String shortenString(String text, FontMetrics fm, int maxWidth) {
    if (SwingUtilities.computeStringWidth(fm, text) > maxWidth) {
      for (int i = 0; i < text.length(); i++) {
        String temp = text.substring(0, text.length() - i - 1) + "...";
        if (SwingUtilities.computeStringWidth(fm, temp) < maxWidth) {
          return temp;
        }
      }
    } else {
      return text;
    }

    return "";
  }

  /**
   * Helper method that updates progress and task information and is also able
   * to "hide" those information if there is no task running.
   */
  private void updateInfo() {
    tasksLock.lock();
    try {
      ITask currentTask = null;
      if (tasks.size() > 0) {
        currentTask = tasks.get(tasks.size() - 1);
      }

      final ITask task = currentTask;

      BasicUtilities.invokeLaterOnEDT(new Runnable() {
        @Override
        public void run() {
          hide = task == null;
          if (task != null) {
            progressBar.setIndeterminate(true);
            if (task.getProgress() > 0) {
              String info =
                  String.format("%.1f%% %s", Float.valueOf(task.getProgress()
                      * progressBar.getMaximum()), task.getName() == null ? ""
                      : task.getName());

              FontMetrics fm =
                  progressBar.getFontMetrics(progressBar.getFont());
              info = shortenString(info, fm, progressBar.getWidth() - 5);

              progressBar.setString(info);
            } else {
              FontMetrics fm =
                  progressBar.getFontMetrics(progressBar.getFont());
              String info =
                  shortenString(task.getName(), fm, progressBar.getWidth() - 5);

              progressBar.setString(info);
            }
            taskInfo.setText(task.getTaskInfo());
          } else {
            progressBar.setIndeterminate(false);
            taskInfo.setText("");
          }
          revalidate();
          repaint();
        }
      });
    } finally {
      tasksLock.unlock();
    }
  }

  @Override
  public void taskAdded(ITask task) {
    // nothing to do
  }

  @Override
  public void taskFinished(ITask task) {
    tasksLock.lock();
    try {
      tasks.remove(task);
      updateInfo();
    } finally {
      tasksLock.unlock();
    }
  }

  @Override
  public void finished(Object source) {
    // nothing to do
  }

  @Override
  public void progress(Object source, float progress) {
    updateInfo();
  }

  @Override
  public void started(Object source) {
    // nothing to do
  }

  @Override
  public void taskInfo(Object source, String info) {
    updateInfo();
  }

  @Override
  public void taskStarted(final ITask task) {
    tasksLock.lock();
    try {
      tasks.add(task);
      updateInfo();
    } finally {
      tasksLock.unlock();
    }
  }

  /**
   * Checks if is hide.
   * 
   * @return true, if is hide
   */
  public boolean isHide() {
    return hide;
  }

}
