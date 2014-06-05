/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

import java.io.Serializable;

import model.devscore.IBasicDEVSModel;

/**
 * The Class MultiCouplingTarget.
 * 
 * @author Christian Ober
 */
public class MultiCouplingTarget implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -4767908568880287473L;

  /** The model. */
  private IBasicDEVSModel model;

  /** The portname as String. */
  private String portName;

  /**
   * Creates a new MultiCouplingTarget.
   * 
   * @param model
   *          The given model.
   * @param portName
   *          The given portname.
   */
  public MultiCouplingTarget(IBasicDEVSModel model, String portName) {
    super();
    this.model = model;
    this.portName = portName;
  }

  /**
   * Returns the current model.
   * 
   * @return The model.
   */
  public IBasicDEVSModel getModel() {
    return this.model;
  }

  /**
   * Returns the current portname.
   * 
   * @return The portname.
   */
  public String getPortName() {
    return this.portName;
  }

  /**
   * Sets the new portname.
   * 
   * @param portName
   *          The new portname.
   */
  public void setPortName(String portName) {
    this.portName = portName;
  }
}
