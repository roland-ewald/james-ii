/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.logging;

import java.util.logging.LogRecord;

/**
 * The Interface ILogListener.
 * 
 * @author Stefan Rybacki
 */
public interface ILogListener {

  /**
   * Publish the log entry at the object implementing this listener.
   * 
   * @param record
   *          the record
   */
  void publish(LogRecord record);

  /**
   * Depending on the listener it might be necessary to empty buffers etc at
   * latest if the software is about to be shut down. This method will be called
   * automatically if a log is unregistered. However, it might be called from a
   * shut down hook as well.
   */
  void flush();

}
