/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire;

import org.jamesii.core.model.State;

/**
 * The Class FireState.
 */
public class FireState extends State {

  /** burn command constant. */
  public static final int BURN = 100;

  /** The Constant BURNING. */
  public static final int BURNING = 3;

  /** The Constant BURNT_OUT. */
  public static final int BURNT_OUT = 5;

  /** The Constant INACTIVE. */
  public static final int INACTIVE = 6;

  /** The Constant INFERNO. */
  public static final int INFERNO = 4;

  // state const definitions
  /** The Constant INITIALIZING. */
  public static final int INITIALIZING = 0;

  /** The Constant PREPARE_TO_SMOULDER. */
  public static final int PREPARE_TO_SMOULDER = 1;

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -7465327714042216484L;

  /** The Constant SMOULDERING. */
  public static final int SMOULDERING = 2;

  /** The Constant North. */
  public static final int North = 1;

  /** The Constant South. */
  public static final int South = 2;

  /** The Constant East. */
  public static final int East = 4;

  /** The Constant West. */
  public static final int West = 8;

  /**
   * Array containing all possible directions in which we might have a
   * neighbour.
   */
  public static int allDirections[] = { North, East, South, West };

  /** The phase. */
  // protected IntVariable phase = new IntVariable();

  protected int phase;

  /** Neighbours to which we have to spread the fire to. */
  public int spreadFireTo = North + South + East + West;

  /**
   * Create a new fire state with the given initial phase.
   * 
   * @param phase
   *          initial phase of the state, most likely INITIALIZING
   */
  public FireState(int phase) {
    super();
    // this.phase.setValue(phase);
    this.phase = phase;
  }

  /**
   * Gets the phase.
   * 
   * @return the phase
   */
  public int getPhase() {
    // return this.phase.getValue();
    return phase;
  }

  /**
   * Sets the phase.
   * 
   * @param phase
   *          the new phase
   */
  public void setPhase(int phase) {
    // this.phase.setValue(phase);
    this.phase = phase;
  }

}
