/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

/**
 * Interface for progress informations.
 * 
 * @author Stefan Rybacki
 */
public interface IProgress {
  /**
   * @return the current progress state between 0 and 1 if available, -1 else
   */
  float getProgress();
}
