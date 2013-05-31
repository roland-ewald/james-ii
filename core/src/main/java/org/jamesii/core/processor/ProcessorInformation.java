/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

import java.io.Serializable;

/**
 * The Class ProcessorInformation.
 */
public class ProcessorInformation implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -3466759398818162936L;

  /** The local. */
  private transient IProcessor local;

  /**
   * Instantiates a new processor information.
   */
  public ProcessorInformation() {
    super();
    local = null;
  }

  /**
   * The Constructor.
   * 
   * @param local
   *          the local
   */
  public ProcessorInformation(IProcessor local) {
    super();
    this.local = local;
  }

  /**
   * Gets the local.
   * 
   * @return the local
   */
  public IProcessor getLocal() {
    return local;
  }

  /**
   * Sets the local.
   * 
   * @param local
   *          the new local
   */
  public void setLocal(IProcessor local) {
    this.local = local;
  }

  @Override
  public String toString() {
    return super.toString() + "\n local:" + local;
  }

}
