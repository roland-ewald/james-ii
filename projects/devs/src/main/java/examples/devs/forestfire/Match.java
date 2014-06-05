/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire;

import org.jamesii.core.util.misc.Pair;

/**
 * The Class Match. The match starts the fire.
 */
public class Match extends model.devs.AtomicModel<MatchState> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 8375569156898473017L;

  static final String OutPort = "O";

  Pair<Integer, Integer> pos;

  /**
   * Instantiates a new match.
   * 
   * @param instanceName
   *          the instance name
   */
  Match(String instanceName, Pair<Integer, Integer> pos) {
    super(instanceName);
    this.pos = pos;
    init();
  }

  /**
   * Return the match's state.
   * 
   * @return the match state
   */
  @Override
  protected MatchState createState() {
    return new MatchState(0);
  }

  /**
   * This method should never be called ...
   * 
   * @param elapsedTime
   *          the elapsed time
   */
  @Override
  public void deltaExternal(double elapsedTime) {
    System.out.println("Someone's talking with the match!!!");
  }

  /**
   * After we have initiated the fire the match will be extinguished and will
   * never get active again.
   */
  @Override
  public void deltaInternal() {
    getState().setPhase(MatchState.EXTINGUISHED);
  }

  @Override
  public void init() {
    getState().setPhase(FireState.BURNING);
    addOutPort(OutPort, FireParcel.class);
  }

  /**
   * Initiate the fire.
   */
  @Override
  public void lambda() {

    getOutPort(OutPort).write(new FireParcel(pos, FireState.BURN));

  }

  /**
   * Only get active if we are in the firestate BURNING, this will only happen
   * once!.
   * 
   * @return the double
   */
  @Override
  public double timeAdvance() {
    return (getState().getPhase() == FireState.BURNING) ? 1.0
        : Double.POSITIVE_INFINITY;

  }

}
