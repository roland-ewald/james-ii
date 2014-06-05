/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

/**
 * The Class Coupling. A typical std DEVS/PDEVS coupling with one sender and one
 * receiver.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public class Coupling extends BasicCoupling {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 3737725134783539526L;

  /** The target-model of this (1:1)Coupling. */
  private IBasicDEVSModel model2;

  /** The target-port of this (1:1)Coupling. */
  private IPort port2;

  /**
   * Creates a new coupling object - from the given.
   * 
   * @param model1
   *          The source-model.
   * @param port1
   *          The source-port.
   * @param model2
   *          The target-model.
   * @param port2
   *          The target-port.
   * 
   * @model1,
   * @port1 to
   * @model2,
   * @port2
   */
  public Coupling(IBasicDEVSModel model1, IPort port1, IBasicDEVSModel model2,
      IPort port2) {
    super(model1, port1);
    this.setModel2(model2);
    this.port2 = port2;
    this.setName(this.toString());
  }

  /**
   * Compares to couplings and returns<br>
   * 0 if the source and target models and ports match<br>
   * 1 if model1 > model2 or (model1 = model) and port1 > port2<br>
   * -1 if model1 < model2 or (model1 = model) and port1 < port2<br>
   * .
   * 
   * @param o
   *          The Object to be compared to.
   * 
   * @return 0 if the source and target models and ports match<br>
   *         1 if model1 > model2 or (model1 = model) and port1 > port2<br>
   *         -1 if model1 < model2 or (model1 = model) and port1 < port2<br>
   */
  @Override
  public int compareTo(BasicCoupling o) {
    if (o instanceof Coupling) {
      int i = getModel1().compareTo(o.getModel1());
      int j = getModel2().compareTo(((Coupling) o).getModel2());
      int k = getPort1().compareTo(((Coupling) o).getPort1());
      int l = getPort2().compareTo(((Coupling) o).getPort2());
      if ((i == j) && (i == k) && (i == l) && (i == 0)) {
        return 0;
      } else if ((i > j) || (i == j) && (k > l)) {
        return 1;
      } else {
        return -1;
      }
    }
    return 0;
  }

  /**
   * Returns a pointer to the drain/target model.
   * 
   * @return A pointer to the target-model.
   */
  public IBasicDEVSModel getModel2() {
    return model2;
  }

  /**
   * Returns a pointer to the drain/target port.
   * 
   * @return A pointer to the target-port.
   */
  public IPort getPort2() {
    return port2;
  }

  /**
   * Returns true if the.
   * 
   * @param model
   *          The model to check if it is the target-model.
   * 
   * @return <code>true</code> if the checked model is the target-model,<br>
   *         <code>false</code> else.
   * 
   * @model is the model2 (that's the drain) of the coupling
   */
  public boolean isModel2(IBasicDEVSModel model) {
    return (getModel2().getFullName().compareTo(model.getFullName()) == 0);
  }

  /**
   * Returns true if the.
   * 
   * @param modelName
   *          the model name
   * 
   * @return <code>true</code> if the checked model is the target-model,<br>
   *         <code>false</code> else.
   * 
   * @model is the model2 (that's the drain) of the coupling
   */
  public boolean isModel2(String modelName) {
    return (getModel2().getFullName().compareTo(modelName) == 0);
  }

  /**
   * Returns the string-representation of the Coupling.
   * 
   * @return The string-representation of the Coupling.
   */
  @Override
  public String toString() {

    StringBuffer buff = new StringBuffer();
    buff.append(getModel1().getName());
    buff.append(":");
    buff.append(getPort1().getName());
    buff.append(" - ");
    buff.append(getModel2().getName());
    buff.append(":");
    buff.append(getPort2().getName());
    return buff.toString();

  }

  /**
   * @param model2 the model2 to set
   */
  public final void setModel2(IBasicDEVSModel model2) {
    this.model2 = model2;
  }

}
