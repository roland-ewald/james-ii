/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devs;

import java.util.Collection;

import model.devscore.IBasicDEVSModel;

/**
 * Standard DEVS CoupledModel In standard DEVS a CoupledModel needs an
 * additional select function which determines the next model to be processed
 * (out of the set of imminent models). This select function is defined here as
 * abstract method so that it must be overwritten in descendant classes. For
 * realizing a concrete select function the abstract class SelectFunction (sub
 * package util) can be used.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 * 
 */
public abstract class CoupledModel extends model.devscore.BasicCoupledModel
    implements ICoupledModel {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 659694013491512003L;

  /**
   * Instantiates a new coupled model.
   */
  public CoupledModel() {
    super();

  }

  /**
   * The Constructor.
   * 
   * @param name
   *          the name
   */
  public CoupledModel(String name) {
    super(name);

  }

  /**
   * Select the next imminent model to be executed Modifications to the list of
   * imminents can get lost!! Under some circumstances removing processed models
   * from this list will have no effect. This means that another way of marking
   * selected models is required! For realizing a select function the
   * SelectFunction class of the sub package util can (but need not to) be used.
   * 
   * @param imminents
   *          The (complete list) imminent models for the current time
   * @return one of the models from the imminents list
   */
  @Override
  public abstract IBasicDEVSModel select(Collection<IBasicDEVSModel> imminents);

}
