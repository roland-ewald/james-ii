/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.session;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer task to check upon {@link IExpiring} objects. The task is designed to
 * be executed repeatedly. It'll cancel itself if it isn't needed anymore.
 * 
 * @author Simon Bartels
 * 
 */
public class SessionTimerTask extends TimerTask {

  /**
   * The session timer is responsible for ALL session timer tasks. It tests how
   * long a session has been inactive and discards it if this has been for too
   * long.
   */
  private static final Timer sessionTimer = new Timer(true);

  /**
   * The subject this task shall check upon.
   */
  private IExpiring subject;

  /**
   * Standard constructor.
   * 
   * @param subject
   *          the subject this task shall check upon
   */
  public SessionTimerTask(IExpiring subject, long timeout) {
    super();
    this.subject = subject;
    sessionTimer.schedule(this, timeout, timeout);
  }

  /**
   * Calls {@link IExpiring#hasBeenActive()} and reacts accordingly. If the
   * subject hasn't been active {@link IExpiring#expire()} is called, subject is
   * set to null and this task is canceled. Else nothing will be done.
   * 
   */
  @Override
  public void run() {
    if (!subject.hasBeenActive()) {
      subject.expire();
      subject = null; // for garbage collection
      this.cancel();
    }
  }

}
