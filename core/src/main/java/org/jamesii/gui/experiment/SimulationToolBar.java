/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment;

import org.jamesii.core.processor.IRunnable;

/**
 * Toolbar providing actions to control a simulation.
 * 
 * @author Stefan Leye
 * 
 */
public class SimulationToolBar {

  /** The processor. */
  private IRunnable processor;

  /**
   * Gets the processor.
   * 
   * @return the processor
   */
  public IRunnable getProcessor() {
    return processor;
  }

  /**
   * Sets the processor.
   * 
   * @param processor
   *          the new processor
   */
  public void setProcessor(IRunnable processor) {
    this.processor = processor;
  }

}
