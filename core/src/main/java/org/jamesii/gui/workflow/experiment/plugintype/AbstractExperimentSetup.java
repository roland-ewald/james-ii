/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.plugintype;

/**
 * Abstract implementation of {@link IExperimentSetup} providing some basic
 * functionality.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractExperimentSetup implements IExperimentSetup {

  /**
   * Fires an event indicating that the can next state changed. This might be
   * due to incomplete filled out forms or non-valid configurations.
   * 
   * @param canNext
   *          the can next state, true if the setup is valid as is
   */
  protected final synchronized void fireCanNext(boolean canNext) {
    throw new IllegalStateException("Nobody to notify yet!");
  }
}
