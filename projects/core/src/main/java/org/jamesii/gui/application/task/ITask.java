/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import org.jamesii.gui.application.IProgressListener;
import org.jamesii.gui.application.ProgressDialog;

/**
 * This class is supposed to be used for long running task that should run while
 * the UI should still be responsive. Use {@link ProgressDialog#runTask(ITask)}
 * to run this task and have a progress window shown. Or better yet use
 * {@link TaskManager#addTask(ITask)} to have an automatic management system.
 * <p>
 * <b><font color="red">This interface is likely to change in future
 * releases.</font></b>
 * 
 * @author Stefan Rybacki
 */
public interface ITask extends Runnable, IProgress, ITaskInfo {
  /**
   * @return true if this task needs to be done before other tasks can start
   */
  boolean isBlocking();

  /**
   * adds an {@link IProgressListener} to the task
   * 
   * @param l
   *          the listener to attach
   */
  void addProgressListener(IProgressListener l);

  /**
   * Removes a previously attached progress listener
   * 
   * @param l
   *          the listener to remove
   */
  void removeProgressListener(IProgressListener l);

  /**
   * Cancels the task if currently running (this method might also be called for
   * queued tasks that have scheduled but not yet started to execute).<br/>
   * Depending on the implementation of the task processing the task might not
   * be immediately canceled. Implementations of cancel should try to forward
   * (depending on the task implementation) the request to cancel to subsequent
   * code parts (if feasible). The sooner this request can be processed the
   * better the usability of the GUI is.
   */
  void cancel();

  /**
   * @return a name describing this task
   */
  String getName();

  /**
   * @return true if task has finished
   */
  boolean isFinished();

  /**
   * @return true if task has been canceled (isFinished must also return true)
   */
  boolean isCancelled();
}
