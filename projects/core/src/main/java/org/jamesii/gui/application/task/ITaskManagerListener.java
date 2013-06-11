/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import org.jamesii.gui.application.IProgressListener;

/**
 * Listener interface that implements a progress listener as well as some
 * methods that are invoked when a task is added to or finished in the task
 * manager. Those additional methods do not rely on a correct implementation of
 * the progress notification mechanism of the {@link ITask}.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface ITaskManagerListener extends IProgressListener {

  /**
   * Invoked when a task is added to the task manager
   * 
   * @param task
   *          the task that is added
   */
  void taskAdded(ITask task);

  /**
   * Invoked when a task has finished either by cancellation or completion
   * 
   * @param task
   *          the finished task
   */
  void taskFinished(ITask task);

  /**
   * Invoked when a task started executing
   * 
   * @param task
   *          the started task
   */
  void taskStarted(ITask task);

}
