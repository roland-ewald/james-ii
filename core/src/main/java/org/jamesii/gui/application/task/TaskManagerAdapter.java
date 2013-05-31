/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

/**
 * Simple adapter class for {@link ITaskManagerListener}.
 * 
 * @author Stefan Rybacki
 */
public class TaskManagerAdapter implements ITaskManagerListener {

  @Override
  public void taskAdded(ITask task) {
  }

  @Override
  public void taskFinished(ITask task) {
  }

  @Override
  public void taskStarted(ITask task) {
  }

  @Override
  public void finished(Object source) {
  }

  @Override
  public void progress(Object source, float progress) {
  }

  @Override
  public void started(Object source) {
  }

  @Override
  public void taskInfo(Object source, String info) {
  }

}
