/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devs;

import org.jamesii.core.model.AbstractState;

import model.devscore.BasicAtomicModel;

/**
 * Class which defines the DEVS specific functions of an atomic model, i.e. -
 * the lambda function - the delta functions (int, ext) - as well as the state
 * on which the functions shall operate The functions must be replaced by
 * someone implementing a concrete basic DEVS model, therefore they are defined
 * as abstract methods.
 * 
 * A simulator executing the model class must ensure the correct calling of the
 * methods, no one should call any of the methods directly (this would lead to a
 * non DEVS behaviour.
 * 
 * * @author Jan Himmelspach
 */
public abstract class AtomicModel<S extends AbstractState> extends
    BasicAtomicModel<S> implements IAtomicModel<S> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2449494454741764942L;

  /**
   * Empty Constructor
   * 
   */
  public AtomicModel() {
    super();
  }

  /**
   * Constructor: Creates a new AtomicModel with a state. Override the
   * createState method for creating a more specialized state.
   * 
   * @param name
   *          of the AtomicModel
   * 
   */
  public AtomicModel(String name) {
    super(name);
  }

}
