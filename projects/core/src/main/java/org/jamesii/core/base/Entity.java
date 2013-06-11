/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.base;

import java.io.Serializable;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.ObserverException;

/**
 * Entity is the base class of nearly all entities in the modeling and
 * simulation framework. An entity is observable, and provides an unique
 * identifier for the unique identification of an entity during a run of the
 * framework. Additionally entity provides the basic support for multiple
 * languages.
 * 
 * The few legacy report methods should not be used anymore. Reporting should be
 * done via {@link SimSystem}.
 * 
 * @author Jan Himmelspach
 */
public class Entity implements IEntity, Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 4777595551925059508L;

  /**
   * Report the passed throwable. All "report" methods here are convenience
   * methods for reporting information to the console, the log and wherever
   * else. "Reporting" means that the message to be reported to the user is only
   * reported where the users wants it to be, i.e., a reported message may be
   * printed to the console or it maybe added to the log. <br>
   * 
   * @see org.jamesii.SimSystem#report(Throwable)
   * @see org.jamesii.SimSystem#report(Level, String, String, Object[])
   * 
   * @param e
   *          the throwable to be reported to the user
   */
  @Deprecated
  public static void report(Throwable e) {
    SimSystem.report(e);
  }

  /**
   * Print the passed string. All "report" methods here are convenience methods
   * for reporting information to the console, the log and wherever else.
   * "Reporting" means that the message to be reported to the user is only
   * reported where the users wants it to be, i.e., a reported message may be
   * printed to the console or it maybe added to the log. <br>
   * Here the string is used as message and id. The log level is set to
   * {@link Level#INFO}. The string is ready to be printed (no more placeholders
   * in there). <br>
   * 
   * @see org.jamesii.SimSystem#report(Throwable)
   * @see org.jamesii.SimSystem#report(Level, String, String, Object[])
   * 
   * @param s
   *          - text to be printed.
   */
  @Deprecated
  public static void report(String s) {
    report(s, s);
  }

  /**
   * Prints the message to the given string buffer if possible, and the reports
   * it via {@link Entity#report(String)}.
   * 
   * @param message
   *          the message to be reported
   * @param out
   *          the output buffer
   */
  @Deprecated
  public static void report(String message, StringBuffer out) {
    if (out != null) {
      out.append(message);
    }
    report(message);
  }

  /**
   * Prints the message to the given string buffer if possible, and the reports
   * it via {@link Entity#report(String)}.
   * 
   * @param level
   *          the log level which shall be used to report the information
   * 
   * @param message
   *          the message to be reported
   * @param out
   *          the output buffer
   */
  @Deprecated
  public static void report(Level level, String message, StringBuffer out) {
    if (out != null) {
      out.append(message);
    }
    report(level, message);
  }

  /**
   * Print the passed string. All "report" methods here are convenience methods
   * for reporting information to the console, the log and wherever else.
   * "Reporting" means that the message to be reported to the user is only
   * reported where the users wants it to be, i.e., a reported message may be
   * printed to the console or it maybe added to the log. <br>
   * Here the string is used as message and id. The string is ready to be
   * printed (no more placeholders in there). <br>
   * 
   * @see java.util.logging.Level
   * @see org.jamesii.SimSystem#report(Throwable)
   * @see org.jamesii.SimSystem#report(Level, String, String, Object[])
   * 
   * @param s
   *          - text to be printed.
   * @param level
   *          the log level to be used
   */
  @Deprecated
  public static void report(Level level, String s) {
    report(level, s, s, null);
  }

  /**
   * Print the string s if none equivalent language dependent string with the id
   * id is found. All "report" methods here are convenience methods for
   * reporting information to the console, the log and wherever else.
   * "Reporting" means that the message to be reported to the user is only
   * reported where the users wants it to be, i.e., a reported message may be
   * printed to the console or it maybe added to the log. <br>
   * The log level is set to {@link Level#INFO}. The string is ready to be
   * printed (no more placeholders in there). <br>
   * 
   * @see java.util.logging.Level
   * @see org.jamesii.SimSystem#report(Throwable)
   * @see org.jamesii.SimSystem#report(Level, String, String, Object[])
   * 
   * @param id
   *          the id
   * @param s
   *          the s
   */
  @Deprecated
  public static void report(String id, String s) {
    report(id, s, null);
  }

  /**
   * Print the string s by using the params. If there is a language dependent
   * version of s use this one instead ...
   * 
   * All "report" methods here are convenience methods for reporting information
   * to the console, the log and wherever else. "Reporting" means that the
   * message to be reported to the user is only reported where the users wants
   * it to be, i.e., a reported message may be printed to the console or it
   * maybe added to the log. <br>
   * The log level is set to {@link Level#INFO}. <br>
   * 
   * @see String#format(String, Object...)
   * @see java.util.logging.Level
   * @see org.jamesii.SimSystem#report(Throwable)
   * @see org.jamesii.SimSystem#report(Level, String, String, Object[])
   * 
   * 
   * @param id
   *          the id of a potential translation
   * @param s
   *          the text to be printed
   * @param params
   *          the parameters to be used in the place holders in the message
   */
  @Deprecated
  public static void report(String id, String s, Object[] params) {
    report(Level.INFO, id, s, params);
  }

  /**
   * Print the string s by using the params. If there is a language dependent
   * version of s use this one instead ...
   * 
   * All "report" methods here are convenience methods for reporting information
   * to the console, the log and wherever else. "Reporting" means that the
   * message to be reported to the user is only reported where the users wants
   * it to be, i.e., a reported message may be printed to the console or it
   * maybe added to the log. <br>
   * 
   * @see String#format(String, Object...)
   * @see java.util.logging.Level
   * @see org.jamesii.SimSystem#report(Throwable)
   * @see org.jamesii.SimSystem#report(Level, String, String, Object[])
   * 
   * @param id
   *          the id of a potential translation
   * @param s
   *          the text to be printed
   * @param params
   *          the parameters to be used in the place holders in the message
   * @param level
   *          the log level, only used for feeding the ApplicationLogger to be
   *          used
   */
  @Deprecated
  public static void report(Level level, String id, String s, Object[] params) {
    SimSystem.report(level, id, s, params);
  }

  /** The mediator. */
  private IMediator mediator = null;

  /**
   * Unique ID for each object in the simulation system.
   */
  private long uid = 0;

  /**
   * Default constructor. Auto assigns a unique id to the entity.
   */
  public Entity() {
    super();
    uid = SimSystem.getUId();
  }

  @Override
  public void changed() {
    if (isObserved()) {
      mediator.notifyObservers(this);
    }
  }

  /**
   * Whenever changed is called any attached observer gets updated. This method
   * additionally propagates a "hint" which can contain information about the
   * change. If you do not need this you can simply use the {@link #changed()}
   * method instead.
   * 
   * @param hint
   *          the hint passed together with the change notification.
   */
  @Override
  public void changed(Object hint) {
    if (isObserved()) {
      mediator.notifyObservers(this, hint);
    }
  }

  @Override
  public String getCompleteInfoString() {
    return "";
  }

  @Override
  public IMediator getMediator() {
    return mediator;
  }

  @Override
  public long getSimpleId() {
    return uid;
  }

  /**
   * Checks if is observed.
   * 
   * @return true, if is observed (thus if an mediator is connected)
   */
  public final boolean isObserved() {
    return (mediator != null);
  }

  @Override
  public final void registerObserver(IObserver observer) {
    if (getMediator() == null) {
      throw new ObserverException(
          "You have to set a mediator before you can register an observer!");
    }
    this.getMediator().register(this, observer);
  }

  @Override
  public void setMediator(IMediator mediator) {
    this.mediator = mediator;
  }

  /**
   * Unregister this object at the {@link #getMediator()}. If no mediator is
   * present this method will do nothing.
   * 
   */
  public final void unregister() {
    if (getMediator() != null) {
      this.getMediator().unRegister(this);
    }
  }

  @Override
  public final void unregisterObserver(IObserver observer) {
    this.getMediator().unRegister(this, observer);
  }

  @Override
  public void unregisterObservers() {
    getMediator().unRegister(this);
  }

}
