/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devscore;

import org.jamesii.core.processor.ProcessorState;

/**
 * This class holds the state of a processor. This includes -for a
 * DEVS-Processor the time of last event and the time of next event.
 * 
 * Basically the information contained in here plus the model should suffice for
 * changing the processor during run time.
 * 
 * @author Jan Himmelspach
 */
public class DEVSProcessorState extends ProcessorState {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -1582168888734085775L;

  /** The tole. */
  private double tole;

  /** The tonie. */
  private double tonie;

  /**
   * Get current tole.
   * 
   * @return time of last event
   */
  public double getTole() {
    return tole;
  }

  /**
   * Get current tonie.
   * 
   * @return tonie of this processor
   */
  public double getTonie() {
    return tonie;
  }

  /**
   * Set new Tole
   * 
   * @param newTole
   *          sets the time of last event for this processor
   */
  public void setTole(double newTole) {
    this.tole = newTole;
    changed();
  }

  /**
   * Change tonie.
   * 
   * @param newTonie
   *          the new time of next event for this processor
   */
  public void setTonie(double newTonie) {
    this.tonie = newTonie;
    changed();
  }
}
