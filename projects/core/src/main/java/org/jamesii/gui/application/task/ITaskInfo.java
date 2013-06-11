/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

/**
 * Interface providing sub-task information in a human readable way. Which means
 * a task can specify what it is currently doing, hence sub-task information.
 * Example: let the overall task be a copy files task and each task info
 * represents the information of which file is currently copying.
 * 
 * @author Stefan Rybacki
 */
public interface ITaskInfo {
  /**
   * @return information about the currently executed sub-task; null if not
   *         available
   */
  String getTaskInfo();
}
