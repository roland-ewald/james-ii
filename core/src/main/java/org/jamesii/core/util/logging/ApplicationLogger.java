/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.ListenerSupport;

/**
 * Static accessible class for application wide logging.
 * 
 * @author Stefan Rybacki
 */
public final class ApplicationLogger extends Handler {
  // TODO sr137: introduce log groups, means you can retrieve a unique
  // id from this class and add log entries to that id, this way you
  // can group log messages that belong together

  /**
   * The default max number of entries.
   */
  private static final int DEFAULTMAXENTRIES = 1000;

  /** number of max entries to cache. */
  private static int maxEntries = DEFAULTMAXENTRIES;

  /** logger instance. */
  private final Logger logger = Logger.getLogger("org.jamesii");

  /** Listener support. */
  private static final ListenerSupport<ILogListener> LISTENERS =
      new ListenerSupport<>();

  /** singleton instance of {@link ApplicationLogger}. */
  private static final ApplicationLogger INSTANCE = new ApplicationLogger();

  /** cached log records. */
  private static final List<LogRecord> RECORDS = new ArrayList<>();

  private static final String[] LOGCLASSES = new String[] {
      LogUtils.class.getName(), ApplicationLogger.class.getName(),
      SimSystem.class.getName(), Entity.class.getName() };

  /** Set of class names of loggers; final, but not immutable */
  public static final Set<String> LOG_CLASSES_TO_IGNORE = new HashSet<>(
      Arrays.asList(LOGCLASSES));

  /** Flag to turn on/off reflection based log method caller identification. */
  private static boolean includeCallerInformation = true;

  /**
   * The default console log.
   */
  private static final ConsoleLog CONSOLELOG = new ConsoleLog();

  /**
   * Flag to determine whether the log is enabled or not.
   */
  private static boolean consoleLogEnabled = false;

  static {
    enableConsoleLog();
  }

  /**
   * Hidden constructor due to singleton pattern.
   */
  private ApplicationLogger() {
    // Handler handler;
    logger.addHandler(this);

    logger.setUseParentHandlers(false);

    /*
     * try {
     * 
     * handler = new FileHandler("log_"+logger.getName()+".xml");
     * handler.setFormatter(new XMLFormatter()); logger.addHandler(handler); }
     * catch (SecurityException e) { e.printStackTrace(); } catch (IOException
     * e) { e.printStackTrace(); }
     */
  }

  /**
   * Sets the maximum number of log records to store in memory.
   * 
   * @param max
   *          the maximum number of entries
   */
  public static void setMaxEntries(int max) {
    maxEntries = max;
    checkRecordSize();
  }

  /**
   * Helper method that ensures that the list of records does not exceed
   * {@link #maxEntries}.
   */
  private static void checkRecordSize() {
    if (maxEntries > 0 && RECORDS.size() > maxEntries) {
      int j = RECORDS.size() - maxEntries;
      for (int i = 0; i < j; i++) {
        RECORDS.remove(0);
      }
    }
  }

  /**
   * Sets the level.
   * 
   * @param newLevel
   *          the new level
   */
  public static void setLogLevel(Level newLevel) {
    log (Level.INFO, "Log level has been set to "+newLevel);
    INSTANCE.logger.setLevel(newLevel);
    INSTANCE.setLevel(newLevel);
  }

  /**
   * Gets the log level.
   * 
   * @return the log level
   */
  public static Level getLogLevel() {
    return INSTANCE.getLevel();
  }

  /**
   * Gets the max entries.
   * 
   * @return the maximum number of entries to store in memory
   */
  public static int getMaxEntries() {
    return maxEntries;
  }

  /**
   * Logs the given message using the given {@link Level}.
   * 
   * @param level
   *          the log level
   * @param msg
   *          the message
   */
  public static void log(Level level, String msg) {
    log(level, msg, 0);
  }
 
  
  /**
   * Logs the given message using the given {@link Level}.
   * 
   * @param level
   *          Log level
   * @param msg
   *          Message to log
   * @param levelsUp
   *          Number of stack trace elements to ignore after finding first
   *          non-logger class entries (e.g. when called from another log
   *          function)
   */
  public static void log(Level level, String msg, int levelsUp) {
    StackTraceElement caller = null;
    if (includeCallerInformation) {
      caller = LogUtils.inferCallerClass(LOG_CLASSES_TO_IGNORE, levelsUp);
    }
    if (caller != null) {
      INSTANCE.logger.logp(level, caller.getClassName(), createInfo(caller),
          msg);
    } else {
      INSTANCE.logger.logp(level, null, null, msg);
    }
  }

  /**
   * Creates the information w.r.t. the caller (such as the line number).
   * 
   * @param caller
   *          the caller
   * @return the string
   */
  public static String createInfo(StackTraceElement caller) {
    return caller.getMethodName() + " (Line: " + caller.getLineNumber() + ")";
  }

  /**
   * 
   * @param level
   * @param identifier
   * @param block
   * @param s
   * @param params
   */
  public static void logCreation(Object object, Class<?> type,
      ParameterBlock block, String s) {
    LogRecord log = new ExtendedLogRecord(Level.FINEST, object, type, block, s);
    StackTraceElement caller = null;
    if (includeCallerInformation) {
      caller = LogUtils.inferCallerClass(LOGCLASSES);
      if (caller != null) {
        log.setSourceClassName(caller.getClassName());
        log.setSourceMethodName(createInfo(caller));
      }
    } else {
      log.setSourceClassName(null);
      log.setSourceMethodName(null);
    }    
    INSTANCE.logger.log(log);
  }

  /**
   * Logs the given message using the given {@link Level}.
   * 
   * @param level
   *          the log level
   * @param msg
   *          the message
   * @param thrown
   *          also logs a {@link Throwable} object
   */
  public static void log(Level level, String msg, Throwable thrown) {
    StackTraceElement caller = null;
    if (includeCallerInformation) {
      caller = LogUtils.inferCallerClass(LOGCLASSES);
    }
    if (caller != null) {
      INSTANCE.logger.logp(level, caller.getClassName(), createInfo(caller),
          msg, thrown);
    } else {
      INSTANCE.logger.logp(level, null, null, msg, thrown);
    }

  }

  /**
   * Logs a {@link Throwable} object and takes message from it and sets log
   * {@link Level} to {@link Level#SEVERE}.
   * 
   * @param thrown
   *          the {@link Throwable} object
   */
  public static void log(Throwable thrown) {
    StackTraceElement caller = null;
    if (includeCallerInformation) {
      caller = LogUtils.inferCallerClass(LOGCLASSES);
    }
    if (caller != null) {
      INSTANCE.logger.logp(Level.SEVERE, caller.getClassName(),
          createInfo(caller), thrown.getMessage(), thrown);
    } else {
      INSTANCE.logger.logp(Level.SEVERE, null, null, thrown.getMessage(), thrown);
    }

  }

  /**
   * Adds a listener to the logger.
   * 
   * @param l
   *          the listener to add
   * @param getPreviousRecords
   *          flag indicating whether the newly registered listener also wants
   *          to receive previous records
   */
  public static void addLogListener(ILogListener l, boolean getPreviousRecords) {
    LISTENERS.addListener(l);

    if (l != null && getPreviousRecords) {
      for (LogRecord r : RECORDS) {
        l.publish(r);
      }
    }
  }

  /**
   * Convenience method to {@link #addLogListener(ILogListener, boolean)}.
   * Previous records are not send to newly registered listeners when using this
   * method.
   * 
   * @param l
   *          the listener to add
   */
  public static void addLogListener(ILogListener l) {
    addLogListener(l, false);
  }

  /**
   * Removes a previously registered listener.
   * 
   * @param l
   *          the listener to remove
   */
  public static void removeLogListener(ILogListener l) {
    LISTENERS.removeListener(l);
    // after removing the listener we call the flush method once (to make
    // sure that all buffers are emptied)
    l.flush();
  }

  @Override
  public void close() {
  }

  @Override
  public void flush() {
  }

  @Override
  public void publish(LogRecord record) {
    if (!isLoggable(record)) {
      return;
    }
    RECORDS.add(record);
    checkRecordSize();
    for (ILogListener l : LISTENERS) {
      if (l != null) {
        l.publish(record);
      }
    }
  }

  /**
   * Disable the default console log.
   */
  public static void disableConsoleLog() {
    log (Level.INFO, "Logger has been disabled.");
    if (!consoleLogEnabled) {
      return;
    }
    removeLogListener(CONSOLELOG);
    consoleLogEnabled = false;
  }

  /**
   * Enable the default console log.
   */
  public static void enableConsoleLog() {
    if (consoleLogEnabled) {
      return;
    }
    addLogListener(CONSOLELOG);
    consoleLogEnabled = true;
  }

  /**
   * Reattaches this logger to root logger. Useful in case a third-party library
   * decides to remove all 'competing' logging handlers.
   */
  public static void reattachLogger() {
    INSTANCE.reattachToLogger();
  }

  /**
   * Re-attach this instance to the logger instance as a handler.
   */
  private void reattachToLogger() {
    logger.removeHandler(this);
    logger.addHandler(this);
  }

  /**
   * If true the information about the caller (class, method, loc) will be
   * automatically retrieved and added to each log comment. The detection of
   * this information is costly and the information given will not be of
   * interest for end-users which means that it should be turned off via
   * {@link #setIncludeCallerInformation(boolean)} in these cases.
   * 
   * @return true if caller information is added to every log comment
   */
  public static boolean isIncludeCallerInformation() {
    return includeCallerInformation;
  }

  /**
   * Activate/Deactivate caller information inclusion. Information retrieval is
   * rather expensive and including the information can blow log file sizes.
   * Thus in a normal running setup (for non-developers) this information should
   * not be included.
   * 
   * @param includeCallerInformation
   *          - true to include the caller information, false otherwise
   */
  public static void setIncludeCallerInformation(boolean includeCallerInformation) {
    ApplicationLogger.includeCallerInformation = includeCallerInformation;
  }

}
