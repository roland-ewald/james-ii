/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

import org.jamesii.core.base.Entity;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

/**
 * The Class BasicCoupling. All couplings in DEVS network models have to be
 * implemented based on this class. This class makes the assumption that there
 * is at most one sender per coupling.
 * 
 * @author Jan Himmelspach
 */
public abstract class BasicCoupling extends Entity implements
    Comparable<BasicCoupling> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3448115731700454815L;

  /** One side of this coupling is always fixed (the starting side). */
  private IBasicDEVSModel model1;

  /**
   * The name of the coupling. It is generated automatically for
   * {@link Coupling} and {@link ClassCoupling}. For {@link MultiCoupling} it
   * must be created by the user.
   */
  private String name;

  /** This port must be a port of the model specified in model1 !!!. */
  private IPort port1;

  /**
   * Creates a new coupling object - from the given
   * 
   * 
   * model1, port1 to model2, port2.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   */
  public BasicCoupling(IBasicDEVSModel model1, IPort port1) {
    super();
    this.setModel1(model1);
    this.port1 = port1;
  }

  /**
   * Compares two couplings and returns 0 if the source and target models and
   * ports match 1 if model1 > model2 or (model1 = model) and port1 > port2 -1
   * if model1 < model2 or (model1 = model) and port1 < port2.
   * 
   * @param o
   *          the coupling this coupling shall be compared with
   * 
   * @return the int
   */
  @Override
  public int compareTo(BasicCoupling o) {

    int i = getModel1().compareTo(o.getModel1());

    int k = getPort1().compareTo(o.getPort1());

    if (i == 0) {
      return k;
    }
    return i;

  }

  /**
   * Returns a pointer to the source model.
   * 
   * @return the source model aka model1
   */
  public IBasicDEVSModel getModel1() {
    return model1;
  }

  /**
   * Returns the name of the BasicCoupling.
   * 
   * @return The name of the BasicCoupling.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns a reference to the source port.
   * 
   * @return the source port, also known as port1
   */
  public IPort getPort1() {
    return port1;
  }

  /**
   * Returns true if the.
   * 
   * 
   * 
   * @param model
   *          to be compared with this model
   * 
   * 
   * @return true if the model and this model are identical
   * 
   * @model is the model1 (that's the source) of the coupling
   */
  public boolean isModel1(IBasicDEVSModel model) {
    return (getModel1().getFullName().compareTo(model.getFullName()) == 0);
  }

  /**
   * Returns true if the.
   * 
   * @param modelName
   *          to be compared with this model
   * 
   * @return true if the model and this model are identical
   */
  public boolean isModel1(String modelName) {
    return (getModel1().getFullName().compareTo(modelName) == 0);
  }

  /**
   * Sets the name of a BasicCoupling. This method must be implemented for each
   * class that extends BasicCoupling.
   * 
   * @param name
   *          The name of the BasicCoupling.
   */
  protected final void setName(String name) {
    this.name = name;
  }

  /**
   * @param model1 the model1 to set
   */
  public final void setModel1(IBasicDEVSModel model1) {
    this.model1 = model1;
  }
}
