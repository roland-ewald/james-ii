/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Simple log listener for testing purposes. It just saves the last
 * {@link LogRecord}.
 * 
 * Can be used if a systems reaction to failures shall be tested (e.g., is the
 * right error message generated / does the info message appear?).
 * 
 * 
 * @author Roland Ewald
 * 
 */
public class TestingLogListener implements ILogListener {

  /** The last log record. */
  private LogRecord lastLogRecord;

  /** The target level. */
  private final Level targetLevel;

  /**
   * Instantiates a new testing log listener. Will store any log record.
   */
  public TestingLogListener() {
    this(null);
  }

  /**
   * Instantiates a new testing log listener.
   * 
   * @param level
   *          the level of interest
   */
  public TestingLogListener(Level level) {
    this.targetLevel = level;
  }

  @Override
  public void publish(LogRecord record) {
    if (targetLevel == null || (record.getLevel() == targetLevel)) {
      lastLogRecord = record;
    }
  }

  /**
   * Gets the last log record.
   * 
   * @return the last log record
   */
  public LogRecord getLastLogRecord() {
    return lastLogRecord;
  }

  /**
   * Resets the last log record to null.
   */
  public void reset() {
    lastLogRecord = null;
  }

  @Override
  public void flush() {
  }

}
