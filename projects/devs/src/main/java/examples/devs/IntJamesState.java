/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
import org.jamesii.core.model.State;

public class IntJamesState extends State {

  static final long serialVersionUID = -6166075005407975922L;

  /**
   * This slot carries a string symbol, textually describing and representing
   * the actual physical state.
   */
  private int phase = 0;

  /**
   * Generates a state without slots phase, beliefs and result set.
   */
  public IntJamesState() {
    this(0);
  }

  /**
   * Generates a state with the given phase and no beliefs and no result.
   */
  public IntJamesState(int phase) {
    super();
    this.phase = phase;
  }

  // --- CLASS-HANDLING --------------------------------------------------


  public int getPhase() {
    return phase;
  }

  // ------AccessorMethoden--------------------------------------------------------


  public void setPhase(int phase) {
    this.phase = phase;
    changed();

  }

  /**
   * Returns a textual description of this state.
   */
  @Override
  public String toString() {
    return ("phase: " + phase);
  }

}
