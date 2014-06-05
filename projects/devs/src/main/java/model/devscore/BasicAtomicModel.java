/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;

import org.jamesii.core.model.AbstractState;
import org.jamesii.core.model.InvalidModelException;

/**
 * Class which defines the DEVS specific functions of an atomic model, i.e., the
 * lambda function - the delta functions (int, ext) - as well as the state on
 * which the functions shall operate The functions must be replaced by someone
 * implementing a concrete basic DEVS model, therefore they are defined as
 * abstract methods. <br>
 * A simulator executing the model class must ensure the correct calling of the
 * methods, no one should call any of the methods directly (this would lead to a
 * non DEVS behaviour. <br>
 * A simulation algorithm should access this model solely via the
 * {@link IBasicAtomicModel} interface, using the xxxSim methods (
 * {@link #deltaExternalSim(double)}, {@link #deltaInternalSim()}, and
 * {@link #timeAdvanceSim()}). So a modeler can rely on state notification
 * changes, and he has only to focus in the model's functionality.<br>
 * The state of a DEVS model is the only place where model state variables
 * should be. Thus there is no need to add additional attributes to the model's
 * class. This is pretty important because there might be simulation algorithms
 * relying on this - and just working on the states which might lead to an
 * information loss if there are additional attributes in the model class.
 * 
 * @author Jan Himmelspach
 * @param <S>
 *          the type of the state
 */
public abstract class BasicAtomicModel<S extends AbstractState> extends
    BasicDEVSModel implements IBasicAtomicModel<S> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 281935867317344968L;

  /**
   * Each atomic model has its own state. This state is used for generating the
   * model output (within lambda) and is modified from within the delta int and
   * ext (or con) function calls - i.e. it is not allowed to modify the state
   * during lambda. A model should only rely on/use state attributes!!
   */
  private S state;

  /**
   * Default constructor. This constructor creates a state by using the
   * (protected) createState method. This createState method can be easily
   * overriden for creating any (@link State) state
   */
  public BasicAtomicModel() {
    super();
    state = createState();
  }

  /**
   * Constructor: Creates a new AtomicModel with a state. Overrride the
   * createState method for creating a more specialized state.
   * 
   * @param name
   */
  public BasicAtomicModel(String name) {
    super(name);
    state = createState();
  }

  /**
   * This method is called after a model's state has been changed due to an
   * internal, external or confluent state transition.
   */
  public void changedState() {
    // if (isObserved()) {
    // }
  }

  @Override
  public boolean getStateisChangedRR() {
    return getState().isChangedRR();
  }

  /**
   * This method can be overridden in descendant classes for creating different
   * types of state. (more specialized ones)
   * 
   * @return a new state
   */
  protected abstract S createState();

  /**
   * The deltaExternal method is called whenever an external transition has to
   * be computed. This method is supposed to modify the models state.
   * 
   * @param elapsedTime
   */
  protected abstract void deltaExternal(double elapsedTime);

  @Override
  public void deltaExternalSim(double elapsedTime) {
    deltaExternal(elapsedTime);
    changedState();
    changed();
  }

  /**
   * The deltaInternal method is called whenever an internal transition has to
   * be computed. This method is supposed to modify the models state.
   */
  protected abstract void deltaInternal();

  @Override
  public void deltaInternalSim() {
    deltaInternal();
    changedState();
    changed();
  }

  @Override
  public S getState() {
    return state;
  }

  /**
   * <p>
   * The lambda function computes the output of this model BEFORE the state is
   * changed. It is automatically called by the processor during runtime and
   * should not be called manually. It must be implemented in all descendant
   * classes.
   * </p>
   * 
   * <p>
   * Please not that in case of classic DEVS only one output should be created
   * at once. So far, there is no explicit check whether or not more outputs
   * created in one state.
   * </p>
   */
  protected abstract void lambda();

  @Override
  public void lambdaSim() {
    lambda();
  }

  /**
   * Sets the state.
   * 
   * @param state
   *          set the state of the model to this state
   */
  @SuppressWarnings("all")
  public void setState(AbstractState state) {
    this.state = (S) state;
  }

  /**
   * The timeAdvance method must be implemented in any descendant class: It
   * computes the of the next / first time of next internal event.
   * 
   * @return the value of time to advance which is a double value
   */
  public abstract double timeAdvance();

  @Override
  public double timeAdvanceSim() {
    double ta = timeAdvance();
    if (ta < 0) {
      throw new InvalidModelException("The model " + getFullName()
          + " an instance of " + getClass().getName()
          + " has a negative time advance value!!! " + ta);
    }
    return ta;
  }

}
