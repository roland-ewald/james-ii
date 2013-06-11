/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.logging;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * The Class ConsoleLog.
 */
public class ConsoleLog implements ILogListener {

  private SimpleFormatter formatter = new SimpleFormatter();

  @Override
  public void publish(LogRecord record) {
    if (record.getLevel() == Level.SEVERE) {
      System.err.println(formatter.format(record));
    } else {
      System.out.println(formatter.format(record));
    }

    if (record.getThrown() != null) {
      System.err.println(Arrays.toString(record.getThrown().getStackTrace()));
    }

  }

  @Override
  public void flush() {
  }

}
