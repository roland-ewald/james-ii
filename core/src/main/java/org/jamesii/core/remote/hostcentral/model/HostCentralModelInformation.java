/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.model;

import org.jamesii.core.model.IModel;
import org.jamesii.core.remote.hostcentral.IObjectId;

/**
 * The class ModelInformation.
 * 
 * @author Simon Bartels
 */
public class HostCentralModelInformation extends
    org.jamesii.core.model.ModelInformation {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -2461156326806334223L;

  /** The remote model reference. */
  private IObjectId ref;

  /**
   * Instantiates a new model information.
   */
  public HostCentralModelInformation() {
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
  public HostCentralModelInformation(IModel local, IObjectId remote) {
    super(local);
    ref = remote;
  }

  /**
   * The Constructor.
   * 
   * @param remote
   *          the remote
   */
  public HostCentralModelInformation(IObjectId remote) {
    super();
    ref = remote;
  }

  /**
   * Gets the remote.
   * 
   * @return the remote
   */
  public IObjectId getRemote() {
    return ref;
  }

  /**
   * Sets the remote model reference.
   * 
   * @param ref
   *          the ref
   */
  public void setModelRef(IObjectId ref) {
    this.ref = ref;
  }

}
