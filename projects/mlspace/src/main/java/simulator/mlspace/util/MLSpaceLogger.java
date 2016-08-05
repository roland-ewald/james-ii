/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.util;

import java.util.Collection;
import java.util.logging.Level;

import org.jamesii.core.util.logging.ApplicationLogger;

import simulator.mlspace.AbstractMLSpaceProcessor;

/**
 * Handler of logging in ML-Space simulations, also containing the settings for
 * what to log and check. Since these settings may differ between simulation
 * runs, each one may have its own "logger" instance, although the actual
 * logging is done by the {@link ApplicationLogger}.
 *
 * @author Arne Bittig
 * @date 10.07.2012
 */
public final class MLSpaceLogger {
  /**
   * Enum holding the various potential log-output-generating criteria
   * 
   * @author Arne Bittig
   */
  public static enum DebugLevel {
    /** Information about start of simulation and, if appropriate, end */
    BASIC_START_AND_END_INFO,
    /** Message when comp is introduced to or removed from event queue */
    COMP_CREATION_AND_DESTRUCTION,
    /** Log effect of event if any rule was applied */
    APPLIED_RULE_INFO,
    /** Log effect of event if it was triggered by a subvol */
    PROCESSED_SV_EVENT_INFO,
    /** Log effect of event if it was triggered by a compartment */
    PROCESSED_COMP_EVENT_INFO,
    /** Message for each comp move attempt (failed or successful) */
    MOVE_ATTEMPT,
    /** Event Queue changes */
    // EVENT_QUEUE_CHANGES, // better handled by event queue decorators
    // /** Check whether all queued NSM events refer to non-empty subvols */
    // NSM_EVENTS_IN_QUEUE_CONSISTENCY,
    /**
     * Correctness check of comp->sv map in hybrid processor (expensive!)
     */
    COMP_MAPS_CORRECTNESS;
  }

  /** Debug level info */
  private final Collection<DebugLevel> debugLevel;

  /** Processor doing the logging (for number of step and sim time) */
  private final AbstractMLSpaceProcessor<?, ?> proc;

  private String simRunID = null;

  /**
   * @param debugLevel
   *          active debug & logging options
   * @param proc
   *          Processor doing the logging (for number of step and sim time)
   */
  public MLSpaceLogger(Collection<DebugLevel> debugLevel,
      AbstractMLSpaceProcessor<?, ?> proc) {
    this.debugLevel = debugLevel;
    this.proc = proc;
    // ApplicationLogger.setLogLevel(Level.FINE);
  }

  /**
   * @param flag
   * @return true iff this {@link DebugLevel} item is activated
   */
  public boolean isDebugLevelSet(DebugLevel flag) {
    return debugLevel.contains(flag);
  }

  /**
   * Set specific debug output
   * 
   * @param dbg
   *          debug output type (see {@link DebugLevel})
   * @param targetVal
   *          new value (true or false, or null to toggle)
   * @return previous value (true or false)
   */
  public boolean setDebugLevel(DebugLevel dbg, Boolean targetVal) {
    boolean oldVal = debugLevel.contains(dbg);
    Boolean newVal = targetVal;
    if (newVal == null) {
      newVal = !oldVal;
    }
    if (newVal) {
      debugLevel.add(dbg);
    } else {
      debugLevel.remove(dbg);
    }
    return oldVal;
  }

  /**
   * Set the sim run ID string to be displayed after all severe messages
   * 
   * @param id
   */
  public void setIDString(String id) {
    this.simRunID = id;
  }

  /**
   * Check whether given debug level setting is activated, and if true, log the
   * given message (preceded by a simulator time string) with the given log
   * level (ignoring this wrapper method in the search for the caller).
   * 
   * @param dbg
   *          Debug setting (null for "log in any case")
   * @param level
   *          Log level
   * @param message
   *          Message to log
   * @return true if message was logged, false if dbg was not activated
   */
  public boolean checkAndLog(DebugLevel dbg, Level level, String message) {
    boolean flag = isDebugLevelSet(dbg);
    if (flag) {
      log(level, message, 2);
    }
    return flag;
  }

  /**
   * Check whether passed boolean value (which is returned back) is true, log
   * given message if so. Example usage: <br>
   * <code>
   * if (checkAndLog(par == null, Level.SEVERE, <br> &nbsp; &nbsp; &nbsp; &nbsp;
   * &nbsp; &nbsp; &nbsp; &nbsp; "Parameter must not be null!")) <br>
   *  &nbsp; &nbsp; return false;
   * </code>
   * 
   * @param flag
   *          Boolean flag whether to do anything at all
   * @param level
   *          Log level
   * @param message
   *          Message to log
   * @return the value of flag
   */
  public boolean checkAndLog(boolean flag, Level level, String message) {
    if (flag) {
      log(level, message, 2);
    }
    return flag;
  }

  /**
   * Log given message.
   * 
   * @param level
   *          Log level
   * @param message
   *          Message to log
   */
  public void log(Level level, String message) {
    log(level, message, 2);
  }

  private void log(Level level, String message, int levelUp) {
    StringBuilder msgStr = buildStepAndTimeStr();
    msgStr.append(message);
    if (level.intValue() >= Level.SEVERE.intValue()) {
      msgStr.append('\n');
      msgStr.append(simRunID);
    }
    ApplicationLogger.log(level, msgStr.toString(), levelUp);
  }

  private StringBuilder buildStepAndTimeStr() {
    StringBuilder stringBuilder = new StringBuilder();
    if (proc != null) {
      stringBuilder.append("Step ");
      stringBuilder.append(proc.getNumOfStep());
      stringBuilder.append("@");
      stringBuilder.append(proc.getTime());
      stringBuilder.append(": ");
    }
    return stringBuilder;
  }

}