/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.listener;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.observe.INotifyingObserver;

/**
 * This is a default observer listener, implemented mainly for testing and
 * debugging reasons.
 * 
 * @author Roland Ewald
 * 
 */
public class DefaultObserverListener implements IObserverListener {

  @Override
  public void init(ComputationTaskRuntimeInformation srti) {
    SimSystem.report(Level.INFO, "Default observer listener was RESET.");
  }

  /**
   * @see org.jamesii.core.observe.listener.IObserverListener#updateOccurred(org.jamesii.core.observe.INotifyingObserver)
   */
  @Override
  public void updateOccurred(INotifyingObserver observer) {
    SimSystem.report(Level.INFO,
        "An update was receved from an observer of type: "
            + observer.getClass().getName() + "\n" + "Observer object: "
            + observer);
  }
}
