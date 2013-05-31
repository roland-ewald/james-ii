/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.model;

import org.jamesii.core.model.IModel;

/**
 * The Class ModelInformation.
 */
public class RMIModelInformation extends
    org.jamesii.core.model.ModelInformation {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -2461156326806334223L;

  /** The remote model reference. */
  private IModelRef ref;

  /**
   * Instantiates a new model information.
   */
  public RMIModelInformation() {
    super();
    ref = null;
  }

  /**
   * The Constructor.
   * 
   * @param local
   *          the local
   * @param remote
   *          the remote
   */
  public RMIModelInformation(IModel local, IModelRef remote) {
    super(local);
    ref = remote;
  }

  /**
   * The Constructor.
   * 
   * @param remote
   *          the remote
   */
  public RMIModelInformation(IModelRef remote) {
    super();
    ref = remote;
  }

  /**
   * Gets the remote.
   * 
   * @return the remote
   */
  public IModelRef getRemote() {
    return ref;
  }

  /**
   * Sets the remote model reference.
   * 
   * @param ref
   *          the ref
   */
  public void setModelRef(IModelRef ref) {
    this.ref = ref;
  }

}
