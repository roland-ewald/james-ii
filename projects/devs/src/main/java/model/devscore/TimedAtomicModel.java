/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;

/**
 * Title: CoSA: AtomicModel Description: Class from which all atomic models that
 * shall support an extra time state variable must inherit from. The simulation
 * time is automatically reflected into the model's state and can be retrieved
 * by using the getTime() method of the state. Copyright: Copyright (c) 2003
 * Company: University of Rostock, Faculty of Computer Science Modeling and
 * Simulation group
 * 
 * @author Jan Himmelspach
 * @version 1.0
 * @param <S>
 *          the type of the state
 */
public abstract class TimedAtomicModel<S extends TimedState> extends
    BasicAtomicModel<S> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 846691460002965913L;

  /**
   * Constructor: Creates a new AtomicModel with a state. Overrride the
   * createState method for creating a more specialized state.
   * 
   * @param name
   *          the model name @
   */
  public TimedAtomicModel(String name) {
    super(name);
  }

  /**
   * If an external event occurs the elapsedTime has to be set as "timeAdvance"
   * value in the timed state. (Because current time is the last time the model
   * got executed + the timeAdvance value - which is now the elapsedTime)
   * 
   * @param elapsedTime
   *          @
   */
  @Override
  public void deltaExternalSim(double elapsedTime) {
    // the old timeAdvance is no longer valid (we have received an external
    // event with a lower time before ...
    // so states getTime method will return the correct value ...
    getState().setTimeAdvance(elapsedTime);
    // call the external simualtion
    super.deltaExternalSim(elapsedTime);
  }

  /**
   * This method is called when a processor for this model gets instantiated.
   * The passes time (simulation start time) is set to the state as first last
   * time value.
   * 
   * @param time
   *          simulation start time @
   */
  @Override
  public void initialization(double time) {
    // do whatever has been implemented in the super class
    super.initialization(time);
    // set the time
    getState().setLastTime(time);
  }

  /**
   * Executes the lamda function @
   */
  public final void lamdaSim() {
    super.lambdaSim();
  }

  /**
   * Calls super implementation and sets the actual simulation time as last time
   * 
   * @return the new timeAdvance @
   */
  @Override
  public final double timeAdvanceSim() {
    double time = super.timeAdvanceSim();
    // set the last time as the actual time
    getState().setLastTime(getState().getTime());
    // set the newly calculated time for the next internal event
    getState().setTimeAdvance(time);
    return time;
  }

}
