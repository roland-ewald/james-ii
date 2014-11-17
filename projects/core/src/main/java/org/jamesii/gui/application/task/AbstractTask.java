/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import org.jamesii.core.util.collection.ListenerSupport;
import org.jamesii.gui.application.IProgressListener;

/**
 * Base class for {@link ITask} implementations. Extend this class for future
 * implementations of {@link ITask}.
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class AbstractTask implements ITask, IProgressListener {
  /**
   * Listener support
   */
  private final ListenerSupport<IProgressListener> listeners =
      new ListenerSupport<>();

  /**
   * current set task info
   */
  private String taskInfo = null;

  /**
   * current set progress state
   */
  private float progress = -1f;

  /**
   * flag indicating whether task is finished
   */
  private volatile boolean finished = false;

  /**
   * flag indicating whether task has started
   */
  private boolean started = false;

  /**
   * flag indicating whether task has been canceled
   */
  private boolean canceled;

  /**
   * the tasks name
   */
  private String name;

  @Override
  public final void addProgressListener(IProgressListener l) {
    listeners.addListener(l);
  }

  /**
   * Creates an {@link AbstractTask} with no name
   */
  public AbstractTask() {
    this(null);
  }

  /**
   * Creates an {@link AbstractTask} with the given name
   * 
   * @param taskName
   *          the name of the task
   */
  public AbstractTask(String taskName) {
    setName(taskName);
  }

  /**
   * Sets the task's name
   * 
   * @param taskName
   *          the name to set
   */
  protected final void setName(String taskName) {
    name = taskName;
  }

  /**
   * Method used to notify listeners that the task has started
   */
  protected final synchronized void fireStarted() {
    if (started || finished) {
      return;
    }
    started = true;
    for (IProgressListener l : listeners.getListeners()) {
      if (l != null) {
        l.started(this);
      }
    }
  }

  /**
   * Method used to notify listeners that the task has finished
   */
  protected final synchronized void fireFinished() {
    if (finished) {
      return;
    }
    finished = true;
    for (IProgressListener l : listeners.getListeners()) {
      if (l != null) {
        l.finished(this);
      }
    }
  }

  /**
   * Sets the progress attribute and notifies attached listeners
   * 
   * @param progress
   *          the progress to set between 0 and 1
   */
  protected synchronized void setProgress(float progress) {
    if (Float.compare(progress, this.progress) == 0) {
      return;
    }

    this.progress = progress;
    fireProgress();
  }

  /**
   * Sets the current sub-task information and notifies attached listeners
   * 
   * @param taskInfo
   *          the task information to set
   */
  protected synchronized void setTaskInfo(String taskInfo) {
    this.taskInfo = taskInfo;
    fireTaskInfo();
  }

  /**
   * Method used to notify listeners of a progress change
   */
  private synchronized void fireProgress() {
    for (IProgressListener l : listeners.getListeners()) {
      if (l != null) {
        l.progress(this, progress);
      }
    }
  }

  @Override
  public final void cancel() {
    setTaskInfo("Canceling...");
    canceled = true;
    cancelTask();
  }

  /**
   * Implement this method to cancel this task.
   * 
   * @see ITask#cancel()
   */
  protected abstract void cancelTask();

  /**
   * Method used to notify listeners of a task info change
   * 
   * @param info
   *          the new info
   */
  private synchronized void fireTaskInfo() {
    for (IProgressListener l : listeners.getListeners()) {
      if (l != null) {
        l.taskInfo(this, taskInfo);
      }
    }
  }

  @Override
  public boolean isBlocking() {
    return false;
  }

  @Override
  public final void removeProgressListener(IProgressListener l) {
    listeners.removeListener(l);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public final synchronized float getProgress() {
    return progress;
  }

  @Override
  public final synchronized String getTaskInfo() {
    return taskInfo;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean isCancelled() {
    return canceled;
  }

  @Override
  public boolean isFinished() {
    return finished;
  }

  @Override
  public final void run() {
    fireStarted();
    task();
    if (!finished) {
      fireFinished();
    }
  }

  /**
   * Put the actual task in this method. This method will be called by the final
   * {@link #run()} method of the AbstractTask class.<br/>
   * Implementations of this method should take {@link #isCancelled()} into
   * account. The more frequent this is done the more reactive the GUI seems to
   * be. As a general rule isCancelled should at least be checked once per loop
   * iteration, if the {@link #task()} method's implementation contains an own
   * loop.
   */
  protected abstract void task();

  @Override
  public final void finished(Object source) {
    if (source == this) {
      fireFinished();
    }
  }

  @Override
  public void progress(Object source, float p) {
    if (source == this) {
      setProgress(p);
    }
  }

  @Override
  public void started(Object source) {
    if (source == this) {
      fireStarted();
    }
  }

  @Override
  public void taskInfo(Object source, String info) {
    if (source == this) {
      setTaskInfo(info);
    }
  }

}
