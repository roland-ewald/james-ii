/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.generator;

import org.jamesii.core.model.State;

/**
 * The state of the {@link Generator}.
 * 
 * @author Alexander Steiniger
 *
 */
public class GeneratorState extends State {

  /**
   * The serialization id
   */
  private static final long serialVersionUID = -2769496571956901567L;
  
  /**
   * The phases of the generator
   */
  public static enum Phase {
    ACTIVE, PASSIVE
  }
  
  /**
   * The phase of the generator
   */
  private Phase phase = Phase.ACTIVE;

  /**
   * Get the value of the phase.
   * @return the phase
   */
  public Phase getPhase() {
    return phase;
  }

  /**
   * Set the phase to the value passed via the phase attribute.
   * @param phase the phase to set
   */
  public void setPhase(Phase phase) {
    this.phase = phase;
  }

}
