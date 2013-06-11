/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

/**
 * Interface for progress listeners.
 * 
 * @author Stefan Rybacki
 * 
 * @see SplashScreen
 * @see IApplication
 * 
 */
public interface IProgressListener {
  /**
   * Called whenever the progress value changed
   * 
   * @param source
   *          the source the progress is coming from (usually the task)
   * @param progress
   *          indicates progress, must be between 0 and 1
   */
  void progress(Object source, float progress);

  /**
   * Called when the task information changed. This is supposed to be used to
   * provide information about the currently executed task
   * 
   * @param source
   *          the source the task information is coming from (usually the task)
   * @param info
   *          information about currently executed task
   */
  void taskInfo(Object source, String info);

  /**
   * Supposed to be called when a task has started.
   * 
   * @param source
   *          the source that started (usually the task)
   */
  void started(Object source);

  /**
   * Supposed to be called when the task has finished
   * 
   * @param source
   *          the source that finished (usually the task)
   */
  void finished(Object source);
}
